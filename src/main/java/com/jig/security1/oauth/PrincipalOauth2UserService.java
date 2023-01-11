package com.jig.security1.oauth;

import com.jig.security1.auth.PrincipalDetails;
import com.jig.security1.model.User;
import com.jig.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    /**
     *  BCryptPasswordEncoder-loadUser, PrincipleService-loadUserByUsername
     *  가 없어도 내부적으로 동작하는데, PrincipalDetails반환해주기 위해 overrride 함.
     *
     * [구글로부터 받은 userRequest 데이터에 대한 후처리를 하는 함수]
     *
     *   구글 로그인 동작 흐름
     *    1. 구글 로그인 버튼 클릭
     *    2. 구글 로그인 창
     *    3. 로그인 완료
     *    4. code를 반환 받는데, OAuth2 Client 라이브러리가 code를 이용해서 AccessToken을 요청
     *    5. AccessToken과 userRequest 정보를 반환받는다. loadUser 함수에서 받환 받는다. ( userRequest에는 회원프로필 정보가 있다.)
     *
     * @param userRequest
     * @return OAuth2User
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /*
        System.out.println("userRequest = " + userRequest);
        System.out.println("userRequest.getClientRegistration() = " + userRequest.getClientRegistration());
        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken());
        System.out.println("userRequest.getAccessToken().getTokenValue() = " + userRequest.getAccessToken().getTokenValue());
        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());
        */

        /*
            [회원가입 정보]

            username = "google_" + super.loadUser(userRequest).getAttributes().get("sub")
            password = 암호화(겟인데어)
            email = super.loadUser(userRequest).getAttributes().get("email")
            role = ROLE_USER
            provider = userRequest.getClientRegistration().get("registrationId")
            providerId = super.loadUser(userRequest).getAttributes().get("sub")
         */

        // 회원등록이 안됐으면 회원가입 진행
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_" + providerId;
        // TODO: OAuth2 회원가입인 경우 패스워드에 특정값을 넣어줘서 노출되는 경우 모든 OAuth 회원이 노출되게 되는 문제가 있음. 해결 필요함
        String password = bCryptPasswordEncoder.encode("특정값");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";


        User userEntity = userRepository.findByUsername(username);
        if (null == userEntity) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
