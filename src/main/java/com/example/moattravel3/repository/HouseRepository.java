package com.example.moattravel3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.moattravel3.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {
	public Page<House> findByNameLike(String keyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<House> findByAddressLike(String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqual(Integer price, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);

	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);

	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);

	public List<House> findTop10ByOrderByCreatedAtDesc();
	
	//キーワード(名前、場所、詳細)検索
	public Page<House> findByNameLikeOrAddressLikeOrDescriptionLike(
			String nameKeyword, String addressKeyword, String descriptionKeyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrDescriptionLikeOrderByPriceAsc(
			String nameKeyword, String addressKeyword, String descriptionKeyword, Pageable pageable);

	public Page<House> findByNameLikeOrAddressLikeOrDescriptionLikeOrderByCreatedAtDesc(
			String nameKeyword, String addressKeyword, String descriptionKeyword, Pageable pageable);

}
