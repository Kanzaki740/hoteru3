package com.example.moattravel3.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if (file == null || file.isEmpty()) {
			return true;
		}
		String contentType = file.getContentType();
		long sizeInBytes = file.getSize();
		// 許可する拡張子: JPG, JPEG, PNG
		boolean validType = contentType != null
				&& (contentType.equals("image/jpeg") || contentType.equals("image/png"));
		// サイズ5MB以内
		boolean validSize = sizeInBytes <= 5 * 1024 * 1024;
		return validType && validSize;
	}
}
