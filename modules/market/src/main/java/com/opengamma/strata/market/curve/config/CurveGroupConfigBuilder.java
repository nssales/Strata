/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve.config;

import static com.opengamma.strata.collect.Guavate.toImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.basics.index.OvernightIndex;
import com.opengamma.strata.basics.index.RateIndex;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.market.curve.CurveGroupName;

/**
 * A mutable builder for creating instances of {@link CurveGroupConfig}.
 */
@SuppressWarnings("unchecked")
public final class CurveGroupConfigBuilder {

  /** The entries in the curve group. */
  private final List<CurveGroupEntry> entries = new ArrayList<>();

  /** The name of the curve group. */
  private CurveGroupName name;

  /**
   * Sets the name of the curve group configuration.
   *
   * @param name  the name of the curve group, not empty
   * @return this builder
   */
  public CurveGroupConfigBuilder name(CurveGroupName name) {
    this.name = ArgChecker.notNull(name, "name");
    return this;
  }

  /**
   * Adds configuration for a discounting curve to the curve group configuration.
   *
   * @param curveConfig  configuration of a discounting curve
   * @param currency  the currency for which the curve provides discount rates
   * @return this builder
   */
  public CurveGroupConfigBuilder addDiscountingCurve(CurveConfig curveConfig, Currency currency) {
    ArgChecker.notNull(curveConfig, "curveConfig");
    ArgChecker.notNull(currency, "currency");

    CurveGroupEntry entry = CurveGroupEntry.builder()
        .curveConfig(curveConfig)
        .discountingCurrency(currency)
        .build();
    entries.add(entry);
    return this;
  }

  /**
   * Adds configuration for a forward curve to the curve group configuration.
   *
   * @param curveConfig  configuration of a forward curve
   * @param index  an index for which the curve provides forward rates
   * @param otherIndices  indices for which the curve provides forward rates
   * @return this builder
   */
  public CurveGroupConfigBuilder addForwardCurve(CurveConfig curveConfig, RateIndex index, RateIndex... otherIndices) {
    ArgChecker.notNull(curveConfig, "curveConfig");
    ArgChecker.notNull(index, "index");

    CurveGroupEntry entry = CurveGroupEntry.builder()
        .curveConfig(curveConfig)
        .iborIndices(iborIndices(index, otherIndices))
        .overnightIndices(overnightIndices(index, otherIndices))
        .build();
    entries.add(entry);
    return this;
  }

  /**
   * Adds configuration to the curve group for a curve used to provide discount rates and forward rates.
   *
   * @param curveConfig  configuration of a curve
   * @param currency  the currency for which the curve provides discount rates
   * @param index  an index for which the curve provides forward rates
   * @param otherIndices  indices for which the curve provides forward rates
   * @return this builder
   */
  public CurveGroupConfigBuilder addCurve(
      CurveConfig curveConfig,
      Currency currency,
      RateIndex index,
      RateIndex... otherIndices) {

    CurveGroupEntry entry = CurveGroupEntry.builder()
        .curveConfig(curveConfig)
        .discountingCurrency(currency)
        .iborIndices(iborIndices(index, otherIndices))
        .overnightIndices(overnightIndices(index, otherIndices))
        .build();
    entries.add(entry);
    return this;
  }

  /**
   * Returns a set containing any IBOR indices in the arguments.
   */
  private static Set<IborIndex> iborIndices(RateIndex index, RateIndex... otherIndices) {
    return ImmutableList.<RateIndex>builder().add(index).add(otherIndices).build().stream()
        .filter(IborIndex.class::isInstance)
        .map(IborIndex.class::cast)
        .collect(toImmutableSet());
  }

  /**
   * Returns a set containing any overnight indices in the arguments.
   */
  private static Set<OvernightIndex> overnightIndices(RateIndex index, RateIndex... otherIndices) {
    return ImmutableList.<RateIndex>builder().add(index).add(otherIndices).build().stream()
        .filter(OvernightIndex.class::isInstance)
        .map(OvernightIndex.class::cast)
        .collect(toImmutableSet());
  }

  /**
   * Returns configuration for a curve group built from the data in this object.
   *
   * @return configuration for a curve group built from the data in this object
   */
  public CurveGroupConfig build() {
    return new CurveGroupConfig(name, entries);
  }
}
