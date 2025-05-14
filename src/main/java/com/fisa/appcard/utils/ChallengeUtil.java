package com.fisa.appcard.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ChallengeUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * 챌린지(빈 인증서)를 생성하는 메서드
     *
     * @return 챌린지(빈 인증서)
     */
    public String generate() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

}
