package com.example.moattravel3.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moattravel3.entity.User;
import com.example.moattravel3.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserRepository userRepository;

	public AdminUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(required = false, defaultValue = "false") boolean showDisabled,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<User> userPage;

		if (keyword != null && !keyword.isEmpty()) {
		    if (showDisabled) {
		        userPage = userRepository.findByNameOrFuriganaContaining(keyword, pageable);
		    } else {
		        userPage = userRepository.findByNameOrFuriganaContainingAndEnabledTrue(keyword, pageable);
		    }
		}else {
	        if (showDisabled) {
	            userPage = userRepository.findAll(pageable);
	        } else {
	            userPage = userRepository.findByEnabledTrue(pageable);
	        }
	    }

		model.addAttribute("userPage", userPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("showDisabled", showDisabled);

		return "admin/users/index";
	}
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        User user = userRepository.getReferenceById(id);
        
        model.addAttribute("user", user);
        
        return "admin/users/show";
    }  
}
