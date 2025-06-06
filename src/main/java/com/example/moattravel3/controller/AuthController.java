package com.example.moattravel3.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel3.entity.User;
import com.example.moattravel3.entity.VerificationToken;
import com.example.moattravel3.event.SignupEventPublisher;
import com.example.moattravel3.form.SignupForm;
import com.example.moattravel3.security.UserDetailsImpl;
import com.example.moattravel3.service.UserService;
import com.example.moattravel3.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;

	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher,
			VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
	}

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "auth/signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
		// メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailRegistered(signupForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		// パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
		if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "auth/signup";
		}
		
		try {
	        userService.create(signupForm);
	    } catch (IllegalStateException e) {
	        bindingResult.rejectValue("email", "signup.email", e.getMessage());
	    }

	    if (bindingResult.hasErrors()) {
	        return "signup"; // フォーム画面に戻す
	    }

		//userService.create(signupForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");
		User createdUser = userService.create(signupForm);
		String requestUrl = new String(httpServletRequest.getRequestURL());
		signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
		redirectAttributes.addFlashAttribute("successMessage",
				"ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");

		return "redirect:/";
	}
	
	//認証メールの再送
	@PostMapping("/signup/resend")
	public String resendVerificationEmail(@RequestParam String email, RedirectAttributes redirectAttributes, HttpServletRequest request) {
	    User user = userService.findByEmail(email);

	    if (user != null && !user.getEnabled()) {
	        String requestUrl = request.getRequestURL().toString();
	        signupEventPublisher.publishSignupEvent(user, requestUrl);

	        redirectAttributes.addFlashAttribute("successMessage", "認証メールを再送しました。メールをご確認ください。");
	    } else {
	        redirectAttributes.addFlashAttribute("errorMessage", "認証済みのアカウント、または存在しないメールアドレスです。");
	    }

	    return "redirect:/";
	}


	@GetMapping("/signup/verify")
	public String verify(@RequestParam(name = "token") String token, Model model) {
		VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

		if (verificationToken != null) {
			User user = verificationToken.getUser();
			userService.enableUser(user);
			String successMessage = "会員登録が完了しました。";
			model.addAttribute("successMessage", successMessage);
		} else {
			String errorMessage = "トークンが無効です。";
			model.addAttribute("errorMessage", errorMessage);
		}

		return "auth/verify";
	}

	@PostMapping("/user/withdraw")
	public String withdraw(RedirectAttributes redirectAttributes, Authentication authentication) {
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		User user = userDetails.getUser();
		userService.withdrawUser(user.getId());
		redirectAttributes.addFlashAttribute("successMessage", "退会処理が完了しました。");
		return "redirect:/?loggedout";
	}

}
