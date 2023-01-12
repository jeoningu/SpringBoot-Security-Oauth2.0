package com.jig.security1.controller;

import com.jig.security1.config.auth.PrincipalDetail;
import com.jig.security1.model.User;
import com.jig.security1.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // view를 리턴
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // localhost:8080
    // localhost:8080/
    @GetMapping({"", "/"})
    public String index() {
        /** 머스테치 사용
         * 기본 폴더 :src/main/resources/
         *  mustache를 의존성 설정하면 뷰리졸버 설정은 기본으로 아래와 같이 설정된다!
         *  : prefix - templates, suffix - .mustache
         */

        return "index";  // src/main/resources/templates/index.mustache
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetail principalDetail) {
        System.out.println("user - principalDetails.getUser() = " + principalDetail.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 이미 로그인 하고 나서도 /login 주소 맵핑대로 동작 안함.
    // 이유 : 스프링시큐리티가 낚아채서 안됨
    // 해결법 : SecurityConfig에서 설정 하면 login 후에는 낚아채지 않음 (아무 설정이나 해도 그런건지 특정 설정한 경우에만 그런건지 모르겠음)
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {

        user.setRole("ROLE_USER");
        String rowPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rowPassword);
        user.setPassword(encPassword);

        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/adminInfo")
    @Secured("ROLE_ADMIN")
    public @ResponseBody String adminInfo() {
        return "개인정보";
    }

    @GetMapping("/managerInfo")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 함수가 시작되기 전에 실행된다
    public @ResponseBody String managerInfo() {
        return "개인정보";
    }

    /**
     *  [일반로그인, OAuth2 로그인 2가지를 한 곳에서 후처리하기 위한 방법]
     *
     *  - 스프링 시큐리티에 session을 관리하는 security session이 있다.
     *  - 로그인 하면 security session에 Authentication이 들어간다.
     *  - Authentication은 각 메서드에서 DI해서 사용할 수 있다.
     *  - Authentication에는 UserDetails 타입, OAuth2User 타입이 들어갈 수 있다. 일반 로그인 한 경우에는 UserDetails, OAuth2로그인한 경우는 OAuth2User
     *  - 로그인 후처리를 위해  UserDetails 타입, OAuth2User 타입 2가지를 한 클래스에서 implements 해서 사용하면 된다.
     *
     *    ===> public class PrincipalDetails implements UserDetails, OAuths2User{}
     */

    /**
     * 로그인 후 세션 정보를 확인 하는 방법 2가지
     *  1. Authentication authentication 를 DI 해서 getPrincipal()을 (PrincipalDetails)형변환 후 getUser()
     *  2. @AuthenticationPrincipal PrincipalDetails userDetails 를 DI해서 getUser()  // PrincipalDetails는 UserDetails를 implements 했기 떄문에 타입으로 사용 가능
     *
     * @param authentication
     * @param userDetails
     * @return
     */
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetail userDetails) {
        System.out.println("======================IndexController.testLogin======================");
        // 1. Authentication authentication
        PrincipalDetail principal = (PrincipalDetail) authentication.getPrincipal();
        System.out.println("authentication principalDetails user 정보 = " + principal.getUser());

        // 2. @AuthenticationPrincipal PrincipalDetails userDetails
        System.out.println("userDetails.getUser() = " + userDetails.getUser());

        return "로그인 후 세션 정보 확인";
    }

    /** OAuth2 로그인 후 세션 정보 확인하는 방법
     *   - Authentication authentication 를 DI 해서 getPrincipal()을 (OAuth2User)형변환 후 getUser()
     * @param authentication
     * @return
     */
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication,
                                               @AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("======================IndexController.testLogin======================");
        // 1. Authentication authentication
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication principalDetails user 정보 = " + principal.getAttributes());

        // 2. @AuthenticationPrincipal OAuth2User oAuth2User
        System.out.println("oAuth2User attribute = " + oAuth2User.getAttributes());


        return "OAuth2 로그인 후 세션 정보 확인";
    }
}
