package com.fisa.appcard.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드 공개키 등록 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterKeyRequest {

    /**
     * 카드 고유 ID (문자열)
     */
    private String cardId;

    /**
     * 카드의 Base64 인코딩 공개키
     */
    private String publicKey;
}