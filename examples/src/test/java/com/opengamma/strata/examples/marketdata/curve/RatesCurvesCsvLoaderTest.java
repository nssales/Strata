/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.examples.marketdata.curve;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.date.DayCounts;
import com.opengamma.strata.basics.index.IborIndices;
import com.opengamma.strata.collect.io.ResourceLocator;
import com.opengamma.strata.market.curve.Curve;
import com.opengamma.strata.market.curve.CurveGroupName;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.curve.CurveParameterMetadata;
import com.opengamma.strata.market.curve.InterpolatedNodalCurve;
import com.opengamma.strata.market.id.DiscountCurveId;
import com.opengamma.strata.market.id.RateCurveId;
import com.opengamma.strata.market.id.RateIndexCurveId;

/**
 * Test {@link RatesCurvesCsvLoader}.
 */
@Test
public class RatesCurvesCsvLoaderTest {

  private static final String GROUPS_1 = "classpath:test-marketdata-complete/curves/groups.csv";
  private static final String SETTINGS_1 = "classpath:test-marketdata-complete/curves/settings.csv";
  private static final String CURVES_1 = "classpath:test-marketdata-complete/curves/curves-1.csv";
  private static final String CURVES_2 = "classpath:test-marketdata-complete/curves/curves-2.csv";
  private static final String CURVES_3 = "classpath:test-marketdata-complete/curves/curves-3.csv";
  private static final String CURVES_1_AND_2 = "classpath:test-marketdata-additional/curves/curves-1-and-2.csv";

  private static final String SETTINGS_INVALID_DAY_COUNT = "classpath:test-marketdata-additional/curves/settings-invalid-day-count.csv";
  private static final String SETTINGS_INVALID_INTERPOLATOR = "classpath:test-marketdata-additional/curves/settings-invalid-interpolator.csv";
  private static final String SETTINGS_INVALID_LEFT_EXTRAPOLATOR = "classpath:test-marketdata-additional/curves/settings-invalid-left-extrapolator.csv";
  private static final String SETTINGS_INVALID_RIGHT_EXTRAPOLATOR = "classpath:test-marketdata-additional/curves/settings-invalid-right-extrapolator.csv";
  private static final String SETTINGS_INVALID_MISSING_COLUMN = "classpath:test-marketdata-additional/curves/settings-invalid-missing-column.csv";
  private static final String SETTINGS_INVALID_VALUE_TYPE = "classpath:test-marketdata-additional/curves/settings-invalid-value-type.csv";

  private static final String GROUPS_INVALID_CURVE_TYPE = "classpath:test-marketdata-additional/curves/groups-invalid-curve-type.csv";
  private static final String GROUPS_INVALID_REFERENCE_INDEX = "classpath:test-marketdata-additional/curves/groups-invalid-reference-index.csv";

  private static final String CURVES_INVALID_DUPLICATE_POINTS = "classpath:test-marketdata-additional/curves/curves-invalid-duplicate-points.csv";

  // curve date used in the test data
  private static final LocalDate CURVE_DATE = LocalDate.of(2009, 7, 31);
  private static final LocalDate CURVE_DATE_CURVES_3 = LocalDate.of(2009, 7, 30);

  // tolerance
  private static final double TOLERANCE = 1.0E-4;

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_missing_settings_file() {
    testSettings("classpath:invalid");
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Header not found: Curve Name")
  public void test_invalid_settings_missing_column_file() {
    testSettings(SETTINGS_INVALID_MISSING_COLUMN);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "DayCount name not found: Act")
  public void test_invalid_settings_day_count_file() {
    testSettings(SETTINGS_INVALID_DAY_COUNT);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "CurveInterpolator name not found: Wacky")
  public void test_invalid_settings_interpolator_file() {
    testSettings(SETTINGS_INVALID_INTERPOLATOR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "CurveExtrapolator name not found: Polynomial")
  public void test_invalid_settings_left_extrapolator_file() {
    testSettings(SETTINGS_INVALID_LEFT_EXTRAPOLATOR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "CurveExtrapolator name not found: Polynomial")
  public void test_invalid_settings_right_extrapolator_file() {
    testSettings(SETTINGS_INVALID_RIGHT_EXTRAPOLATOR);
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      expectedExceptionsMessageRegExp = "Unsupported Value Type in curve settings: DS")
  public void test_invalid_settings_value_type_file() {
    testSettings(SETTINGS_INVALID_VALUE_TYPE);
  }

  private void testSettings(String settingsResource) {
    RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(settingsResource),
        ImmutableList.of(ResourceLocator.of(CURVES_1)),
        CURVE_DATE);
  }

  //-------------------------------------------------------------------------
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_missing_groups_file() {
    testGroups("classpath:invalid");
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Unsupported curve type: Inflation")
  public void test_invalid_groups_curve_type_file() {
    testGroups(GROUPS_INVALID_CURVE_TYPE);
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No index found for reference: LIBOR")
  public void test_invalid_groups_reference_index_file() {
    testGroups(GROUPS_INVALID_REFERENCE_INDEX);
  }

  private void testGroups(String groupsResource) {
    RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(groupsResource),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1)),
        CURVE_DATE);
  }

  //-------------------------------------------------------------------------
  public void test_single_curve_single_file() {
    Map<RateCurveId, Curve> curves = RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1)),
        CURVE_DATE);

    assertEquals(curves.size(), 1);

    Curve curve = Iterables.getOnlyElement(curves.values());
    assertUsdDisc(curve);
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Multiple entries with same key: .*")
  public void test_single_curve_multiple_Files() {
    RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1), ResourceLocator.of(CURVES_1)),
        CURVE_DATE);
  }

  public void test_multiple_curves_single_file() {
    Map<RateCurveId, Curve> curves = RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1_AND_2)),
        CURVE_DATE);

    assertEquals(curves.size(), 2);
    assertCurves(curves);
  }

  public void test_multiple_curves_multiple_files() {
    Map<RateCurveId, Curve> curves = RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1), ResourceLocator.of(CURVES_2)),
        CURVE_DATE);

    assertEquals(curves.size(), 2);
    assertCurves(curves);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_invalid_curve_duplicate_points() {
    RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_INVALID_DUPLICATE_POINTS)),
        CURVE_DATE);
  }
  
  //-------------------------------------------------------------------------
  public void test_load_all_curves() {
    Map<LocalDate, Map<RateCurveId, Curve>> allCurves = RatesCurvesCsvLoader.loadAllCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1), ResourceLocator.of(CURVES_2), ResourceLocator.of(CURVES_3)));
    
    assertEquals(allCurves.size(), 2);
    assertCurves(allCurves.get(CURVE_DATE));
    
    Map<RateCurveId, Curve> curves3 = allCurves.get(CURVE_DATE_CURVES_3);
    assertEquals(curves3.size(), 2);
    
    // All curve points are set to 0 in test data to ensure these are really different curve instances
    DiscountCurveId discountCurveId = DiscountCurveId.of(Currency.USD, CurveGroupName.of("Default"));
    Curve usdDisc = curves3.get(discountCurveId);
    InterpolatedNodalCurve usdDiscNodal = (InterpolatedNodalCurve) usdDisc;
    assertEquals(usdDiscNodal.getMetadata().getCurveName(), CurveName.of("USD-Disc"));
    for (double yValue : usdDiscNodal.getYValues()) {
      assertEquals(yValue, 0d);
    }
    
    RateIndexCurveId libor3mCurveId = RateIndexCurveId.of(IborIndices.USD_LIBOR_3M, CurveGroupName.of("Default"));
    Curve usd3ml = curves3.get(libor3mCurveId);
    InterpolatedNodalCurve usd3mlNodal = (InterpolatedNodalCurve) usd3ml;
    assertEquals(usd3mlNodal.getMetadata().getCurveName(), CurveName.of("USD-3ML"));
    for (double yValue : usd3mlNodal.getYValues()) {
      assertEquals(yValue, 0d);
    }
  }
  
  public void test_load_curves_date_filtering() {
    Map<RateCurveId, Curve> curves = RatesCurvesCsvLoader.loadCurves(
        ResourceLocator.of(GROUPS_1),
        ResourceLocator.of(SETTINGS_1),
        ImmutableList.of(ResourceLocator.of(CURVES_1), ResourceLocator.of(CURVES_2), ResourceLocator.of(CURVES_3)),
        CURVE_DATE);

    assertEquals(curves.size(), 2);
    assertCurves(curves);
  }

  //-------------------------------------------------------------------------
  private void assertCurves(Map<RateCurveId, Curve> curves) {
    assertNotNull(curves);
    
    DiscountCurveId discountCurveId = DiscountCurveId.of(Currency.USD, CurveGroupName.of("Default"));
    Curve usdDisc = curves.get(discountCurveId);
    assertUsdDisc(usdDisc);

    RateIndexCurveId libor3mCurveId = RateIndexCurveId.of(IborIndices.USD_LIBOR_3M, CurveGroupName.of("Default"));
    Curve usd3ml = curves.get(libor3mCurveId);
    assertUsd3ml(usd3ml);
  }
  
  private void assertUsdDisc(Curve curve) {
    assertTrue(curve instanceof InterpolatedNodalCurve);
    InterpolatedNodalCurve nodalCurve = (InterpolatedNodalCurve) curve;
    assertEquals(nodalCurve.getMetadata().getCurveName(), CurveName.of("USD-Disc"));

    LocalDate valuationDate = LocalDate.of(2009, 7, 31);
    LocalDate[] nodeDates = new LocalDate[] {
        LocalDate.of(2009, 11, 6),
        LocalDate.of(2010, 2, 8),
        LocalDate.of(2010, 8, 6),
        LocalDate.of(2011, 8, 8),
        LocalDate.of(2012, 8, 8),
        LocalDate.of(2014, 8, 6),
        LocalDate.of(2019, 8, 7)
    };
    String[] labels = new String[] {"3M", "6M", "1Y", "2Y", "3Y", "5Y", "10Y"};

    for (int i = 0; i < nodalCurve.getXValues().length; i++) {
      LocalDate nodeDate = nodeDates[i];
      double actualYearFraction = nodalCurve.getXValues()[i];
      double expectedYearFraction = getYearFraction(valuationDate, nodeDate);
      assertThat(actualYearFraction).isCloseTo(expectedYearFraction, offset(TOLERANCE));

      CurveParameterMetadata nodeMetadata = nodalCurve.getMetadata().getParameterMetadata().get().get(i);
      assertEquals(nodeMetadata.getLabel(), labels[i]);
    }

    double[] expectedYValues = new double[] {
        0.001763775,
        0.002187884,
        0.004437206,
        0.011476741,
        0.017859057,
        0.026257102,
        0.035521988
    };
    assertEquals(nodalCurve.getYValues(), expectedYValues);
  }

  private void assertUsd3ml(Curve curve) {
    assertTrue(curve instanceof InterpolatedNodalCurve);
    InterpolatedNodalCurve nodalCurve = (InterpolatedNodalCurve) curve;
    assertEquals(nodalCurve.getMetadata().getCurveName(), CurveName.of("USD-3ML"));

    LocalDate valuationDate = LocalDate.of(2009, 7, 31);
    LocalDate[] nodeDates = new LocalDate[] {
        LocalDate.of(2009, 11, 4),
        LocalDate.of(2010, 8, 4),
        LocalDate.of(2011, 8, 4),
        LocalDate.of(2012, 8, 6),
        LocalDate.of(2014, 8, 5),
        LocalDate.of(2019, 8, 6)
    };
    String[] labels = new String[] {"3M", "1Y", "2Y", "3Y", "5Y", "10Y"};

    for (int i = 0; i < nodalCurve.getXValues().length; i++) {
      LocalDate nodeDate = nodeDates[i];
      double actualYearFraction = nodalCurve.getXValues()[i];
      double expectedYearFraction = getYearFraction(valuationDate, nodeDate);
      assertThat(actualYearFraction).isCloseTo(expectedYearFraction, offset(TOLERANCE));

      CurveParameterMetadata nodeMetadata = nodalCurve.getMetadata().getParameterMetadata().get().get(i);
      assertEquals(nodeMetadata.getLabel(), labels[i]);
    }

    double[] expectedYValues = new double[] {
        0.007596889,
        0.008091541,
        0.015244398,
        0.021598026,
        0.029984216,
        0.039245812
    };
    assertEquals(nodalCurve.getYValues(), expectedYValues);
  }

  private double getYearFraction(LocalDate fromDate, LocalDate toDate) {
    return DayCounts.ACT_ACT_ISDA.yearFraction(fromDate, toDate);
  }

}
