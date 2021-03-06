/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.engine.marketdata;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.opengamma.strata.basics.market.MarketDataId;
import com.opengamma.strata.basics.market.ObservableId;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;

/**
 * A mutable builder for building up {@link MarketEnvironment} instances.
 */
public final class MarketEnvironmentBuilder {

  /** The valuation date associated with the market data. */
  private LocalDate valuationDate;

  /** The single value market data items, keyed by ID. */
  private final Map<MarketDataId<?>, Object> values = new HashMap<>();

  /** Time series of observable market data values, keyed by ID. */
  private final Map<ObservableId, LocalDateDoubleTimeSeries> timeSeries = new HashMap<>();

  /**
   * Creates a builder with a valuation date but no market data.
   *
   * @param valuationDate  the valuation date associated with the market data
   */
  MarketEnvironmentBuilder(LocalDate valuationDate) {
    this.valuationDate = valuationDate;
  }

  /**
   * Creates a builder pre-populated with data.
   *
   * @param valuationDate  the valuation date associated with the market data
   * @param values  the single value market data items, keyed by ID
   * @param timeSeries  time series of observable market data values, keyed by ID
   */
  MarketEnvironmentBuilder(
      LocalDate valuationDate,
      Map<? extends MarketDataId<?>, Object> values,
      Map<? extends ObservableId, LocalDateDoubleTimeSeries> timeSeries) {

    this.valuationDate = ArgChecker.notNull(valuationDate, "valuationDate");
    this.values.putAll(values);
    this.timeSeries.putAll(timeSeries);
  }

  /**
   * Adds a single item of market data, replacing any existing value with the same ID.
   *
   * @param id  the ID of the market data
   * @param value  the market data value
   * @param <T>  the type of the market data value
   * @return this builder
   */
  public <T> MarketEnvironmentBuilder addValue(MarketDataId<T> id, T value) {
    ArgChecker.notNull(id, "id");
    ArgChecker.notNull(value, "value");
    values.put(id, value);
    return this;
  }

  /**
   * Adds a single item of market data, replacing any existing value with the same ID.
   * <p>
   * The type of the value is checked to ensure it is compatible with the ID.
   *
   * @param id  the ID of the market data
   * @param value  the market data value
   * @param <T>  the type of the market data value
   * @return this builder
   */
  <T> MarketEnvironmentBuilder addValueUnsafe(MarketDataId<T> id, Object value) {
    ArgChecker.notNull(id, "id");
    ArgChecker.notNull(value, "value");
    values.put(id, id.getMarketDataType().cast(value));
    return this;
  }

  /**
   * Adds multiple items of market data, replacing any existing values with the same IDs.
   *
   * @param values  the items of market data, keyed by ID
   * @return this builder
   */
  public MarketEnvironmentBuilder addAllValues(Map<? extends MarketDataId<?>, ?> values) {
    ArgChecker.notNull(values, "values");
    this.values.putAll(values);
    return this;
  }

  /**
   * Adds a time series of observable market data values, replacing any existing time series with the same ID.
   *
   * @param id  the ID of the values
   * @param timeSeries  a time series of observable market data values
   * @return this builder
   */
  public MarketEnvironmentBuilder addTimeSeries(ObservableId id, LocalDateDoubleTimeSeries timeSeries) {
    ArgChecker.notNull(id, "id");
    ArgChecker.notNull(timeSeries, "timeSeries");
    this.timeSeries.put(id, timeSeries);
    return this;
  }

  /**
   * Adds multiple time series of observable market data, replacing any existing time series with the same IDs.
   *
   * @param series  the time series of market data, keyed by ID
   * @return this builder
   */
  public MarketEnvironmentBuilder addAllTimeSeries(Map<? extends ObservableId, LocalDateDoubleTimeSeries> series) {
    ArgChecker.notNull(series, "series");
    timeSeries.putAll(series);
    return this;
  }

  /**
   * Sets the valuation date associated with the market data, replacing the existing valuation date.
   *
   * @param valuationDate  the valuation date associated with the market data
   * @return this builder
   */
  public MarketEnvironmentBuilder valuationDate(LocalDate valuationDate) {
    ArgChecker.notNull(valuationDate, "valuationDate");
    this.valuationDate = valuationDate;
    return this;
  }

  /**
   * Builds a set of market data from the data in this builder.
   * <p>
   * It is possible to continue to add more data to a builder after calling {@code build()}. Any
   * {@code BaseMarketData} instances built previously will be unaffected.
   *
   * @return a set of market data from the data in this builder
   */
  public MarketEnvironment build() {
    return new MarketEnvironment(valuationDate, values, timeSeries);
  }
}
