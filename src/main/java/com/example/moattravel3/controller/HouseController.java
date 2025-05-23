package com.example.moattravel3.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.moattravel3.entity.House;
import com.example.moattravel3.entity.Review;
import com.example.moattravel3.form.ReservationInputForm;
import com.example.moattravel3.repository.HouseRepository;
import com.example.moattravel3.repository.ReviewRepository;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final ReviewRepository reviewRepository;

	public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository) {
		this.houseRepository = houseRepository;
		this.reviewRepository = reviewRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		
		Pageable customPageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {
			String kw = "%" + keyword + "%";
			housePage = houseRepository.findByNameLikeOrAddressLike(kw, kw, customPageable);
			if (order != null && order.equals("priceAsc")) {
		        housePage = houseRepository
		            .findByNameLikeOrAddressLikeOrDescriptionLikeOrderByPriceAsc(kw, kw, kw, customPageable);
		    } else {
		        housePage = houseRepository
		            .findByNameLikeOrAddressLikeOrDescriptionLikeOrderByCreatedAtDesc(kw, kw, kw, customPageable);
		    }
		} else if (area != null && !area.isEmpty()) {
			housePage = houseRepository.findByAddressLike("%" + area + "%", customPageable);
			if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", customPageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", customPageable);
            }
		} else if (price != null) {
			housePage = houseRepository.findByPriceLessThanEqual(price, customPageable);
			if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, customPageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, customPageable);
            }
		} else {
			housePage = houseRepository.findAll(customPageable);
			if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(customPageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(customPageable);   
            }
		}
 
		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		model.addAttribute("size", size);

		return "houses/index";
	}
	
	@GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        House house = houseRepository.getReferenceById(id);
        List<Review> reviews = reviewRepository.findByHouseIdAndIsPublicTrueOrderByCreatedAtDesc(id);
        
        model.addAttribute("house", house);  
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reviews", reviews);
        
        return "houses/show";
    }

}