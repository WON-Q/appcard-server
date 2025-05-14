package com.fisa.appcard.feign.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인증 성공 요청 API DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>32번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgAuthorizeRequest {

    /**
     * 결제 트랜잭션 ID
     */
    private String txnId;

    /**
     * 인증 여부
     */
    private boolean authenticated;

    /**
     * 인증 완료 시각
     */
    private String authenticatedAt;

    /**
     * 결제 카드 번호
     */
    private String cardNumber;

    /**
     * 카드 종류(예: CREDIT, DEBIT)
     */
    private String cardType;
}