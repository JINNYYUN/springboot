package com.jinny.playingspringboot.config.auth;

import com.jinny.playingspringboot.model.User;
import com.jinny.playingspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//시큐리티 설정에서 loginProcessingUrl("/loginProc");
//login요청이 오면 자동으로 UserDetailsService타입으로 IOC되어 있는 loadUserByUsername함수가 실행
@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if(user == null) { //찾지 못했으면
			return null;
		}
		return new PrincipalDetails(user); //리턴된 값이 Authentication내부에 들어가고, 이 Authentication객체는 시큐리티 session에 들ㅓ감.
	}
}
