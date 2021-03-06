/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.engine.marketdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.strata.basics.market.MarketDataId;
import com.opengamma.strata.basics.market.ObservableId;
import com.opengamma.strata.collect.ArgChecker;

/**
 * Mutable builder for creating instances of {@link MarketDataRequirements}.
 */
public final class MarketDataRequirementsBuilder {

  /** IDs identifying the observable market data values required for the calculations. */
  private final Set<ObservableId> observables = new HashSet<>();

  /** IDs identifying the non-observable market data values required for the calculations. */
  private final Set<MarketDataId<?>> nonObservables = new HashSet<>();

  /** IDs identifying the time series of market data values required for the calculations. */
  private final Set<ObservableId> timeSeries = new HashSet<>();

  /**
   * Adds requirements for time series of observable market data.
   *
   * @param ids  IDs of the data
   * @return this builder
   */
  public MarketDataRequirementsBuilder addTimeSeries(Collection<? extends ObservableId> ids) {
    ArgChecker.notNull(ids, "ids");
    timeSeries.addAll(ids);
    return this;
  }

  /**
   * Adds requirements for time series of observable market data.
   *
   * @param ids  IDs of the data
   * @return this builder
   */
  public MarketDataRequirementsBuilder addTimeSeries(ObservableId... ids) {
    return addTimeSeries(Arrays.asList(ids));
  }

  /**
   * Adds requirements for single values of market data.
   *
   * @param ids  IDs of the data
   * @return this builder
   */
  public MarketDataRequirementsBuilder addValues(Collection<? extends MarketDataId<?>> ids) {
    ArgChecker.notNull(ids, "ids");

    for (MarketDataId<?> id : ids) {
      if (id instanceof ObservableId) {
        observables.add((ObservableId) id);
      } else {
        nonObservables.add(id);
      }
    }
    return this;
  }

  /**
   * Adds requirements for single values of market data.
   *
   * @param ids  IDs of the data
   * @return this builder
   */
  public MarketDataRequirementsBuilder addValues(MarketDataId<?>... ids) {
    return addValues(Arrays.asList(ids));
  }

  /**
   * Adds all requirements from an instance of {@code MarketDataRequirements} to this builder.
   *
   * @param requirements  a set of requirements
   * @return this builder
   */
  public MarketDataRequirementsBuilder addRequirements(MarketDataRequirements requirements) {
    ArgChecker.notNull(requirements, "requirements");
    observables.addAll(requirements.getObservables());
    nonObservables.addAll(requirements.getNonObservables());
    timeSeries.addAll(requirements.getTimeSeries());
    return this;
  }

  /**
   * Returns a set of market data requirements built from the data in this builder.
   *
   * @return a set of market data requirements built from the data in this builder
   */
  public MarketDataRequirements build() {
    return new MarketDataRequirements(observables, nonObservables, timeSeries);
  }
}
