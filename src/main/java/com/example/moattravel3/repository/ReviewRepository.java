package com.example.moattravel3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel3.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByHouseIdAndIsPublicTrueOrderByCreatedAtDesc(Integer houseId);
	Page<Review> findAll(Pageable pageable);
	List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId); //ユーザー用レビュー閲覧
}
