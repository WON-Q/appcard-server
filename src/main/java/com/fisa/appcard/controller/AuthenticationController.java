package com.fisa.appcard.controller;

import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.request.RegisterKeyRequest;
import com.fisa.appcard.dto.request.VerifyRequest;
import com.fisa.appcard.dto.response.ChallengeResponse;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.dto.response.VerifyResponse;
import com.fisa.appcard.feign.dto.response.BaseResponse;
import com.fisa.appcard.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentications")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    /**
     * 딥링크 생성 API
     */
    @PostMapping
    public ResponseEntity<BaseResponse<InitiateAuthResponse>> initiate(
            @RequestBody InitiateAuthRequest req
    ) {
        BaseResponse<InitiateAuthResponse> resp = authService.initiate(req);
        return ResponseEntity
                .ok(resp);
    }

    /**
     * 챌린지 조회 API
     */
    @GetMapping("/{txnId}/challenge")
    public ChallengeResponse challenge(@PathVariable String txnId) {
        return new ChallengeResponse(authService.getChallenge(txnId));
    }

    /**
     * 공개키 DB 등록 API
     */
    @PostMapping("/registerKey")
    public ResponseEntity<Void> registerKey(@RequestBody RegisterKeyRequest req) {
        authService.registerPublicKey(req.getCardId(), req.getPublicKey());
        return ResponseEntity.ok().build();
    }

    /**
     * 검증 요청 API
     */
    @PostMapping("/{txnId}/verify")
    public VerifyResponse verify(
            @PathVariable String txnId,
            @Valid @RequestBody VerifyRequest req
    ) {
        return new VerifyResponse(authService.verify(txnId, req.getCardId(), req.getSignature(), req.getCardNumber(), req.getCardType()));
    }

}
