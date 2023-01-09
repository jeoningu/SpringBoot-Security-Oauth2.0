package com.jig.security1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터가 스프링 필터체인에 등록됩니다.
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()   // crsf 토큰 비활성화 ( 테스트 시 필요)
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()          // 이 경로는 인증이 필요하다.
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 이 경로는 해당 역할이 필요하다.
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")         // 이 경로는 해당 역할이 필요하다.
                .anyRequest().permitAll()                                                           // 위 경로가 아닌 경우는 누구나 허용
                .and()
                .formLogin()              // 위 경로가 아닌 요청은 로그인 페이지로 이동시키고
                .loginPage("/loginForm")      // 로그인 페이지는 이 경로로 하겠다.
                .defaultSuccessUrl("/");  // 정상 로그인 후 이동되는 경로
    }
}
