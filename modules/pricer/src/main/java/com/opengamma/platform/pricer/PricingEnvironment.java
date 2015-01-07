/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.platform.pricer;

import java.time.LocalDate;

import com.opengamma.analytics.financial.instrument.index.IndexON;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.basics.currency.Currency;
import com.opengamma.basics.currency.CurrencyAmount;
import com.opengamma.basics.currency.CurrencyPair;
import com.opengamma.basics.currency.MultiCurrencyAmount;
import com.opengamma.basics.index.FxIndex;
import com.opengamma.basics.index.IborIndex;
import com.opengamma.basics.index.Index;
import com.opengamma.basics.index.OvernightIndex;
import com.opengamma.collect.timeseries.LocalDateDoubleTimeSeries;

/**
 * The pricing environment used to calculate analytic measures.
 * <p>
 * All implementations of this interface must be immutable and thread-safe.
 */
public interface PricingEnvironment {
  
  // TODO: Should the valuation date be a feature of the PricingEnvironment and not a argument of all methods?

  /**
   * Gets the multicurve data.
   * 
   * @return the multicurve data
   */
  public MulticurveProviderInterface getMulticurve();
  /** FIXME: there are two ways to get discount factors: through the method or through the multicurve.
   *   Should it be restricted to only one? How to make sure that both are identical.
   *   Probably both should be accessible to have at the same time easy and efficient access to data. 
   *   But the multicurve version should come with a warning: use at you own risk.
   */

  /**
   * Gets the time series of an index.
   * 
   * @param index  the index to find a time series for
   * @return the time series of an index
   */
  public LocalDateDoubleTimeSeries getTimeSeries(Index index);

  /**
   * Gets the discount factor of a date.
   * 
   * @param currency  the currency to apply the discount factor in
   * @param valuationDate  the valuation date
   * @param date  the date to discount to
   * @return the discount factor
   */
  public double discountFactor(Currency currency, LocalDate valuationDate, LocalDate date);

  //-------------------------------------------------------------------------
  /**
   * Gets the historic or forward rate of a rate index.
   * 
   * @param index  the index to lookup
   * @param valuationDate  the valuation date
   * @param fixingDate  the fixing date to query the rate for
   * @return the rate of the index, either historic or forward
   */
  public double indexRate(IborIndex index, LocalDate valuationDate, LocalDate fixingDate);

  //-------------------------------------------------------------------------
  /**
   * Gets the current FX rate for a currency pair.
   * <p>
   * The rate returned is the rate from the base to counter as defined in the currency pair.
   * 
   * @param currencyPair  the ordered currency pair defining the rate required
   * @return the current FX rate for the currency pair
   */
  public double fxRate(CurrencyPair currencyPair);

  /**
   * Gets the historic or forward rate of an FX rate for a currency pair.
   * <p>
   * The rate returned is the rate from the base to counter as defined in the currency pair.
   * 
   * @param index  the index to lookup
   * @param currencyPair  the ordered currency pair defining the rate required
   * @param valuationDate  the valuation date
   * @param fixingDate  the fixing date to query the rate for
   * @return the rate of the index, either historic or forward
   */
  public double fxRate(FxIndex index, CurrencyPair currencyPair, LocalDate valuationDate, LocalDate fixingDate);

  /**
   * Converts in a given currency an multi-currency amount using the FX rates in the multi-curve provider.
   * 
   * @param mca The MultiCurrencyAmount.
   * @param ccy The currency in which the multi-currency amount should be converted. 
   * @return the rate of the index, either historic or forward
   */
  public CurrencyAmount convert(MultiCurrencyAmount mca, Currency ccy);
  
  /**
   * Convert the Overnight index into a "IndexON" used in OG-Analytics.
   * <p>
   * FIXME: Code only to initial testing. The objects should be uniformed before 3.0.
   * 
   * @param index The overnight index.
   * @return The index.
   */
  public IndexON convert(OvernightIndex index);
  
  /**
   * Convert the Overnight index into a "IndexIbor" used in OG-Analytics.
   * <p>
   * FIXME: Code only to initial testing. The objects should be uniformed before 3.0.
   * 
   * @param index The Ibor index.
   * @return The index.
   */
  public com.opengamma.analytics.financial.instrument.index.IborIndex convert(IborIndex index);
  
  /**
   * Convert the Currency into a "Currency" used in OG-Analytics.
   * <p>
   * FIXME: Code only to initial testing. The objects should be uniformed before 3.0.
   * 
   * @param ccy The currency.
   * @return The currency.
   */
  public com.opengamma.util.money.Currency currency(Currency ccy);

  //-------------------------------------------------------------------------
  /**
   * Converts a date to a relative {@code double} time.
   * <p>
   * This uses the day-count of the environment to determine the year fraction.
   * 
   * @param baseDate  the base date to find the time relative to
   * @param date  the date to find the relative time of
   * @return the relative time
   */
  public double relativeTime(LocalDate baseDate, LocalDate date);
  // FIXME: Need one relative time for each Currency/Index. To be able to use ACT or BUS in the same PE.
  
  public double relativeTime(Currency currency, LocalDate baseDate, LocalDate date);
  
  public double relativeTime(IborIndex index, LocalDate baseDate, LocalDate date);
  
  public double relativeTime(OvernightIndex index, LocalDate baseDate, LocalDate date);

}