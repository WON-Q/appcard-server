package com.fisa.appcard.domain;

/**
 * 세션의 인증 상태
 */
public enum AuthStatus {

    /**
     * 인증 진행중
     */
    PENDING,

    /**
     * 인증 완료
     */
    AUTHENTICATED,

    /**
     * 인증 실패
     */
    FAILED
}