/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve.config;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.joda.beans.Bean;
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

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.basics.index.OvernightIndex;

/**
 * An item in the configuration for a curve group, containing the configuration for a curve and the market data keys
 * identifying how the curve is used.
 * <p>
 * A curve can be used for multiple purposes and therefore the curve itself contains no information about how
 * it is used.
 * <p>
 * In the simple case a curve is only used for a single purpose. For example, if a curve is used for discounting
 * it will have one key of type {@code DiscountCurveKey}.
 * <p>
 * A single curve can also be used as both a discounting curve and a forward curve.
 * In that case its key set would contain a {@code DiscountCurveKey} and a {@code RateIndexCurveKey}.
 * <p>
 * Every curve must be associated with at least once key.
 */
@BeanDefinition
public final class CurveGroupEntry implements ImmutableBean {

  /** The configuration of the curve. */
  @PropertyDefinition(validate = "notNull")
  private final CurveConfig curveConfig;

  /** The currency for which the curve provides discount rates, not present if it is not used for discounting. */
  @PropertyDefinition(get = "optional")
  private final Currency discountingCurrency;

  /** The IBOR indices for which the curve provides forward rates. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<IborIndex> iborIndices;

  /** The overnight indices for which the curve provides forward rates. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableSet<OvernightIndex> overnightIndices;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CurveGroupEntry}.
   * @return the meta-bean, not null
   */
  public static CurveGroupEntry.Meta meta() {
    return CurveGroupEntry.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CurveGroupEntry.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CurveGroupEntry.Builder builder() {
    return new CurveGroupEntry.Builder();
  }

  private CurveGroupEntry(
      CurveConfig curveConfig,
      Currency discountingCurrency,
      Set<IborIndex> iborIndices,
      Set<OvernightIndex> overnightIndices) {
    JodaBeanUtils.notNull(curveConfig, "curveConfig");
    JodaBeanUtils.notNull(iborIndices, "iborIndices");
    JodaBeanUtils.notNull(overnightIndices, "overnightIndices");
    this.curveConfig = curveConfig;
    this.discountingCurrency = discountingCurrency;
    this.iborIndices = ImmutableSet.copyOf(iborIndices);
    this.overnightIndices = ImmutableSet.copyOf(overnightIndices);
  }

  @Override
  public CurveGroupEntry.Meta metaBean() {
    return CurveGroupEntry.Meta.INSTANCE;
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
   * Gets the configuration of the curve.
   * @return the value of the property, not null
   */
  public CurveConfig getCurveConfig() {
    return curveConfig;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency for which the curve provides discount rates, not present if it is not used for discounting.
   * @return the optional value of the property, not null
   */
  public Optional<Currency> getDiscountingCurrency() {
    return Optional.ofNullable(discountingCurrency);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the IBOR indices for which the curve provides forward rates.
   * @return the value of the property, not null
   */
  public ImmutableSet<IborIndex> getIborIndices() {
    return iborIndices;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the overnight indices for which the curve provides forward rates.
   * @return the value of the property, not null
   */
  public ImmutableSet<OvernightIndex> getOvernightIndices() {
    return overnightIndices;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CurveGroupEntry other = (CurveGroupEntry) obj;
      return JodaBeanUtils.equal(getCurveConfig(), other.getCurveConfig()) &&
          JodaBeanUtils.equal(discountingCurrency, other.discountingCurrency) &&
          JodaBeanUtils.equal(getIborIndices(), other.getIborIndices()) &&
          JodaBeanUtils.equal(getOvernightIndices(), other.getOvernightIndices());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getCurveConfig());
    hash = hash * 31 + JodaBeanUtils.hashCode(discountingCurrency);
    hash = hash * 31 + JodaBeanUtils.hashCode(getIborIndices());
    hash = hash * 31 + JodaBeanUtils.hashCode(getOvernightIndices());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("CurveGroupEntry{");
    buf.append("curveConfig").append('=').append(getCurveConfig()).append(',').append(' ');
    buf.append("discountingCurrency").append('=').append(discountingCurrency).append(',').append(' ');
    buf.append("iborIndices").append('=').append(getIborIndices()).append(',').append(' ');
    buf.append("overnightIndices").append('=').append(JodaBeanUtils.toString(getOvernightIndices()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CurveGroupEntry}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code curveConfig} property.
     */
    private final MetaProperty<CurveConfig> curveConfig = DirectMetaProperty.ofImmutable(
        this, "curveConfig", CurveGroupEntry.class, CurveConfig.class);
    /**
     * The meta-property for the {@code discountingCurrency} property.
     */
    private final MetaProperty<Currency> discountingCurrency = DirectMetaProperty.ofImmutable(
        this, "discountingCurrency", CurveGroupEntry.class, Currency.class);
    /**
     * The meta-property for the {@code iborIndices} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<IborIndex>> iborIndices = DirectMetaProperty.ofImmutable(
        this, "iborIndices", CurveGroupEntry.class, (Class) ImmutableSet.class);
    /**
     * The meta-property for the {@code overnightIndices} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableSet<OvernightIndex>> overnightIndices = DirectMetaProperty.ofImmutable(
        this, "overnightIndices", CurveGroupEntry.class, (Class) ImmutableSet.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "curveConfig",
        "discountingCurrency",
        "iborIndices",
        "overnightIndices");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2042609937:  // curveConfig
          return curveConfig;
        case 1852985906:  // discountingCurrency
          return discountingCurrency;
        case -118808757:  // iborIndices
          return iborIndices;
        case 1523471171:  // overnightIndices
          return overnightIndices;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CurveGroupEntry.Builder builder() {
      return new CurveGroupEntry.Builder();
    }

    @Override
    public Class<? extends CurveGroupEntry> beanType() {
      return CurveGroupEntry.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code curveConfig} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveConfig> curveConfig() {
      return curveConfig;
    }

    /**
     * The meta-property for the {@code discountingCurrency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> discountingCurrency() {
      return discountingCurrency;
    }

    /**
     * The meta-property for the {@code iborIndices} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<IborIndex>> iborIndices() {
      return iborIndices;
    }

    /**
     * The meta-property for the {@code overnightIndices} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableSet<OvernightIndex>> overnightIndices() {
      return overnightIndices;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2042609937:  // curveConfig
          return ((CurveGroupEntry) bean).getCurveConfig();
        case 1852985906:  // discountingCurrency
          return ((CurveGroupEntry) bean).discountingCurrency;
        case -118808757:  // iborIndices
          return ((CurveGroupEntry) bean).getIborIndices();
        case 1523471171:  // overnightIndices
          return ((CurveGroupEntry) bean).getOvernightIndices();
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
   * The bean-builder for {@code CurveGroupEntry}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<CurveGroupEntry> {

    private CurveConfig curveConfig;
    private Currency discountingCurrency;
    private Set<IborIndex> iborIndices = ImmutableSet.of();
    private Set<OvernightIndex> overnightIndices = ImmutableSet.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(CurveGroupEntry beanToCopy) {
      this.curveConfig = beanToCopy.getCurveConfig();
      this.discountingCurrency = beanToCopy.discountingCurrency;
      this.iborIndices = beanToCopy.getIborIndices();
      this.overnightIndices = beanToCopy.getOvernightIndices();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2042609937:  // curveConfig
          return curveConfig;
        case 1852985906:  // discountingCurrency
          return discountingCurrency;
        case -118808757:  // iborIndices
          return iborIndices;
        case 1523471171:  // overnightIndices
          return overnightIndices;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2042609937:  // curveConfig
          this.curveConfig = (CurveConfig) newValue;
          break;
        case 1852985906:  // discountingCurrency
          this.discountingCurrency = (Currency) newValue;
          break;
        case -118808757:  // iborIndices
          this.iborIndices = (Set<IborIndex>) newValue;
          break;
        case 1523471171:  // overnightIndices
          this.overnightIndices = (Set<OvernightIndex>) newValue;
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
    public CurveGroupEntry build() {
      return new CurveGroupEntry(
          curveConfig,
          discountingCurrency,
          iborIndices,
          overnightIndices);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the configuration of the curve.
     * @param curveConfig  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder curveConfig(CurveConfig curveConfig) {
      JodaBeanUtils.notNull(curveConfig, "curveConfig");
      this.curveConfig = curveConfig;
      return this;
    }

    /**
     * Sets the currency for which the curve provides discount rates, not present if it is not used for discounting.
     * @param discountingCurrency  the new value
     * @return this, for chaining, not null
     */
    public Builder discountingCurrency(Currency discountingCurrency) {
      this.discountingCurrency = discountingCurrency;
      return this;
    }

    /**
     * Sets the IBOR indices for which the curve provides forward rates.
     * @param iborIndices  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder iborIndices(Set<IborIndex> iborIndices) {
      JodaBeanUtils.notNull(iborIndices, "iborIndices");
      this.iborIndices = iborIndices;
      return this;
    }

    /**
     * Sets the {@code iborIndices} property in the builder
     * from an array of objects.
     * @param iborIndices  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder iborIndices(IborIndex... iborIndices) {
      return iborIndices(ImmutableSet.copyOf(iborIndices));
    }

    /**
     * Sets the overnight indices for which the curve provides forward rates.
     * @param overnightIndices  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder overnightIndices(Set<OvernightIndex> overnightIndices) {
      JodaBeanUtils.notNull(overnightIndices, "overnightIndices");
      this.overnightIndices = overnightIndices;
      return this;
    }

    /**
     * Sets the {@code overnightIndices} property in the builder
     * from an array of objects.
     * @param overnightIndices  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder overnightIndices(OvernightIndex... overnightIndices) {
      return overnightIndices(ImmutableSet.copyOf(overnightIndices));
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(160);
      buf.append("CurveGroupEntry.Builder{");
      buf.append("curveConfig").append('=').append(JodaBeanUtils.toString(curveConfig)).append(',').append(' ');
      buf.append("discountingCurrency").append('=').append(JodaBeanUtils.toString(discountingCurrency)).append(',').append(' ');
      buf.append("iborIndices").append('=').append(JodaBeanUtils.toString(iborIndices)).append(',').append(' ');
      buf.append("overnightIndices").append('=').append(JodaBeanUtils.toString(overnightIndices));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
