package com.noor.controller;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noor.config.JwtProvider;
import com.noor.exception.UserException;
import com.noor.model.USER_ROLE;
import com.noor.model.User;
import com.noor.repository.UserRepository;
import com.noor.request.LoginRequest;
import com.noor.response.AuthResponse;
import com.noor.service.CustomUserServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserServiceImpl customUserServiceImpl;
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse>createUserHandler(
			@RequestBody User user) throws UserException{
			
		User isEmailExist=userRepository.findByEmail(user.getEmail());
		
		if(isEmailExist!=null) {
			throw new UserException("email is already registered");
		}
		
		//create user
		User createUser=new User();
		createUser.setEmail(user.getEmail());
		createUser.setPassword(passwordEncoder.encode(user.getPassword()));
		createUser.setName(user.getName());
		createUser.setRole(user.getRole());
		createUser.setMobile(user.getMobile());
		
		User savedUser=userRepository.save(createUser);
		
		
		//Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
		Authentication authentication=new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token=jwtProvider.generateToken(authentication);
		
		AuthResponse authResponse=new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Registration success");
		authResponse.setStatus(true);
		authResponse.setRole(savedUser.getRole());
				
		return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.CREATED);
		
	}
	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse>loginUserHandler(
			@RequestBody LoginRequest loginRequest) throws UserException{
	
		String userName=loginRequest.getEmail();
		String password=loginRequest.getPassword();
		
		Authentication authentication=authenticate(userName,password);
		
		Collection<? extends GrantedAuthority>authorities=authentication.getAuthorities();
		String role=authorities.isEmpty()?null:authorities.iterator().next().getAuthority();
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	
		String token=jwtProvider.generateToken(authentication);
		
		AuthResponse authResponse=new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Signin Success");
		authResponse.setStatus(true);
		authResponse.setRole(USER_ROLE.valueOf(role));
		
		return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);

	}
	
	private Authentication authenticate(String userName,String password) {
		UserDetails userDetails=customUserServiceImpl.loadUserByUsername(userName);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid UserName...");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid Password...");
		}
		return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
	}
}