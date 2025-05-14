package com.fisa.appcard.dto.response;

import lombok.*;

/**
 * 딥링크 생성 API 응답 DTO
 * <br />
 * 이 DTO는 결제 흐름 중 <b>19번째 단계</b>에서 사용됩니다.
 * 자세한 내용은 프로젝트 내 {@code docs/payment-flow.md} 문서를 참고해 주세요.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateAuthResponse {

    /**
     * 앱카드 앱을 실행시킬 딥링크
     */
    private String deepLink;

}