package com.noor.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noor.model.USER_ROLE;
import com.noor.model.User;
import com.noor.repository.UserRepository;

@Service
public class CustomUserServiceImpl implements UserDetailsService{

	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user=userRepository.findByEmail(username);
		if(user==null) {
			throw new UsernameNotFoundException("user not found with email "+username);
		}
		
		USER_ROLE role=user.getRole();
		
		if(role==null)role=USER_ROLE.ROLE_VOTER;
		List<GrantedAuthority>authorities=new ArrayList<>(); 
		
		authorities.add(new SimpleGrantedAuthority(role.toString()));
		
//		authorities= Arrays.stream(user.getRole().split(","))
//        		//create new object
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);
	}

}
