package com.jinny.playingspringboot.repository;


import com.jinny.playingspringboot.domain.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findByid(Long id) {
        //null이 반환될 가능성이 있을 때는 Optional을 사용해서 다음과 같이 처리해준다.
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        store.values().stream()
                .filter(member -> member.getName().equals(name))    //member.getName이 name과 동일한지 확인
                .findAny();
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        return null;
    }
}
