package com.jig.security1.config.auth;

import com.jig.security1.model.User;
import com.jig.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 스프링시큐리티 로그인 처리 설정
 *  - UserDetailsService 구현
 *     => 시큐리티 설정(WebSecurityConfigurerAdapter 상속)에서 loginProcessingUrl("/login")으로 설정 해두면
 *        /login 요청이 왔을 때 UserDetailsService 타입의 스프링빈(PrincipleService)에 구현된 loadUserByUsername() 을 실행시킨다.
 *
 *  -  BCryptPasswordEncoder-loadUser, PrincipleService-loadUserByUsername
 *  가 없어도 내부적으로 동작하는데, PrincipalDetails반환해주기 위해 overrride 함.
 *
 *  아래와 같이 반환됨
 *  시큐리티 session <- Authentication <- UserDetails(principle)
 */
@Service
public class PrincipleService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            return null;
        }else {
            return new PrincipalDetail(user);
        }
    }
}
