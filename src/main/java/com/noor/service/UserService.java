package com.noor.service;

import java.util.List;

import com.noor.exception.UserException;
import com.noor.model.User;


public interface UserService {

	User findUserByEmail(String email) throws UserException;
	
	User getUserProfileByJwt(String jwt) throws UserException;
	
	List<User>getAllUsers();
}
