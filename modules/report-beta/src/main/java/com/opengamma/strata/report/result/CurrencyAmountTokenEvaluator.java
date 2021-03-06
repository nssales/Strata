/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.report.result;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.result.Result;

/**
 * Evaluates a token against a currency amount.
 */
public class CurrencyAmountTokenEvaluator extends TokenEvaluator<CurrencyAmount> {

  private final String CURRENCY_FIELD = "currency";
  private final String AMOUNT_FIELD = "amount";

  @Override
  public Class<CurrencyAmount> getTargetType() {
    return CurrencyAmount.class;
  }

  @Override
  public ImmutableSet<String> tokens(CurrencyAmount amount) {
    return ImmutableSet.of(CURRENCY_FIELD, AMOUNT_FIELD);
  }

  @Override
  public Result<?> evaluate(CurrencyAmount amount, String token) {
    if (token.equals(CURRENCY_FIELD)) {
      return Result.success(amount.getCurrency());
    }
    if (token.equals(AMOUNT_FIELD)) {
      // Can be rendered directly - retains the currency for formatting purposes
      return Result.success(amount);
    }
    return invalidTokenFailure(amount, token);
  }

}
