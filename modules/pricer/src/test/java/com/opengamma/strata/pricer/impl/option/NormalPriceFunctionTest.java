/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.option;

import static com.opengamma.strata.basics.PutCall.CALL;
import static com.opengamma.strata.basics.PutCall.PUT;
import static com.opengamma.strata.collect.TestHelper.assertThrowsIllegalArg;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

/**
 * Test {@link NormalPriceFunction},
 */
@Test
public class NormalPriceFunctionTest {

  private static final double T = 4.5;
  private static final double F = 104;
  private static final double DELTA = 10;
  private static final EuropeanVanillaOption ITM_CALL = EuropeanVanillaOption.of(F - DELTA, T, CALL);
  private static final EuropeanVanillaOption OTM_CALL = EuropeanVanillaOption.of(F + DELTA, T, CALL);
  private static final EuropeanVanillaOption ITM_PUT = EuropeanVanillaOption.of(F + DELTA, T, PUT);
  private static final EuropeanVanillaOption OTM_PUT = EuropeanVanillaOption.of(F - DELTA, T, PUT);
  private static final double DF = 0.9;
  private static final double SIGMA = 20.0;
  private static final NormalFunctionData VOL_DATA = NormalFunctionData.of(F, DF, SIGMA);
  private static final NormalFunctionData ZERO_VOL_DATA = NormalFunctionData.of(F, DF, 0);
  private static final NormalPriceFunction FUNCTION = new NormalPriceFunction();

  public void testInvalid() {
    assertThrowsIllegalArg(() -> FUNCTION.getPriceFunction(null));
    assertThrowsIllegalArg(() -> FUNCTION.getPriceFunction(ITM_CALL).evaluate((NormalFunctionData) null));
    assertThrowsIllegalArg(() -> FUNCTION.getPriceAdjoint(ITM_CALL, VOL_DATA, new double[0]));
  }

  public void testZeroVolPrice() {
    assertEquals(FUNCTION.getPriceFunction(ITM_CALL).evaluate(ZERO_VOL_DATA), DF * DELTA, 1e-15);
    assertEquals(FUNCTION.getPriceFunction(OTM_CALL).evaluate(ZERO_VOL_DATA), 0, 1e-15);
    assertEquals(FUNCTION.getPriceFunction(ITM_PUT).evaluate(ZERO_VOL_DATA), DF * DELTA, 1e-15);
    assertEquals(FUNCTION.getPriceFunction(OTM_PUT).evaluate(ZERO_VOL_DATA), 0, 1e-15);
  }

  public void testPriceAdjoint() {
    // Price
    double price = FUNCTION.getPriceFunction(ITM_CALL).evaluate(VOL_DATA);
    double[] priceDerivative = new double[3];
    double priceAdjoint = FUNCTION.getPriceAdjoint(ITM_CALL, VOL_DATA, priceDerivative);
    assertEquals(priceAdjoint, price, 1E-10);
    // Price with 0 volatility
    double price0 = FUNCTION.getPriceFunction(ITM_CALL).evaluate(ZERO_VOL_DATA);
    double[] price0Derivative = new double[3];
    double price0Adjoint = FUNCTION.getPriceAdjoint(ITM_CALL, ZERO_VOL_DATA, price0Derivative);
    assertEquals(price0Adjoint, price0, 1E-10);
    // Derivative forward.
    double deltaF = 0.01;
    NormalFunctionData dataFP = NormalFunctionData.of(F + deltaF, DF, SIGMA);
    NormalFunctionData dataFM = NormalFunctionData.of(F - deltaF, DF, SIGMA);
    double priceFP = FUNCTION.getPriceFunction(ITM_CALL).evaluate(dataFP);
    double priceFM = FUNCTION.getPriceFunction(ITM_CALL).evaluate(dataFM);
    double derivativeF_FD = (priceFP - priceFM) / (2 * deltaF);
    assertEquals(priceDerivative[0], derivativeF_FD, 1E-7);
    // Derivative strike.
    double deltaK = 0.01;
    EuropeanVanillaOption optionKP = EuropeanVanillaOption.of(F - DELTA + deltaK, T, CALL);
    EuropeanVanillaOption optionKM = EuropeanVanillaOption.of(F - DELTA - deltaK, T, CALL);
    double priceKP = FUNCTION.getPriceFunction(optionKP).evaluate(VOL_DATA);
    double priceKM = FUNCTION.getPriceFunction(optionKM).evaluate(VOL_DATA);
    double derivativeK_FD = (priceKP - priceKM) / (2 * deltaK);
    assertEquals(priceDerivative[2], derivativeK_FD, 1E-7);
    // Derivative volatility.
    double deltaV = 0.0001;
    NormalFunctionData dataVP = NormalFunctionData.of(F, DF, SIGMA + deltaV);
    NormalFunctionData dataVM = NormalFunctionData.of(F, DF, SIGMA - deltaV);
    double priceVP = FUNCTION.getPriceFunction(ITM_CALL).evaluate(dataVP);
    double priceVM = FUNCTION.getPriceFunction(ITM_CALL).evaluate(dataVM);
    double derivativeV_FD = (priceVP - priceVM) / (2 * deltaV);
    assertEquals(priceDerivative[1], derivativeV_FD, 1E-6);
  }

  private static final EuropeanVanillaOption ATM_CALL = EuropeanVanillaOption.of(F, T, CALL);
  private static final EuropeanVanillaOption ATM_PUT = EuropeanVanillaOption.of(F, T, PUT);

  // Test getDelta, getGamma and getVega
  public void greeksTest() {
    double tol = 1.0e-12;
    double eps = 1.0e-5;
    double[] priceDerivative = new double[3];
    EuropeanVanillaOption[] options = new EuropeanVanillaOption[] {
        ITM_CALL, ITM_PUT, OTM_CALL, OTM_PUT, ATM_CALL, ATM_PUT};
    for (EuropeanVanillaOption option : options) {
      // consistency with getPriceFunction for first order derivatives
      FUNCTION.getPriceAdjoint(option, VOL_DATA, priceDerivative);
      double delta = FUNCTION.getDelta(option, VOL_DATA);
      double vega = FUNCTION.getVega(option, VOL_DATA);
      assertEquals(priceDerivative[0], delta, tol);
      assertEquals(priceDerivative[1], vega, tol);

      // testing second order derivative against finite difference approximation
      NormalFunctionData dataUp = NormalFunctionData.of(F + eps, DF, SIGMA);
      NormalFunctionData dataDw = NormalFunctionData.of(F - eps, DF, SIGMA);
      double deltaUp = FUNCTION.getDelta(option, dataUp);
      double deltaDw = FUNCTION.getDelta(option, dataDw);
      double ref = 0.5 * (deltaUp - deltaDw) / eps;
      double gamma = FUNCTION.getGamma(option, VOL_DATA);
      assertEquals(gamma, ref, eps);

      EuropeanVanillaOption optionUp = EuropeanVanillaOption.of(option.getStrike(), T + eps, option.getPutCall());
      EuropeanVanillaOption optionDw = EuropeanVanillaOption.of(option.getStrike(), T - eps, option.getPutCall());
      double priceTimeUp = FUNCTION.getPriceAdjoint(optionUp, VOL_DATA, priceDerivative);
      double priceTimeDw = FUNCTION.getPriceAdjoint(optionDw, VOL_DATA, priceDerivative);
      ref = -0.5 * (priceTimeUp - priceTimeDw) / eps;
      double theta = FUNCTION.getTheta(option, VOL_DATA);
      assertEquals(theta, ref, eps);
    }
  }

  // Testing the branch for sigmaRootT < 1e-16
  public void smallParameterGreeksTest() {
    double eps = 1.0e-5;
    double[] der = new double[3];
    NormalFunctionData dataVolUp = NormalFunctionData.of(F, DF, eps);
    NormalFunctionData dataFwUp = NormalFunctionData.of(F + eps, DF, 0.0);
    NormalFunctionData dataFwDw = NormalFunctionData.of(F - eps, DF, 0.0);

    EuropeanVanillaOption[] options = new EuropeanVanillaOption[] {
        ITM_CALL, ITM_PUT, OTM_CALL, OTM_PUT, ATM_CALL, ATM_PUT};
    for (EuropeanVanillaOption option : options) {
      double delta = FUNCTION.getDelta(option, ZERO_VOL_DATA);
      double priceUp = FUNCTION.getPriceAdjoint(option, dataFwUp, der);
      double priceDw = FUNCTION.getPriceAdjoint(option, dataFwDw, der);
      double refDelta = 0.5 * (priceUp - priceDw) / eps;
      assertEquals(delta, refDelta, eps);

      double vega = FUNCTION.getVega(option, ZERO_VOL_DATA);
      double priceVolUp = FUNCTION.getPriceAdjoint(option, dataVolUp, der);
      double price = FUNCTION.getPriceAdjoint(option, ZERO_VOL_DATA, der);
      double refVega = (priceVolUp - price) / eps;
      assertEquals(vega, refVega, eps);

      double gamma = FUNCTION.getGamma(option, ZERO_VOL_DATA);
      double deltaUp = FUNCTION.getDelta(option, dataFwUp);
      double deltaDw = FUNCTION.getDelta(option, dataFwDw);
      double refGamma = 0.5 * (deltaUp - deltaDw) / eps;
      if (Math.abs(refGamma) > 0.1 / eps) { // infinity handled
        assertTrue(Double.isInfinite(gamma));
      } else {
        assertEquals(gamma, refGamma, eps);
      }

      EuropeanVanillaOption optionUp = EuropeanVanillaOption.of(option.getStrike(), T + eps, option.getPutCall());
      EuropeanVanillaOption optionDw = EuropeanVanillaOption.of(option.getStrike(), T - eps, option.getPutCall());
      double priceTimeUp = FUNCTION.getPriceAdjoint(optionUp, ZERO_VOL_DATA, der);
      double priceTimeDw = FUNCTION.getPriceAdjoint(optionDw, ZERO_VOL_DATA, der);
      double refTheta = -0.5 * (priceTimeUp - priceTimeDw) / eps;
      double theta = FUNCTION.getTheta(option, ZERO_VOL_DATA);
      assertEquals(theta, refTheta, eps);
    }
  }

}
