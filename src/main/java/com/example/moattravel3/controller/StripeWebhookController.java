package com.example.moattravel3.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.moattravel3.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {
	private final StripeService stripeService;

	@Value("${stripe.api-key}")
	private String stripeApiKey;

	@Value("${stripe.webhook-secret}")
	private String webhookSecret;

	public StripeWebhookController(StripeService stripeService) {
		this.stripeService = stripeService;
	}

	@PostMapping("/stripe/webhook")
	public ResponseEntity<String> webhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {
		System.out.println("Stripe Webhookエンドポイントに到達しました");
		Stripe.apiKey = stripeApiKey;
		Event event = null;

		try {
			event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
		} catch (SignatureVerificationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		if ("checkout.session.completed".equals(event.getType())) {
			stripeService.processSessionCompleted(event);
		}

		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	//テスト
	@GetMapping("/stripe/test")
	public ResponseEntity<String> test() {
		System.out.println("★ /stripe/test 呼び出し");
		return ResponseEntity.ok("ok");
	}
}
