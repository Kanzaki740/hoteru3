package com.example.moattravel3.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel3.entity.House;
import com.example.moattravel3.entity.Review;
import com.example.moattravel3.entity.User;
import com.example.moattravel3.repository.HouseRepository;
import com.example.moattravel3.repository.ReviewRepository;
import com.example.moattravel3.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final HouseRepository houseRepository;

    @PostMapping
    public String submitReview(@RequestParam Integer houseId,
                                @RequestParam int rating,
                                @RequestParam String comment,
                                @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                RedirectAttributes redirectAttributes) {

        // 宿情報とユーザー情報の取得
        House house = houseRepository.findById(houseId).orElseThrow();
        User user = userDetailsImpl.getUser();

        // レビューエンティティ作成
        Review review = Review.builder()
                .house(house)
                .user(user)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .isPublic(true)
                .build();

        // 保存
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");

        // 民宿詳細画面などにリダイレクト
        return "redirect:/houses/" + houseId + "?reviewSuccess";
    }
}
