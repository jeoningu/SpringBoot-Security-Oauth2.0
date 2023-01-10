package com.jig.security1.controller;

import com.jig.security1.model.User;
import com.jig.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public @ResponseBody String user(){
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    // 이미 로그인 하고 나서도 /login 주소 맵핑대로 동작 안함.
    // 이유 : 스프링시큐리티가 낚아채서 안됨
    // 해결법 : SecurityConfig에서 설정 하면 login 후에는 낚아채지 않음 (아무 설정이나 해도 그런건지 특정 설정한 경우에만 그런건지 모르겠음)
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){

        user.setRole("ROLE_USER");
        String rowPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rowPassword);
        user.setPassword(encPassword);

        userRepository.save(user);
        return "redirect:/loginForm";
    }


}
