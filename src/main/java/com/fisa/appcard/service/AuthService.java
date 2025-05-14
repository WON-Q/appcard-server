package com.fisa.appcard.service;

import com.fisa.appcard.domain.AppCardKey;
import com.fisa.appcard.domain.AuthStatus;
import com.fisa.appcard.domain.AuthenticationSession;
import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.repository.AppCardKeyRepository;
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
    private final AppCardKeyRepository keyRepository;

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

    /**
     * txnId에 해당하는 인증세션의 챌린지 값을 반환합니다.
     *
     * @param txnId
     * @return challenge
     */
    public String getChallenge(String txnId) {
        return authSessionRepository.findById(txnId).orElseThrow().getChallenge();
    }

    /**
     * 카드의 공개키를 저장합니다.
     *
     * @param cardId    저장할 카드의 고유 ID
     * @param publicKey 카드의 Base64 인코딩 공개키
     */
    public void registerPublicKey(String cardId, String publicKey) {
        AppCardKey key = new AppCardKey(cardId, publicKey);
        keyRepository.save(key);
    }

}
