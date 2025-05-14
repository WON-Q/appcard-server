package com.fisa.appcard.dto.response;

import lombok.*;

/**
 * 검증 요청 API 응답 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>31번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse {

    /**
     * Flutter앱 내 개인키 서명에 대한 검증 결과 (T/F)
     */
    private boolean verified;

}
