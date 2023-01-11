package com.jig.security1.config;

import com.jig.security1.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * OAuth2 과정
 *  1. 토큰 받기 (인증)
 *  2. 토큰으로 access토큰 받기 (정보에 대한 접근 권한)
 *  3. access토큰으로 정보 요청
 *  4. 받아온 정보를 이용해 바로 회원가입을 할 수 있음 or 회원에 추가정보가 필요한 경우 추가정보입력화면을 띄워서 회원가입 진행
 *
 * 그런데, OAuth2 Client 라이브러리 사용하면 바로 access토큰, 사용자프로필정보 를 받아온다.
 */
@Configuration
@EnableWebSecurity  // 스프링 시큐리티 필터가 스프링 필터체인에 등록됩니다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // securedEnabled = true -> @Secured 어노테이션 활성화 // prePostEnabled = true -> @PreAuthorize, @PostAuthorize 어노테이션 활성화
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()   // crsf 토큰 비활성화 ( 테스트 시 필요)
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()          // 이 경로는 인증이 필요하다. ( 별도 역할을 체크하지 않고 로그인만 되면 접속 가능)
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")  // 이 경로는 해당 역할이 필요하다.
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")                               // 이 경로는 해당 역할이 필요하다.
                .anyRequest().permitAll()                                                               // 위 경로가 아닌 경우는 누구나 허용
                .and()
                .formLogin()                    // 위 경로가 아닌 요청은 로그인 페이지로 이동시키고
                .loginPage("/loginForm")        // 로그인 페이지는 이 경로로 하겠다.
                .loginProcessingUrl("/login")   // 이 경로가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행한다.
                .defaultSuccessUrl("/")         // /loginForm 으로 들어와서 정상 로그인 후 이동되는 경로 ( 다른 경로로 들어와서 로그인 한 경우에는 들어온 경로로 callback 한다)
                .and()
                .oauth2Login()                      // OAuth2기반의 로그인인 경우
                .loginPage("/loginForm")            // 인증이 필요한 URL에 접근하면 /loginForm으로 이동
                .userInfoEndpoint()                         // 로그인 성공 후 사용자정보를 가져온다
                .userService(principalOauth2UserService)   // 로그인 후 후처리하는 객체 등록  // 구글서버가 '엑세스 토큰', '사용자 프로필 정보'를 반환
        ;


//                .authorizationEndpoint()
//                .baseUri("/login/oauth2/authorization");


    }
}
