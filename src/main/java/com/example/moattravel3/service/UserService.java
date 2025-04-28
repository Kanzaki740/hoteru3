package com.example.moattravel3.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moattravel3.entity.Role;
import com.example.moattravel3.entity.User;
import com.example.moattravel3.form.SignupForm;
import com.example.moattravel3.form.UserEditForm;
import com.example.moattravel3.repository.RoleRepository;
import com.example.moattravel3.repository.UserRepository;
import com.example.moattravel3.repository.VerificationTokenRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final VerificationTokenRepository verificationTokenRepository;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			VerificationTokenRepository verificationTokenRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();
		User existingUser = userRepository.findByEmail(signupForm.getEmail());
		Role role = roleRepository.findByName("ROLE_GENERAL");

		if (existingUser != null) {
			if (existingUser.getEnabled()) {
				// 有効なユーザーなら新規登録NG
				throw new IllegalStateException("すでに登録済みのメールアドレスです。");
			} else {
				// 無効なユーザーを上書きして再利用する
				existingUser.setName(signupForm.getName());
				existingUser.setFurigana(signupForm.getFurigana());
				existingUser.setPostalCode(signupForm.getPostalCode());
				existingUser.setAddress(signupForm.getAddress());
				existingUser.setPhoneNumber(signupForm.getPhoneNumber());
				existingUser.setPassword(passwordEncoder.encode(signupForm.getPassword()));
				existingUser.setEnabled(false);

				User savedUser = userRepository.save(existingUser);

				// 古いVerificationTokenを削除する
				verificationTokenRepository.deleteByUser(savedUser);

				return savedUser;
			}
		} else {
			// 新規ユーザー登録
			user.setName(signupForm.getName());
			user.setFurigana(signupForm.getFurigana());
			user.setPostalCode(signupForm.getPostalCode());
			user.setAddress(signupForm.getAddress());
			user.setPhoneNumber(signupForm.getPhoneNumber());
			user.setEmail(signupForm.getEmail());
			user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
			user.setRole(role);
			user.setEnabled(false);

			return userRepository.save(user);
		}
	}

	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());

		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());

		userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmailAndEnabled(email, true);
		return user != null;
	}

	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	// メールアドレスが変更されたかどうかをチェックする
	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	//ユーザーを無効にする
	@Transactional
	public void withdrawUser(Integer userId) {
		User user = userRepository.getReferenceById(userId);
		user.setEnabled(false); // 論理削除
		userRepository.save(user);
	}

	//メール再送用
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
