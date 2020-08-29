package com.jinny.playingspringboot.config.auth;

//로그인 진행이 완려되면 시큐리티 session을 만들어준다.(Security ContextHolder라는 키값에 정보를 저장한다)
//오브젝트는 Authentication타입의 객체이다.
//Authentication안에 User정보가 있어야 함.
//User오브젝트타입은 UserDetails타입 객체이어야 한다.

import com.jinny.playingspringboot.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

//즉, 시큐리티가 가지고 있는 세션에 세션 정보를 저장할 수 있는 객체는 Authentication이며,이 객체 안에 저장할 수 있는 User정보는 UserDetails타입이다.
public class PrincipalDetails implements UserDetails {

    private User user; //콤포지션

    public PrincipalDetails(User user){
        this.user = user;
    }

    //해당 유저의 권한을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //우리 사이트에서 1년동안 회원이 로그인을 하지 않았을 경우, 휴먼 계정을 하기로 함.
        //user.getLoginDate();
        //'현재시간 - 로그인 시간'을 계산해서
        //return false;를 하면 됨.
        return true;
    }
}
