package com.example.moattravel3.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moattravel3.entity.Review;
import com.example.moattravel3.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

	private final ReviewRepository reviewRepository;

	@GetMapping
	public String listReviews(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			Model model) {

		Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, 10, sort);
		Page<Review> reviewPage = reviewRepository.findAll(pageable);

		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("direction", direction);
		return "admin/reviews/index";
	}
}
