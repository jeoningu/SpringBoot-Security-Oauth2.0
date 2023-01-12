package com.jig.security1.config.auth;

import com.jig.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * [로그인 처리 과정에서 사용되는 UserDetails]
 *
 *  - Security Session -> Authentication -> UserDetails
 *  - 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 *  - 로그인 진행이 완료되면 시큐리티 session을 만든다. (Security ContextHolder에 session정보를 저장한다.)
 *  - Authentication안에 user정보를 담는다. User 오브젝트는 UserDetails 타입이여야 한다.
 *  - UserDetails를 구현하여 사용하는데, user Entity를 컴포지션(변수로 사용)하고 각 메서드를 overrride 한다.
 */

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
@Data
public class PrincipalDetail implements UserDetails, OAuth2User {

    private User user;

    private Map<String, Object> attributes;

    /**
     * 일반 로그인할 때 사용되는 생성자
     * @param user
     */
    public PrincipalDetail(User user) {
        this.user = user;
    }

    /**
     * OAuth 로그인할 때 사용되는 생성자
     * @param user
     * @param attributes
     */
    public PrincipalDetail(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // user.getRole() 을 타입에 맞게 만들어준다.
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 계정이 만료된게 아닌지 ( true : 만료 안 됨)
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠긴게 아닌지 ( true : 안 잠김)
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 패스워드가 만료된게 아닌지 ( true : 만료 안 됨)
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화 됐는지 (true : 활성화)
     * @return
     */
    @Override
    public boolean isEnabled() {

        /**
         * 사용 예시
         *  1년동안 로그인 안한 회원을 휴면계정으로 처리하기로 함
         *    1. User객체에 '로그인 시간' 변수를 둠.   ( user.getLoginTime() )
         *    2. '현재시간 - 로그인시간' => 1년을 초과하면 return false;
         */

        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
