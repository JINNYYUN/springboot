package com.jinny.playingspringboot.config.oauth;

import com.jinny.playingspringboot.config.auth.PrincipalDetails;
import com.jinny.playingspringboot.model.User;
import com.jinny.playingspringboot.repository.UserRepository;
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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-client라이브러리) -> AccessToken요청

        // 1. code를 통해 구성한 정보
        System.out.println("userRequest clientRegistration : " + userRequest.getClientRegistration()); //어떤 OAuth로 로그인했는지 확인 가능
        System.out.println("userRequest accessToken : " + userRequest.getAccessToken());

        // 2. google의 회원 프로필 조회
        //userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원프로필을 받아준다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User : " + oAuth2User);// token을 통해 응답받은 회원정보
        System.out.println("getAttribute : " + oAuth2User.getAttributes());

        //3. 받은 정보를 토대로, 회원가입을 진행
        String provider = userRequest.getClientRegistration().getClientId(); //google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider+"_"+providerId;
        String password =bCryptPasswordEncoder.encode("임시");
        String email = oAuth2User.getAttribute("email");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        //3-1.
        User userEntity = userRepository.findByUsername(username);

        //회원정보가 없을 때 > 회원가입 진행
        if(userEntity == null){
            System.out.println("구글로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("구글로그인을 진행한 적입습니다. 자동 회원가입이 진행됩니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
