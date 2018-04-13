package com.faint.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.faint.domain.Authority;
import com.faint.domain.UserVO;
import com.faint.domain.UsersException;
import com.faint.dto.LoginDTO;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService service;

	@Override
	public UserDetails loadUserByUsername(String email) 
			throws UsernameNotFoundException {
		
		System.out.println("loadUserByUsername");
		
		try {
			/*
			 * 로그인할 때 클라이언트로부터 전달받은 아이디(email) 값을 넘겨 받아
			 * 해당 사용자의 정보를 불러온다.
			 */
			System.out.println("loadUserByUsername & email: "+email);
			UserVO users = service.detailByEmail(email);
			System.out.println("야야야야ㅑ야야야ㅑ111 ");
			
			
			/*
			 *  해당 사용자가 존재하지 않으면
			 *  이미 정의된 UsernameNotFoundException을 이용하여
			 *  예외를 생성해서 던져주면 스프링이 알아서 예외처리를 하게 된다. 
			 */
			
			
		
			if (users == null) {
			System.out.println("UsernameNotFoundException 부분");
				throw new UsernameNotFoundException("해당 사용자를 찾지 못했습니다.");
				
			}

			System.out.println("userDetailService의 userVO값: "+users.toString());
			System.out.println("userDetailService인스턴스의 getGrantedAuthorities(권한값): "+this.getGrantedAuthorities(users));
			
			
			return new User(users.getEmail(),
					users.getPassword(),
					
					true,
					true,
					true,
					true,
			
					this.getGrantedAuthorities(users));
			
		} catch (UsersException e) {
			System.out.println(e.getMessage());
		}		
		
		return null;
	}
	
	// 사용자에게 권한을 부여하기위해 권한 목록을 리턴하는 메소드
	private List<GrantedAuthority> getGrantedAuthorities(UserVO users) {
		
		// 최종적으로 스프링에게 전달할 리스트 (리스트 타입은 GrantedAuthority)
		List<GrantedAuthority> auths = new ArrayList<>();
		
		/*
		 * 데이터베이스에 저장되어 있던 해당 사용자의 권한을 하나씩 끄집어 내어
		 * GrantedAuthority 리스트에 추가해주는 작업
		 * 
		 * 스프링 3버전에서는 "ROLE_" 이라는 문자열을 추가 안해도 되지만
		 * 스프링 4버전 이후부터는 "ROLE_" 이라는 문자열을 권한이름 앞에 붙여줘야
		 * 정상 동작합니다.
		 */
		for (Authority item : users.getAuthorities()) {
			auths.add(new SimpleGrantedAuthority("ROLE_" + item.getName()));
		}
		
		System.out.println(users.getEmail() + " 사용자의 권한: " + auths);
		
		return auths;
	}
	
	
}

