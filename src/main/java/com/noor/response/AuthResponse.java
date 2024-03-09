package com.noor.response;

import com.noor.model.USER_ROLE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String jwt;
	
	private String message;
	
	private Boolean status;
	
	private USER_ROLE role;
}
