package com.example.moattravel3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel3.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmail(String email);

	public User findByEmailAndEnabled(String email, boolean enabled);

	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}
