package com.example.moattravel3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.moattravel3.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmail(String email);

	public User findByEmailAndEnabled(String email, boolean enabled);

	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);

	Page<User> findByEnabledTrue(Pageable pageable);

	@Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.furigana LIKE %:keyword%)")
	Page<User> findByNameOrFuriganaContaining(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.furigana LIKE %:keyword%) AND u.enabled = true")
	Page<User> findByNameOrFuriganaContainingAndEnabledTrue(@Param("keyword") String keyword, Pageable pageable);

}
