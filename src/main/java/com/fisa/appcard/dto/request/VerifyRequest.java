package com.fisa.appcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 검증 요청 API 요청 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>30번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRequest {

    /**
     * 시그니처 = 서명된 챌린지
     */
    @NotBlank
    private String signature;

    /**
     * 카드 타입(CREDIT, CHECK)
     */
    @NotBlank
    private String cardType;

    /**
     * 선택된 카드 번호
     */
    @NotBlank
    private String cardNumber;

    /**
     * 선택된 카드 ID
     */
    @NotBlank
    private String cardId;

}