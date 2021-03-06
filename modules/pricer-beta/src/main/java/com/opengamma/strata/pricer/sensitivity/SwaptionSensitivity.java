/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.sensitivity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ComparisonChain;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.FxRateProvider;
import com.opengamma.strata.finance.rate.swap.type.FixedIborSwapConvention;
import com.opengamma.strata.market.sensitivity.MutablePointSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivity;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;

/**
 * Point sensitivity to a swaption implied parameter point.
 * <p>
 * Holds the sensitivity to the swaption grid point.
 */
@BeanDefinition(builderScope = "private")
public final class SwaptionSensitivity
    implements PointSensitivity, PointSensitivityBuilder, ImmutableBean, Serializable {

  /**
   * The convention of the swap for which the data is valid.
   */
  @PropertyDefinition(validate = "notNull")
  private final FixedIborSwapConvention convention;
  /**
   * The expiry date/time of the option.
   */
  @PropertyDefinition(validate = "notNull")
  private final ZonedDateTime expiry;
  /**
   * The underlying swap tenor.
   */
  @PropertyDefinition
  private final double tenor;
  /**
   * The swaption strike rate.
   */
  @PropertyDefinition
  private final double strike;
  /**
   * The underlying swap forward rate.
   */
  @PropertyDefinition
  private final double forward;
  /**
   * The currency of the sensitivity.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final Currency currency;
  /**
   * The value of the sensitivity.
   */
  @PropertyDefinition(overrideGet = true)
  private final double sensitivity;

  //-------------------------------------------------------------------------
  /**
   * Obtains a {@code SwaptionSensitivity} from the specified elements.
   * 
   * @param convention  the convention of the swap for which the data is valid
   * @param expiry  the expiry date/time of the option
   * @param tenor  the underlying swap tenor
   * @param strike  the swaption strike rate
   * @param forward  the underlying swap forward rate
   * @param sensitivityCurrency  the currency of the sensitivity
   * @param sensitivity  the value of the sensitivity
   * @return the point sensitivity object
   */
  public static SwaptionSensitivity of(
      FixedIborSwapConvention convention,
      ZonedDateTime expiry,
      double tenor,
      double strike,
      double forward,
      Currency sensitivityCurrency,
      double sensitivity) {

    return new SwaptionSensitivity(convention, expiry, tenor, strike, forward, sensitivityCurrency, sensitivity);
  }

  //-------------------------------------------------------------------------
  @Override
  public SwaptionSensitivity withCurrency(Currency ccy) {
    return new SwaptionSensitivity(convention, expiry, tenor, strike, forward, ccy, sensitivity);
  }

  @Override
  public PointSensitivity withSensitivity(double value) {
    return new SwaptionSensitivity(convention, expiry, tenor, strike, forward, currency, value);
  }

  @Override
  public int compareKey(PointSensitivity other) {
    if (other instanceof SwaptionSensitivity) {
      SwaptionSensitivity otherSwpt = (SwaptionSensitivity) other;
      return ComparisonChain.start()
          .compare(currency, otherSwpt.currency)
          .compare(expiry, otherSwpt.expiry)
          .compare(tenor, otherSwpt.tenor)
          .compare(strike, otherSwpt.strike)
          .compare(forward, otherSwpt.forward)
          .compare(convention.toString(), otherSwpt.convention.toString())
          .result();
    }
    return getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
  }

  @Override
  public SwaptionSensitivity convertedTo(Currency resultCurrency, FxRateProvider rateProvider) {
    return (SwaptionSensitivity) PointSensitivity.super.convertedTo(resultCurrency, rateProvider);
  }

  //-------------------------------------------------------------------------
  @Override
  public SwaptionSensitivity multipliedBy(double factor) {
    return new SwaptionSensitivity(convention, expiry, tenor, strike, forward, currency, sensitivity * factor);
  }

  @Override
  public SwaptionSensitivity mapSensitivity(DoubleUnaryOperator operator) {
    return new SwaptionSensitivity(convention, expiry, tenor, strike, forward, currency, operator.applyAsDouble(sensitivity));
  }

  @Override
  public SwaptionSensitivity normalize() {
    return this;
  }

  @Override
  public MutablePointSensitivities buildInto(MutablePointSensitivities combination) {
    return combination.add(this);
  }

  @Override
  public SwaptionSensitivity cloned() {
    return this;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SwaptionSensitivity}.
   * @return the meta-bean, not null
   */
  public static SwaptionSensitivity.Meta meta() {
    return SwaptionSensitivity.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SwaptionSensitivity.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private SwaptionSensitivity(
      FixedIborSwapConvention convention,
      ZonedDateTime expiry,
      double tenor,
      double strike,
      double forward,
      Currency currency,
      double sensitivity) {
    JodaBeanUtils.notNull(convention, "convention");
    JodaBeanUtils.notNull(expiry, "expiry");
    JodaBeanUtils.notNull(currency, "currency");
    this.convention = convention;
    this.expiry = expiry;
    this.tenor = tenor;
    this.strike = strike;
    this.forward = forward;
    this.currency = currency;
    this.sensitivity = sensitivity;
  }

  @Override
  public SwaptionSensitivity.Meta metaBean() {
    return SwaptionSensitivity.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the convention of the swap for which the data is valid.
   * @return the value of the property, not null
   */
  public FixedIborSwapConvention getConvention() {
    return convention;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiry date/time of the option.
   * @return the value of the property, not null
   */
  public ZonedDateTime getExpiry() {
    return expiry;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying swap tenor.
   * @return the value of the property
   */
  public double getTenor() {
    return tenor;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the swaption strike rate.
   * @return the value of the property
   */
  public double getStrike() {
    return strike;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying swap forward rate.
   * @return the value of the property
   */
  public double getForward() {
    return forward;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency of the sensitivity.
   * @return the value of the property, not null
   */
  @Override
  public Currency getCurrency() {
    return currency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the value of the sensitivity.
   * @return the value of the property
   */
  @Override
  public double getSensitivity() {
    return sensitivity;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SwaptionSensitivity other = (SwaptionSensitivity) obj;
      return JodaBeanUtils.equal(getConvention(), other.getConvention()) &&
          JodaBeanUtils.equal(getExpiry(), other.getExpiry()) &&
          JodaBeanUtils.equal(getTenor(), other.getTenor()) &&
          JodaBeanUtils.equal(getStrike(), other.getStrike()) &&
          JodaBeanUtils.equal(getForward(), other.getForward()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getSensitivity(), other.getSensitivity());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getConvention());
    hash = hash * 31 + JodaBeanUtils.hashCode(getExpiry());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTenor());
    hash = hash * 31 + JodaBeanUtils.hashCode(getStrike());
    hash = hash * 31 + JodaBeanUtils.hashCode(getForward());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSensitivity());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(256);
    buf.append("SwaptionSensitivity{");
    buf.append("convention").append('=').append(getConvention()).append(',').append(' ');
    buf.append("expiry").append('=').append(getExpiry()).append(',').append(' ');
    buf.append("tenor").append('=').append(getTenor()).append(',').append(' ');
    buf.append("strike").append('=').append(getStrike()).append(',').append(' ');
    buf.append("forward").append('=').append(getForward()).append(',').append(' ');
    buf.append("currency").append('=').append(getCurrency()).append(',').append(' ');
    buf.append("sensitivity").append('=').append(JodaBeanUtils.toString(getSensitivity()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SwaptionSensitivity}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code convention} property.
     */
    private final MetaProperty<FixedIborSwapConvention> convention = DirectMetaProperty.ofImmutable(
        this, "convention", SwaptionSensitivity.class, FixedIborSwapConvention.class);
    /**
     * The meta-property for the {@code expiry} property.
     */
    private final MetaProperty<ZonedDateTime> expiry = DirectMetaProperty.ofImmutable(
        this, "expiry", SwaptionSensitivity.class, ZonedDateTime.class);
    /**
     * The meta-property for the {@code tenor} property.
     */
    private final MetaProperty<Double> tenor = DirectMetaProperty.ofImmutable(
        this, "tenor", SwaptionSensitivity.class, Double.TYPE);
    /**
     * The meta-property for the {@code strike} property.
     */
    private final MetaProperty<Double> strike = DirectMetaProperty.ofImmutable(
        this, "strike", SwaptionSensitivity.class, Double.TYPE);
    /**
     * The meta-property for the {@code forward} property.
     */
    private final MetaProperty<Double> forward = DirectMetaProperty.ofImmutable(
        this, "forward", SwaptionSensitivity.class, Double.TYPE);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> currency = DirectMetaProperty.ofImmutable(
        this, "currency", SwaptionSensitivity.class, Currency.class);
    /**
     * The meta-property for the {@code sensitivity} property.
     */
    private final MetaProperty<Double> sensitivity = DirectMetaProperty.ofImmutable(
        this, "sensitivity", SwaptionSensitivity.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "convention",
        "expiry",
        "tenor",
        "strike",
        "forward",
        "currency",
        "sensitivity");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
        case -1289159373:  // expiry
          return expiry;
        case 110246592:  // tenor
          return tenor;
        case -891985998:  // strike
          return strike;
        case -677145915:  // forward
          return forward;
        case 575402001:  // currency
          return currency;
        case 564403871:  // sensitivity
          return sensitivity;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SwaptionSensitivity> builder() {
      return new SwaptionSensitivity.Builder();
    }

    @Override
    public Class<? extends SwaptionSensitivity> beanType() {
      return SwaptionSensitivity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code convention} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FixedIborSwapConvention> convention() {
      return convention;
    }

    /**
     * The meta-property for the {@code expiry} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ZonedDateTime> expiry() {
      return expiry;
    }

    /**
     * The meta-property for the {@code tenor} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> tenor() {
      return tenor;
    }

    /**
     * The meta-property for the {@code strike} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> strike() {
      return strike;
    }

    /**
     * The meta-property for the {@code forward} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> forward() {
      return forward;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> currency() {
      return currency;
    }

    /**
     * The meta-property for the {@code sensitivity} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> sensitivity() {
      return sensitivity;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return ((SwaptionSensitivity) bean).getConvention();
        case -1289159373:  // expiry
          return ((SwaptionSensitivity) bean).getExpiry();
        case 110246592:  // tenor
          return ((SwaptionSensitivity) bean).getTenor();
        case -891985998:  // strike
          return ((SwaptionSensitivity) bean).getStrike();
        case -677145915:  // forward
          return ((SwaptionSensitivity) bean).getForward();
        case 575402001:  // currency
          return ((SwaptionSensitivity) bean).getCurrency();
        case 564403871:  // sensitivity
          return ((SwaptionSensitivity) bean).getSensitivity();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code SwaptionSensitivity}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<SwaptionSensitivity> {

    private FixedIborSwapConvention convention;
    private ZonedDateTime expiry;
    private double tenor;
    private double strike;
    private double forward;
    private Currency currency;
    private double sensitivity;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
        case -1289159373:  // expiry
          return expiry;
        case 110246592:  // tenor
          return tenor;
        case -891985998:  // strike
          return strike;
        case -677145915:  // forward
          return forward;
        case 575402001:  // currency
          return currency;
        case 564403871:  // sensitivity
          return sensitivity;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          this.convention = (FixedIborSwapConvention) newValue;
          break;
        case -1289159373:  // expiry
          this.expiry = (ZonedDateTime) newValue;
          break;
        case 110246592:  // tenor
          this.tenor = (Double) newValue;
          break;
        case -891985998:  // strike
          this.strike = (Double) newValue;
          break;
        case -677145915:  // forward
          this.forward = (Double) newValue;
          break;
        case 575402001:  // currency
          this.currency = (Currency) newValue;
          break;
        case 564403871:  // sensitivity
          this.sensitivity = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public SwaptionSensitivity build() {
      return new SwaptionSensitivity(
          convention,
          expiry,
          tenor,
          strike,
          forward,
          currency,
          sensitivity);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(256);
      buf.append("SwaptionSensitivity.Builder{");
      buf.append("convention").append('=').append(JodaBeanUtils.toString(convention)).append(',').append(' ');
      buf.append("expiry").append('=').append(JodaBeanUtils.toString(expiry)).append(',').append(' ');
      buf.append("tenor").append('=').append(JodaBeanUtils.toString(tenor)).append(',').append(' ');
      buf.append("strike").append('=').append(JodaBeanUtils.toString(strike)).append(',').append(' ');
      buf.append("forward").append('=').append(JodaBeanUtils.toString(forward)).append(',').append(' ');
      buf.append("currency").append('=').append(JodaBeanUtils.toString(currency)).append(',').append(' ');
      buf.append("sensitivity").append('=').append(JodaBeanUtils.toString(sensitivity));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
