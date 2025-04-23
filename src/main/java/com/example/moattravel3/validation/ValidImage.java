package com.example.moattravel3.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
	String message() default "画像ファイル（JPEG, PNG）を5MB以内で選択してください。";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
