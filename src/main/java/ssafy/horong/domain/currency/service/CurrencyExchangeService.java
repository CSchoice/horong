package ssafy.horong.domain.currency.service;

import ssafy.horong.api.currency.response.CurrencyExchangeResponse;

import java.util.List;

public interface CurrencyExchangeService {
    List<CurrencyExchangeResponse> getCurrencyExchangeList();
}
