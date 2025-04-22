package ssafy.horong.domain.currency.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssafy.horong.api.currency.response.CurrencyExchangeResponse;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeListWrapper implements Serializable {
    private List<CurrencyExchangeResponse> exchanges;
}
