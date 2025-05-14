package com.fisa.appcard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_card_keys")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCardKey {

    /**
     * 카드의 고유 ID값
     */
    @Id
    @Column(name = "card_id", length = 64)
    private String cardId;

    /**
     * 각 카드가 소유하고 있는 공개키
     */
    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    private String publicKey;
}