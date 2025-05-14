package com.fisa.appcard.feign.dto.response;

import com.fisa.appcard.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 결제 성공 응답 API DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>48번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PgAuthorizeResponse {

    /**
     * 결제 트랜잭션 ID
     */
    private String txnId;

    /**
     * 결제 상태(SUCCESS, FAILED)
     */
    private PaymentStatus paymentStatus;



}