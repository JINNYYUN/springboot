package com.jinny.playingspringboot.config.oauth;

import com.jinny.playingspringboot.config.auth.PrincipalDetails;
import com.jinny.playingspringboot.config.oauth.provider.GoogleUserInfo;
import com.jinny.playingspringboot.config.oauth.provider.NaverUserInfo;
import com.jinny.playingspringboot.config.oauth.provider.OAuth2UserInfo;
import com.jinny.playingspringboot.model.User;
import com.jinny.playingspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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
        //userRequest정보 -> loadUser함수 호출 -> 회원프로필을 받아준다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User : " + oAuth2User);// token을 통해 응답받은 회원정보
        System.out.println("getAttribute : " + oAuth2User.getAttributes());

        //3. 받은 정보를 토대로, 회원가입을 진행
        OAuth2UserInfo oAuth2UserInfo  = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청~~");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청~~");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response")); //리턴타입이 Map이다.
        } else {
            System.out.println("우리는 구글과 네이버만 지원해요 ㅎㅎ");
        }
        
        String provider = oAuth2UserInfo.getProvider(); //google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId;
        String password =bCryptPasswordEncoder.encode("임시");
       // String email = oAuth2User.getAttribute("email");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";


        //3-1.
        //System.out.println("oAuth2UserInfo.getProvider() : " + oAuth2UserInfo.getProvider());
        User userEntity = userRepository.findByUsername(username);

        //회원정보가 없을 때 > 회원가입 진행
        User user;
        if(userEntity == null){
            System.out.println("처음 로그인입니다.");
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
            // user가 존재하면 update 해주기
            userEntity.setEmail(email);
            userRepository.save(userEntity);
            System.out.println("로그인을 진행한 적이 있습니다. 자동 회원가입이 진행됩니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
