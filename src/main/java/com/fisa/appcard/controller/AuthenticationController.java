package com.fisa.appcard.controller;

import com.fisa.appcard.dto.request.InitiateAuthRequest;
import com.fisa.appcard.dto.response.InitiateAuthResponse;
import com.fisa.appcard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
