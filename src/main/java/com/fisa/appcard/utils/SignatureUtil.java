package com.fisa.appcard.utils;

import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

/**
 * 전자 서명 검증을 위한 유틸리티 클래스
 * <br />
 * 이 클래스는 Ed25519 알고리즘 기반의 전자 서명을 검증하는 데 사용되며,
 * 서버가 발급한 'challenge 문자열'에 대해 클라이언트가 생성한 서명을 확인하는 데 사용됩니다.
 */
@NoArgsConstructor
public final class SignatureUtil {

    static {
        // 자바 내장 보안 프로바이더는 Ed25519 지원이 제한적일 수 있으므로,
        // 다양한 알고리즘을 지원하는 BouncyCastle을 등록하여 확장성을 확보합니다.
        // 이는 JVM 수준에서 한 번만 등록되며, Signature.getInstance(...) 호출 시 사용됩니다.
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 클라이언트가 보낸 Ed25519 서명 값이, 서버가 발행한 원본 challenge 문자열에 대해 유효한지를 검증하는 메서드
     *
     * @param challenge    서명 검증 대상인 원본 문자열 (서버가 발행한 챌린지 문자열)
     * @param derSignature Flutter 클라이언트가 Ed25519 개인키로 서명한 바이트 배열 (raw 64바이트)
     *                     URL-safe Base64로 인코딩되어 왔다가 서버에서 decode됨
     * @param publicKey    클라이언트(카드)에 등록된 Ed25519 공개키
     * @return true: 서명이 challenge에 대해 유효한 경우 (해당 클라이언트가 진짜 키 소유자임)
     * false: 서명 위조이거나, 잘못된 공개키를 사용한 경우
     */
    public static boolean check(String challenge, byte[] derSignature, PublicKey publicKey) {
        try {
            // Ed25519 알고리즘에 대한 Signature 객체 생성
            Signature verifier = Signature.getInstance("Ed25519");

            // 서명 검증 준비: 공개키 설정
            verifier.initVerify(publicKey);

            // 검증 대상 데이터 (challenge) 지정
            verifier.update(challenge.getBytes(StandardCharsets.UTF_8));

            // 서명 검증 수행
            return verifier.verify(derSignature);

        } catch (GeneralSecurityException e) { // 서명 검증 중 에러가 발생한 경우 (지원되지 않는 알고리즘, 키 오류 등)
            return false; // 검증 실패로 간주
        }
    }
}