package com.fisa.appcard.dto.request;

import lombok.*;

/**
 * 딥링크 생성 API 요청 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>16번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateAuthRequest {

    /**
     * 결제 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 가격
     */
    private Long amount;

    /**
     * 가맹점명
     */
    private String merchantName;

    /**
     * 결제 후 돌아올 웹 URL
     */
    private String callbackUrl;

}