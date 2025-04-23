package com.example.moattravel3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
	@GetMapping("/test/index")
	public String index() {
		return "test/index";
	}

}
