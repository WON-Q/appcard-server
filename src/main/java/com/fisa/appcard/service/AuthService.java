package com.fisa.appcard.service;

import com.fisa.appcard.domain.AppCardKey;
import com.fisa.appcard.domain.AuthStatus;
import com.fisa.appcard.domain.AuthenticationSession;
import com.fisa.appcard.domain.PaymentStatus;
import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.feign.PgClient;
import com.fisa.appcard.feign.dto.request.PgAuthorizeRequest;
import com.fisa.appcard.feign.dto.response.BaseResponse;
import com.fisa.appcard.feign.dto.response.PgAuthorizeResponse;
import com.fisa.appcard.repository.AppCardKeyRepository;
import com.fisa.appcard.repository.AuthSessionRepository;
import com.fisa.appcard.utils.ChallengeUtil;
import com.fisa.appcard.utils.SignatureUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PgClient pgClient;
    private final AuthSessionRepository authSessionRepository;
    private final AppCardKeyRepository keyRepository;

    private final ChallengeUtil generator;

    @Transactional
    public BaseResponse<InitiateAuthResponse> initiate(InitiateAuthRequest req) {
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
                + "&amount=" + req.getAmount()
                + "&returnUrl=" + req.getCallbackUrl();

        InitiateAuthResponse data = new InitiateAuthResponse(deepLink);

        // 딥링크 DTO 반환
        return new BaseResponse<>(
                true,
                "200",
                "딥링크 발급 성공",
                data
        );
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

    /**
     * 클라이언트가 전송한 서명을 통해 해당 사용자가 정당한 카드 소유자인지를 검증하는 메서드
     *
     * @param txnId              트랜잭션 ID (서버가 인증 세션을 구분하는 고유 식별자)
     * @param cardId             사용자가 선택한 카드의 고유 ID
     * @param signatureBase64Url 클라이언트(Flutter)가 전송한 서명값, URL-safe Base64 형식의 64바이트 Ed25519 서명 (raw r||s)
     * @return 검증 성공 여부 (true: 인증 성공, false: 인증 실패)
     */
    public boolean verify(String txnId, String cardId, String signatureBase64Url, String cardNumber, String cardType) {
        // 1. 인증 세션 조회
        AuthenticationSession session = authSessionRepository.findById(txnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션이 없습니다."));

        // 2. 카드에 저장된 공개키 조회
        PublicKey publicKey = getPublicKey(cardId);

        // 3. 서명값 디코딩 (URL-safe Base64 → raw 64바이트 시그니처)
        byte[] rawSig = Base64.getUrlDecoder().decode(signatureBase64Url);

        // 4. 클라이언트가 받은 challenge 값을 올바르게 서명했는지 검증
        boolean ok = SignatureUtil.check(
                session.getChallenge(), // 서버가 발행한 challenge (텍스트 문자열)
                rawSig,                 // 클라이언트가 서명한 시그니처 (64바이트)
                publicKey               // 등록된 공개키
        );


        // 5. 검증 결과에 따라 세션 상태를 업데이트 -> PG서버에 인증 완료 API 호출 -> 결제 성공 여부 응답 받음
        if (ok) {
            // (1) 인증에 성공한 경우:
            session.authenticate(); // 인증 성공 → 세션 상태를 AUTHENTICATED로 변경


            // (2) PG 서버에 전송할 인증 완료 요청 객체 생성
            PgAuthorizeRequest requestDto = PgAuthorizeRequest.builder()
                    .txnId(txnId) // 트랜잭션 고유 ID
                    .authenticated(true) // 인증 성공 여부(true)
                    .authenticatedAt(Instant.now().toString()) // 인증 시각 (ISO-8601 형식 문자열)
                    .cardNumber(cardNumber) // 사용자가 선택한 카드 번호
                    .cardType(cardType) // 카드 유형 (CREDIT, CHECK)
                    .build();

            // (3) PG 서버에 인증 결과 전송 → 실제 결제 승인 요청
            // PG 서버는 이 인증 결과를 바탕으로 결제를 진행하거나 거절할 수 있음
            ResponseEntity<BaseResponse<PgAuthorizeResponse>> response = pgClient.authorize(requestDto);
            // 여기서 response의 paymentStatus값이 SUCCESS 라면 True를 Fail이라면 False를 반환함.
            return Optional.ofNullable(response.getBody())
                    .map(BaseResponse::getData)                  // PgAuthorizeResponse
                    .map(PgAuthorizeResponse::getPaymentStatus) // PaymentStatus
                    .map(status -> status == PaymentStatus.SUCCEEDED) // SUCCESS → true, 나머지 → false
                    .orElse(false);                              // body 또는 data 가 없으면 실패(false)


        } else {
            session.fail(); // 인증 실패 → 상태를 FAILED로 변경
            return false;
        }
    }

    /**
     * 카드에 저장된 공개키(Base64로 인코딩된 raw Ed25519 공개키)를 조회해서 Java의 PublicKey 객체로 변환하는 메서드
     *
     * @param cardId 공개키를 조회할 카드의 고유 ID
     * @return DER 인코딩이 적용된 Ed25519 Java PublicKey 객체
     */
    private PublicKey getPublicKey(String cardId) {
        // 1. cardId로부터 저장된 공개키(Base64 형식) 조회
        String publicKeyB64 = keyRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공개키가 존재하지 않습니다."))
                .getPublicKey();

        // 2. Base64 문자열을 디코딩하여 raw 공개키 바이트 추출 (32바이트)
        byte[] raw = Base64.getDecoder().decode(publicKeyB64);

        try {
            // 3. Java KeyFactory가 요구하는 DER 인코딩을 수동으로 추가 (ASN.1 구조의 Ed25519 X.509 prefix)
            // 이 prefix는 "SubjectPublicKeyInfo" 형식으로 만들어 주기 위한 템플릿
            byte[] prefix = new byte[]{
                    (byte) 0x30, (byte) 0x2a,              // SEQUENCE (전체 길이 0x2a = 42 bytes)
                    (byte) 0x30, (byte) 0x05,              // SEQUENCE (알고리즘 ID)
                    (byte) 0x06, (byte) 0x03,              // OBJECT IDENTIFIER (Ed25519)
                    (byte) 0x2b, (byte) 0x65, (byte) 0x70, //   OID: 1.3.101.112 (Ed25519)
                    (byte) 0x03, (byte) 0x21,              // BIT STRING (33 bytes = 0x21)
                    (byte) 0x00                            // unused bits in BIT STRING = 0
            };

            // 4. prefix + raw 공개키를 합쳐서 최종 DER 형식의 바이트 배열 생성
            byte[] der = new byte[prefix.length + raw.length];
            System.arraycopy(prefix, 0, der, 0, prefix.length);
            System.arraycopy(raw, 0, der, prefix.length, raw.length);

            // 5. DER 인코딩된 바이트 배열을 X509EncodedKeySpec으로 감싸고 PublicKey 객체 생성
            X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
            return KeyFactory.getInstance("Ed25519").generatePublic(spec);

        } catch (GeneralSecurityException e) { // DER 형식 오류, 키 생성 실패 등의 오류가 발생한 경우
            throw new IllegalStateException("유효하지 않은 공개키 형식입니다.", e);
        }
    }

}
