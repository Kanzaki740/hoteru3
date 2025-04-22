package com.example.moattravel3.form;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
 @Data
public class ReservationInputForm {
 	@NotBlank(message = "チェックイン日とチェックアウト日を選択してください。")
 	private String fromCheckinDateToCheckoutDate;    
	 
 	@NotNull(message = "宿泊人数を入力してください。")
 	@Min(value = 1, message = "宿泊人数は1人以上に設定してください。")
 	private Integer numberOfPeople;
 	// チェックイン日を取得する
 	public LocalDate getCheckinDate() {
 		String[] parts = getFromCheckinDateToCheckoutDate().split(" から ");
 	    if (parts.length >= 1) {
 	        return LocalDate.parse(parts[0]);
 	    }
 	    return null; 
 	}
 	// チェックアウト日を取得する
 	public LocalDate getCheckoutDate() {
 		String[] parts = getFromCheckinDateToCheckoutDate().split(" から ");
 	    if (parts.length >= 2) {
 	        return LocalDate.parse(parts[1]);
 	    }
 	    return null;
 	}
}

