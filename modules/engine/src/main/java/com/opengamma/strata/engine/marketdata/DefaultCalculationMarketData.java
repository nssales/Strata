/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.engine.marketdata;

import java.time.LocalDate;
import java.util.List;

import com.opengamma.strata.basics.market.MarketDataId;
import com.opengamma.strata.basics.market.MarketDataKey;
import com.opengamma.strata.basics.market.ObservableId;
import com.opengamma.strata.basics.market.ObservableKey;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;
import com.opengamma.strata.engine.marketdata.mapping.MarketDataMappings;

/**
 * A source of market data used for a calculation across multiple scenarios.
 */
public final class DefaultCalculationMarketData implements CalculationMarketData {

  /** The market data, keyed by market data ID. */
  private final ScenarioCalculationEnvironment marketData;

  /** Mappings to convert from the market data keys passed to the methods to IDs used for looking up the market data. */
  private final MarketDataMappings marketDataMappings;

  /**
   * Creates a new set of market data.
   *
   * @param marketData  the market data
   * @param marketDataMappings  mappings to convert from the market data keys passed to the methods to IDs used
   *   for looking up the market data
   */
  public DefaultCalculationMarketData(ScenarioCalculationEnvironment marketData, MarketDataMappings marketDataMappings) {
    this.marketData = ArgChecker.notNull(marketData, "marketData");
    this.marketDataMappings = ArgChecker.notNull(marketDataMappings, "marketDataMappings");
  }

  @Override
  public List<LocalDate> getValuationDates() {
    return marketData.getValuationDates();
  }

  @Override
  public int getScenarioCount() {
    return marketData.getScenarioCount();
  }

  @Override
  public <T> List<T> getValues(MarketDataKey<T> key) {
    MarketDataId<T> id = marketDataMappings.getIdForKey(key);
    return marketData.getValues(id);
  }

  @Override
  public LocalDateDoubleTimeSeries getTimeSeries(ObservableKey key) {
    ObservableId id = marketDataMappings.getIdForObservableKey(key);
    return marketData.getTimeSeries(id);
  }

  @Override
  public <T, K extends MarketDataKey<T>> T getGlobalValue(K key) {
    MarketDataId<T> id = marketDataMappings.getIdForKey(key);
    return marketData.getGlobalValue(id);
  }
}
