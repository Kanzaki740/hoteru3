package com.example.moattravel3.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel3.entity.Review;
import com.example.moattravel3.entity.User;
import com.example.moattravel3.repository.ReviewRepository;
import com.example.moattravel3.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class UserReviewController {

    private final ReviewRepository reviewRepository;

    //レビュー一覧表示
    @GetMapping
    public String listUserReviews(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userDetails.getUser();
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("reviews", reviews);
        return "reviews/index";
    }
    
    //更新
    @GetMapping("/{id}/edit")
    public String editReviewForm(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("レビューが見つかりません"));
        if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "このレビューを編集する権限がありません。");
            return "redirect:/reviews";
        }
        model.addAttribute("review", review);
        return "reviews/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateReview(@PathVariable Long id,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("レビューが見つかりません"));
        if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "このレビューを更新する権限がありません。");
            return "redirect:/reviews";
        }
        review.setRating(rating);
        review.setComment(comment);
        review.setUpdatedAt(java.time.LocalDateTime.now());
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました。");
        return "redirect:/reviews";
    }


    //レビュー削除
    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               RedirectAttributes redirectAttributes) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("レビューが見つかりません"));

        //自分のレビューかどうか確認
        if (!review.getUser().getId().equals(userDetails.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "このレビューを削除する権限がありません。");
            return "redirect:/reviews";
        }

        reviewRepository.delete(review); //完全削除
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
        return "redirect:/reviews";
    }
}
