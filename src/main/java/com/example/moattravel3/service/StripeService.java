package com.example.moattravel3.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.moattravel3.entity.House;
import com.example.moattravel3.entity.User;
import com.example.moattravel3.form.ReservationRegisterForm;
import com.example.moattravel3.repository.HouseRepository;
import com.example.moattravel3.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
	private String stripeApiKey;
	private final ReservationService reservationService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private MailService mailService;

	public StripeService(ReservationService reservationService) {

		this.reservationService = reservationService;

	}

	// セッションを作成し、Stripeに必要な情報を返す
	public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm,
			HttpServletRequest httpServletRequest) {
		Stripe.apiKey = stripeApiKey;
		String requestUrl = new String(httpServletRequest.getRequestURL());
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
												.setProductData(
														SessionCreateParams.LineItem.PriceData.ProductData.builder()
																.setName(houseName)
																.build())
												.setUnitAmount((long) reservationRegisterForm.getAmount())
												.setCurrency("jpy")
												.build())
								.setQuantity(1L)
								.build())
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(
						requestUrl.replaceAll("/houses/[0-9]+/reservations/confirm", "") + "/reservations?reserved")
				.setCancelUrl(requestUrl.replace("/reservations/confirm", ""))
				.setPaymentIntentData(
						SessionCreateParams.PaymentIntentData.builder()
								.putMetadata("houseId", reservationRegisterForm.getHouseId().toString())
								.putMetadata("userId", reservationRegisterForm.getUserId().toString())
								.putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())
								.putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())
								.putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())
								.putMetadata("amount", reservationRegisterForm.getAmount().toString())
								.build())
				.build();
		try {
			Session session = Session.create(params);
			return session.getId();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}

	// セッションから予約情報を取得し、ReservationServiceクラスを介してデータベースに登録する
	@Transactional
	public void processSessionCompleted(Event event) {

		System.out.println("処理開始：checkout.session.completed");

		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();

		String sessionId = null;
		if (optionalStripeObject.isPresent()) {
			System.out.println("デシリアライズ成功：Session情報を取得しました");
			Session session = (Session) optionalStripeObject.get();
			sessionId = session.getId();
		} else {
			System.out.println("デシリアライズ失敗：Session情報が空です");
			// セッションIDをJSONから手動で取り出す
			String rawJson = event.getDataObjectDeserializer().getRawJson();
			System.out.println("RAW JSON: " + rawJson);

			// JSONから "id" を抽出
			int idIndex = rawJson.indexOf("\"id\":\"");
			if (idIndex != -1) {
				int start = idIndex + 6;
				int end = rawJson.indexOf("\"", start);
				sessionId = rawJson.substring(start, end);
				System.out.println("抽出されたセッションID: " + sessionId);
			}
		}

		if (sessionId != null) {
			System.out.println("デシリアライズ失敗：Session情報が空です");
			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

			try {
				Stripe.apiKey = stripeApiKey;
				Session session = Session.retrieve(sessionId, params, null);
				Map<String, String> metadata = session.getPaymentIntentObject().getMetadata();
				System.out.println("取得したメタデータ: " + metadata);

				reservationService.create(metadata);

				//予約完了メールを送信
				Integer houseId = Integer.parseInt(metadata.get("houseId"));
				Integer userId = Integer.parseInt(metadata.get("userId"));
				Integer numberOfPeople = Integer.parseInt(metadata.get("numberOfPeople"));
				Integer amount = Integer.parseInt(metadata.get("amount"));
				String checkinDate = metadata.get("checkinDate");
				String checkoutDate = metadata.get("checkoutDate");

				User user = userRepository.getReferenceById(userId);
				House house = houseRepository.getReferenceById(houseId);

				String subject = "【Moat Travel】予約が完了しました";
				String body = String.format("""
						%s 様

						以下の内容でご予約が完了しました：

						■ 宿泊施設：%s
						■ 宿泊日：%s ～ %s
						■ 宿泊人数：%d名
						■ 合計金額：%,d円

						Moat Travelをご利用いただきありがとうございます。
						～連絡先とか～
						""",
						user.getName(), house.getName(), checkinDate, checkoutDate, numberOfPeople, amount);

				mailService.sendReservationConfirmation(user.getEmail(), subject, body);
				System.out.println("予約完了メールを送信しました。");
			} catch (StripeException e) {
				System.err.println("セッション再取得エラー");
				e.printStackTrace();
			}
		}

	}
}