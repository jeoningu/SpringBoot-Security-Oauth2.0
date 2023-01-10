package com.jig.security1.auth;

import com.jig.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인 진행이 완료되면 시큐리티 session을 만든다. (Security ContextHolder에 session정보를 저장한다.)
 * Authentication안에 user정보를 담는다. User 오브젝트는 UserDetails 타입이여야 한다.
 * UserDetails를 구현하여 사용하는데, user Entity를 컴포지션(변수로 사용)하고 각 메서드를 overrride 한다.
 *
 * Security Session -> Authentication -> UserDetails
 */
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
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
}
