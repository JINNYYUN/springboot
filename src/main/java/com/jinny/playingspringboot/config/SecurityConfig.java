package com.jinny.playingspringboot.config;

import com.jinny.playingspringboot.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
               // .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() //다른 주소는 권한이 허용
            .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/loginProc") //login주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                .defaultSuccessUrl("/")
            .and()
                .oauth2Login()
                .loginPage("/loginForm")
                //구글 로그인이 완료된 후의 처리가 필요하다.
                // 1. 코드받기(인증) 2.엑세스토큰(권한) 3.권한을 통해 사용자 프로필 정보를 가져올 수 있음
                // 4.그 정보를 토대로 회원가입을 자동으로 진행시키거나
                // 5. 정보가 부족할 경우 추가적인 회원가입 창을 통해 가입을 진행함.
                .userInfoEndpoint()
                .userService(principalOauth2UserService); //엑세스 토큰과 사용자 프로필 정보를 한 번에 가져옴.
    }
}
