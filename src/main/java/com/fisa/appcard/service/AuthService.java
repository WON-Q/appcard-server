package com.fisa.appcard.service;

import com.fisa.appcard.domain.AuthStatus;
import com.fisa.appcard.domain.AuthenticationSession;
import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.repository.AuthSessionRepository;
import com.fisa.appcard.utils.ChallengeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthSessionRepository authSessionRepository;

    private final ChallengeUtil generator;

    @Transactional
    public InitiateAuthResponse initiate(InitiateAuthRequest req) {
        // 인증을 위한 챌린지 생성
        String challenge = generator.generate();

        // 인증세션 객체 생성
        AuthenticationSession authenticationSession = AuthenticationSession.builder()
                .txnId(req.getTxnId())
                .amount(req.getAmount())
                .merchantName(req.getMerchantName())
                .orderId(req.getOrderId())
                .challenge(challenge)
                .status(AuthStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusSeconds(60))
                .status(AuthStatus.PENDING)
                .build();

        // 인증세션 저장
        authSessionRepository.save(authenticationSession);

        // 인증을 위한 딥링크 URL 생성
        String deepLink = "appcard://auth"
                + "?txn=" + req.getTxnId()
                + "&merchant=" + URLEncoder.encode(req.getMerchantName(), StandardCharsets.UTF_8)
                + "&amount=" + req.getAmount();

        // 딥링크 DTO 반환
        return new InitiateAuthResponse(deepLink);
    }

}
