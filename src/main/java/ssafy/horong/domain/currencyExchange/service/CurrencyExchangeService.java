package ssafy.horong.domain.currencyExchange.service;

import ssafy.horong.api.currencyExchange.response.CurrencyExchangeResponse;

import java.util.List;

public interface CurrencyExchangeService {
    List<CurrencyExchangeResponse> getCurrencyExchangeList();
}
