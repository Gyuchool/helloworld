package com.practice.springbatch;

import com.practice.springbatch.domain.GradeStatus;
import com.practice.springbatch.domain.Member;
import com.practice.springbatch.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (memberRepository.findAll().isEmpty()) {
            List<Member> members = new ArrayList<>();
            for (int i = 1; i < 100; i++) {
                members.add(new Member("name"+i, GradeStatus.BASIC));
            }
            memberRepository.saveAll(members);
        }
    }
}