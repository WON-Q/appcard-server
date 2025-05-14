package com.fisa.appcard.controller;

import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.request.RegisterKeyRequest;
import com.fisa.appcard.dto.response.ChallengeResponse;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.service.AuthService;
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
    public InitiateAuthResponse initiate(@RequestBody InitiateAuthRequest req) {
        return authService.initiate(req);
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

}
