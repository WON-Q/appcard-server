package com.fisa.appcard.feign;

import com.fisa.appcard.feign.dto.request.PgAuthorizeRequest;
import com.fisa.appcard.feign.dto.response.BaseResponse;
import com.fisa.appcard.feign.dto.response.PgAuthorizeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * PG(결제 대행) 서버와의 HTTP 통신을 추상화한 Feign 클라이언트 인터페이스
 * <p>
 * 주요 역할:
 * - 앱 카드 인증 완료 후 결제 승인을 요청하기 위해 PG 서버에 REST API를 호출합니다.
 *
 * @FeignClient 설명:
 * - name: FeignClient의 이름을 정의합니다. (스프링 컨테이너에서의 식별자)
 * - url: 실제 PG 서버의 기본 URL을 지정합니다. (application.yml에서 pg.base-url로 설정)
 * <p>
 * 호출 대상 API:
 * - POST /payments/{txnId}/authorize
 * → 해당 트랜잭션(txnId)에 대한 인증 성공 여부 및 카드 정보를 PG 서버에 전달하여 결제 승인을 요청합니다.
 */
@FeignClient(name = "pgClient", url = "${app.pg.endpoint")
public interface PgClient {

    /**
     * PG 서버에 인증 결과를 전달하고 결제 승인 요청을 보냅니다.
     *
     * @param txnId   트랜잭션 ID (URL 경로 변수로 전달됨)
     * @param request 결제 승인에 필요한 정보가 담긴 요청 본문 객체
     * @return PG 서버로부터 받은 승인 응답 객체
     */
    @PostMapping("/payments/{txnId}/authorize")
    ResponseEntity<BaseResponse<PgAuthorizeResponse>> authorize(
            @PathVariable("txnId") String txnId,
            @RequestBody PgAuthorizeRequest request
    );
}