package com._candoit.drfood.service;

import com._candoit.drfood.domain.Member;
import com._candoit.drfood.enums.Gender;
import com._candoit.drfood.enums.UserDisease;
import com._candoit.drfood.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void calculateAndSaveMember(Member member) {
        double standardWeight;

        if (member.getGender().equals(Gender.MALE)) {
            standardWeight = Math.pow(Double.parseDouble(member.getHeight()) / 100.0, 2) * 22;
        } else {
            standardWeight = Math.pow(Double.parseDouble(member.getHeight()) / 100.0, 2) * 21;
        }

        double dailyEnergy;
        switch (member.getExerciseStatus()) {
            case NONE:
                dailyEnergy = standardWeight * 27;
                break;
            case REGULAR:
                dailyEnergy = standardWeight * 32;
                break;
            case FREQUENT:
                dailyEnergy = standardWeight * 37;
                break;
            default:
                throw new IllegalArgumentException("Invalid exercise status");
        }

        member.setDailyEnergy(BigDecimal.valueOf(dailyEnergy));
        memberRepository.save(member);
    }

    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    public Member getMemberInfo(String loginId, String password) {
        Member member = memberRepository.findByLoginIdAndPassword(loginId, password)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!member.getPassword().equals(password)) {
            throw new IllegalArgumentException("해당 정보 없음");
        }

        return member;
    }

    @Transactional
    public void updateDisease(Long userId, UserDisease disease) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.setUserDisease(disease);
        memberRepository.save(member);
    }
}
