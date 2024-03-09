package com.noor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noor.config.JwtProvider;
import com.noor.exception.UserException;
import com.noor.model.User;
import com.noor.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findUserByEmail(String email) throws UserException {
		
		User user=userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("User not found with email "+email);
		}
		
		return user;
	}

	@Override
	public User getUserProfileByJwt(String jwt) throws UserException {
		
		String email=JwtProvider.getEmailFromJwtToken(jwt);
		
		User user=userRepository.findByEmail(email);
		
		if(user==null) {
			throw new UserException("User not found with "+email);
		}
		
		return user;
	}

	@Override
	public List<User> getAllUsers() {
		
		return userRepository.findAll();
	}

}
