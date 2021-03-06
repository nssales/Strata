/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate.deposit;

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.finance.rate.deposit.ExpandedIborFixingDeposit;
import com.opengamma.strata.finance.rate.deposit.IborFixingDeposit;
import com.opengamma.strata.finance.rate.deposit.IborFixingDepositProduct;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.market.value.DiscountFactors;
import com.opengamma.strata.pricer.impl.rate.ForwardIborRateObservationFn;
import com.opengamma.strata.pricer.rate.RatesProvider;

/**
 * The methods associated to the pricing of Ibor fixing deposit by discounting.
 * <p>
 * This function provides the ability to price a {@link IborFixingDeposit}.
 */
public class DiscountingIborFixingDepositProductPricer {

  /**
   * Default implementation.
   */
  public static final DiscountingIborFixingDepositProductPricer DEFAULT =
      new DiscountingIborFixingDepositProductPricer(ForwardIborRateObservationFn.DEFAULT);

  /**
   * Rate observation function.
   */
  private final ForwardIborRateObservationFn rateObservationFn;

  /**
   * Creates an instance.
   * @param rateObservationFn the rate observation function
   */
  public DiscountingIborFixingDepositProductPricer(ForwardIborRateObservationFn rateObservationFn) {
    this.rateObservationFn = ArgChecker.notNull(rateObservationFn, "rateObservationFn");
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value of the Ibor fixing deposit product.
   * <p>
   * The present value of the product is the value on the valuation date.
   * 
   * @param product  the product to price
   * @param provider  the rates provider
   * @return the present value of the product
   */
  public CurrencyAmount presentValue(IborFixingDepositProduct product, RatesProvider provider) {
    ExpandedIborFixingDeposit deposit = product.expand();
    Currency currency = deposit.getCurrency();
    if (provider.getValuationDate().isAfter(deposit.getEndDate())) {
      return CurrencyAmount.of(currency, 0.0d);
    }
    double forwardRate = forwardRate(deposit, provider);
    double discountFactor = provider.discountFactor(currency, deposit.getEndDate());
    double fv = deposit.getNotional() * deposit.getYearFraction() * (deposit.getFixedRate() - forwardRate);
    double pv = discountFactor * fv;
    return CurrencyAmount.of(currency, pv);
  }

  /**
   * Calculates the present value sensitivity of the Ibor fixing product.
   * <p>
   * The present value sensitivity of the product is the sensitivity of the present value to
   * the underlying curves.
   * 
   * @param product  the product to price
   * @param provider  the rates provider
   * @return the point sensitivity of the present value
   */
  public PointSensitivities presentValueSensitivity(IborFixingDepositProduct product, RatesProvider provider) {
    ExpandedIborFixingDeposit deposit = product.expand();
    double forwardRate = forwardRate(deposit, provider);
    DiscountFactors discountFactors = provider.discountFactors(deposit.getCurrency());
    double discountFactor = discountFactors.discountFactor(deposit.getEndDate());
    // sensitivity
    PointSensitivityBuilder sensiFwd = forwardRateSensitivity(deposit, provider)
        .multipliedBy(-discountFactor * deposit.getNotional() * deposit.getYearFraction());
    PointSensitivityBuilder sensiDsc = discountFactors.zeroRatePointSensitivity(deposit.getEndDate())
        .multipliedBy(deposit.getNotional() * deposit.getYearFraction() * (deposit.getFixedRate() - forwardRate));
    return sensiFwd.combinedWith(sensiDsc).build();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the deposit fair rate given the start and end time and the accrual factor.
   * 
   * @param product  the product to price
   * @param provider  the rates provider
   * @return the par rate
   */
  public double parRate(IborFixingDepositProduct product, RatesProvider provider) {
    ExpandedIborFixingDeposit deposit = product.expand();
    return forwardRate(deposit, provider);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the spread to be added to the deposit rate to have a zero present value.
   * 
   * @param product  the product to price
   * @param provider  the rates provider
   * @return the par spread
   */
  public double parSpread(IborFixingDepositProduct product, RatesProvider provider) {
    ExpandedIborFixingDeposit deposit = product.expand();
    return forwardRate(deposit, provider) - deposit.getFixedRate();
  }

  /**
   * Calculates the par spread curve sensitivity.
   * 
   * @param product  the product to price
   * @param provider  the rates provider
   * @return the par spread curve sensitivity
   */
  public PointSensitivities parSpreadSensitivity(IborFixingDepositProduct product, RatesProvider provider) {
    ExpandedIborFixingDeposit deposit = product.expand();
    return forwardRateSensitivity(deposit, provider).build();
  }

  //-------------------------------------------------------------------------
  // query the forward rate
  private double forwardRate(ExpandedIborFixingDeposit product, RatesProvider provider) {
    return rateObservationFn.rate(product.getFloatingRate(), product.getStartDate(), product.getEndDate(), provider);
  }

  // query the forward rate sensitivity
  private PointSensitivityBuilder forwardRateSensitivity(ExpandedIborFixingDeposit product, RatesProvider provider) {
    return rateObservationFn.rateSensitivity(
        product.getFloatingRate(), product.getStartDate(), product.getEndDate(), provider);
  }

}
