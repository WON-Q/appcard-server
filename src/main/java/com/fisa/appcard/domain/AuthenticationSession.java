package com.fisa.appcard.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationSession {

    /**
     * 결제 트랜잭션 ID
     */
    @Id
    @Column(name = "txn_id", nullable = false)
    private String txnId;

    /**
     * 결제 가격
     */
    @Column(nullable = false)
    private Long amount;

    /**
     * 가맹점명
     */
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    /**
     * 챌린지(= 비어있는 인증 문서)
     */
    @Column(nullable = false)
    private String challenge;

    /**
     * 인증 상태
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthStatus status = AuthStatus.PENDING;

    /**
     * 세션 만료 시간
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

}