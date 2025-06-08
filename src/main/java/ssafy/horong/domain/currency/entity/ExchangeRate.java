package ssafy.horong.domain.currency.entity;

import jakarta.persistence.*;
import lombok.*;
import ssafy.horong.domain.common.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ExchangeRate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;  // 엔화, 위안화, 달러 등 지원할 화폐 종류

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeType exchangeType;  // 구매 또는 판매 여부

    @Column(nullable = false)
    private double amount;  // 환율 금액

    @ManyToOne
    @JoinColumn(name = "currency_exchange_id", nullable = false)
    private CurrencyExchange currencyExchange;  // 환전소와의 관계

    // 화폐 종류 (Enum 타입)
    public enum Currency {
        JPY, // 일본 엔화
        CNY, // 중국 위안화
        USD  // 미국 달러
    }

    // 거래 유형 (Enum 타입)
    public enum ExchangeType {
        BUY,  // 구매
        SELL  // 판매
    }
}
