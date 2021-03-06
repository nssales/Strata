/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.future;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
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

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.Product;

/**
 * A generic futures contract based on an expiration month.
 * <p>
 * A future is a financial instrument that is based on the future value of an underlying.
 * The buyer is typically required to purchase the underlying at a price fixed in advance.
 * This class represents the structure of a single futures contract.
 * <p>
 * For example, an airline can use a future to lock in the price of jet fuel in advance.
 * The airline will lose on the trade if jet fuel drops in price, but gain on the trade
 * if jet fuel rises in price.
 */
@BeanDefinition
public final class GenericFuture
    implements Product, ImmutableBean, Serializable {

  /**
   * The base product identifier.
   * <p>
   * The identifier that is used for the base product, also known as the symbol.
   * A future typically expires monthly or quarterly, thus the product referred to here
   * is the base product of a series of contracts. A unique identifier for the contract is formed
   * by combining the base product and the expiration month.
   * For example, 'Eurex~FGBL' could be used to refer to the Euro-Bund base product at Eurex.
   */
  @PropertyDefinition(validate = "notNull")
  private final StandardId productId;
  /**
   * The expiration month.
   * <p>
   * The month used to identify the expiration of the future.
   * When the future expires, trading stops.
   * <p>
   * Futures expire on a specific date, but as there is typically only one contract per month,
   * the month is used to refer to the future. Note that it is possible for the expiration
   * date to be in a different calendar month to that used to refer to the future.
   */
  @PropertyDefinition(validate = "notNull")
  private final YearMonth expirationMonth;
  /**
   * The expiration date, optional.
   * <p>
   * This is the date that the future expires.
   * A generic future is intended to be used for futures that expire monthly or quarterly.
   * As such, the {@code expirationMonth} field is used to identify the contract and this
   * date is primarily for information.
   */
  @PropertyDefinition(get = "optional")
  private final LocalDate expirationDate;
  /**
   * The size of each tick.
   * <p>
   * The tick size is defined as a decimal number.
   * If the tick size is 1/32, the tick size would be 0.03125.
   */
  @PropertyDefinition
  private final double tickSize;
  /**
   * The monetary value of one tick.
   * <p>
   * When the price changes by one tick, this amount is gained/lost.
   */
  @PropertyDefinition(validate = "notNull")
  private final CurrencyAmount tickValue;

  //-----------------------------------------------------------------------
  /**
   * Gets the currency of the future.
   * <p>
   * The currency is derived from the tick value.
   * 
   * @return the currency
   */
  public Currency getCurrency() {
    return tickValue.getCurrency();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code GenericFuture}.
   * @return the meta-bean, not null
   */
  public static GenericFuture.Meta meta() {
    return GenericFuture.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(GenericFuture.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static GenericFuture.Builder builder() {
    return new GenericFuture.Builder();
  }

  private GenericFuture(
      StandardId productId,
      YearMonth expirationMonth,
      LocalDate expirationDate,
      double tickSize,
      CurrencyAmount tickValue) {
    JodaBeanUtils.notNull(productId, "productId");
    JodaBeanUtils.notNull(expirationMonth, "expirationMonth");
    JodaBeanUtils.notNull(tickValue, "tickValue");
    this.productId = productId;
    this.expirationMonth = expirationMonth;
    this.expirationDate = expirationDate;
    this.tickSize = tickSize;
    this.tickValue = tickValue;
  }

  @Override
  public GenericFuture.Meta metaBean() {
    return GenericFuture.Meta.INSTANCE;
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
   * Gets the base product identifier.
   * <p>
   * The identifier that is used for the base product, also known as the symbol.
   * A future typically expires monthly or quarterly, thus the product referred to here
   * is the base product of a series of contracts. A unique identifier for the contract is formed
   * by combining the base product and the expiration month.
   * For example, 'Eurex~FGBL' could be used to refer to the Euro-Bund base product at Eurex.
   * @return the value of the property, not null
   */
  public StandardId getProductId() {
    return productId;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiration month.
   * <p>
   * The month used to identify the expiration of the future.
   * When the future expires, trading stops.
   * <p>
   * Futures expire on a specific date, but as there is typically only one contract per month,
   * the month is used to refer to the future. Note that it is possible for the expiration
   * date to be in a different calendar month to that used to refer to the future.
   * @return the value of the property, not null
   */
  public YearMonth getExpirationMonth() {
    return expirationMonth;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiration date, optional.
   * <p>
   * This is the date that the future expires.
   * A generic future is intended to be used for futures that expire monthly or quarterly.
   * As such, the {@code expirationMonth} field is used to identify the contract and this
   * date is primarily for information.
   * @return the optional value of the property, not null
   */
  public Optional<LocalDate> getExpirationDate() {
    return Optional.ofNullable(expirationDate);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the size of each tick.
   * <p>
   * The tick size is defined as a decimal number.
   * If the tick size is 1/32, the tick size would be 0.03125.
   * @return the value of the property
   */
  public double getTickSize() {
    return tickSize;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the monetary value of one tick.
   * <p>
   * When the price changes by one tick, this amount is gained/lost.
   * @return the value of the property, not null
   */
  public CurrencyAmount getTickValue() {
    return tickValue;
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
      GenericFuture other = (GenericFuture) obj;
      return JodaBeanUtils.equal(getProductId(), other.getProductId()) &&
          JodaBeanUtils.equal(getExpirationMonth(), other.getExpirationMonth()) &&
          JodaBeanUtils.equal(expirationDate, other.expirationDate) &&
          JodaBeanUtils.equal(getTickSize(), other.getTickSize()) &&
          JodaBeanUtils.equal(getTickValue(), other.getTickValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getProductId());
    hash = hash * 31 + JodaBeanUtils.hashCode(getExpirationMonth());
    hash = hash * 31 + JodaBeanUtils.hashCode(expirationDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(getTickSize());
    hash = hash * 31 + JodaBeanUtils.hashCode(getTickValue());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("GenericFuture{");
    buf.append("productId").append('=').append(getProductId()).append(',').append(' ');
    buf.append("expirationMonth").append('=').append(getExpirationMonth()).append(',').append(' ');
    buf.append("expirationDate").append('=').append(expirationDate).append(',').append(' ');
    buf.append("tickSize").append('=').append(getTickSize()).append(',').append(' ');
    buf.append("tickValue").append('=').append(JodaBeanUtils.toString(getTickValue()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code GenericFuture}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code productId} property.
     */
    private final MetaProperty<StandardId> productId = DirectMetaProperty.ofImmutable(
        this, "productId", GenericFuture.class, StandardId.class);
    /**
     * The meta-property for the {@code expirationMonth} property.
     */
    private final MetaProperty<YearMonth> expirationMonth = DirectMetaProperty.ofImmutable(
        this, "expirationMonth", GenericFuture.class, YearMonth.class);
    /**
     * The meta-property for the {@code expirationDate} property.
     */
    private final MetaProperty<LocalDate> expirationDate = DirectMetaProperty.ofImmutable(
        this, "expirationDate", GenericFuture.class, LocalDate.class);
    /**
     * The meta-property for the {@code tickSize} property.
     */
    private final MetaProperty<Double> tickSize = DirectMetaProperty.ofImmutable(
        this, "tickSize", GenericFuture.class, Double.TYPE);
    /**
     * The meta-property for the {@code tickValue} property.
     */
    private final MetaProperty<CurrencyAmount> tickValue = DirectMetaProperty.ofImmutable(
        this, "tickValue", GenericFuture.class, CurrencyAmount.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "productId",
        "expirationMonth",
        "expirationDate",
        "tickSize",
        "tickValue");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1051830678:  // productId
          return productId;
        case 750402833:  // expirationMonth
          return expirationMonth;
        case -668811523:  // expirationDate
          return expirationDate;
        case 1936822078:  // tickSize
          return tickSize;
        case -85538348:  // tickValue
          return tickValue;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public GenericFuture.Builder builder() {
      return new GenericFuture.Builder();
    }

    @Override
    public Class<? extends GenericFuture> beanType() {
      return GenericFuture.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code productId} property.
     * @return the meta-property, not null
     */
    public MetaProperty<StandardId> productId() {
      return productId;
    }

    /**
     * The meta-property for the {@code expirationMonth} property.
     * @return the meta-property, not null
     */
    public MetaProperty<YearMonth> expirationMonth() {
      return expirationMonth;
    }

    /**
     * The meta-property for the {@code expirationDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> expirationDate() {
      return expirationDate;
    }

    /**
     * The meta-property for the {@code tickSize} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> tickSize() {
      return tickSize;
    }

    /**
     * The meta-property for the {@code tickValue} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurrencyAmount> tickValue() {
      return tickValue;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1051830678:  // productId
          return ((GenericFuture) bean).getProductId();
        case 750402833:  // expirationMonth
          return ((GenericFuture) bean).getExpirationMonth();
        case -668811523:  // expirationDate
          return ((GenericFuture) bean).expirationDate;
        case 1936822078:  // tickSize
          return ((GenericFuture) bean).getTickSize();
        case -85538348:  // tickValue
          return ((GenericFuture) bean).getTickValue();
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
   * The bean-builder for {@code GenericFuture}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<GenericFuture> {

    private StandardId productId;
    private YearMonth expirationMonth;
    private LocalDate expirationDate;
    private double tickSize;
    private CurrencyAmount tickValue;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(GenericFuture beanToCopy) {
      this.productId = beanToCopy.getProductId();
      this.expirationMonth = beanToCopy.getExpirationMonth();
      this.expirationDate = beanToCopy.expirationDate;
      this.tickSize = beanToCopy.getTickSize();
      this.tickValue = beanToCopy.getTickValue();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1051830678:  // productId
          return productId;
        case 750402833:  // expirationMonth
          return expirationMonth;
        case -668811523:  // expirationDate
          return expirationDate;
        case 1936822078:  // tickSize
          return tickSize;
        case -85538348:  // tickValue
          return tickValue;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1051830678:  // productId
          this.productId = (StandardId) newValue;
          break;
        case 750402833:  // expirationMonth
          this.expirationMonth = (YearMonth) newValue;
          break;
        case -668811523:  // expirationDate
          this.expirationDate = (LocalDate) newValue;
          break;
        case 1936822078:  // tickSize
          this.tickSize = (Double) newValue;
          break;
        case -85538348:  // tickValue
          this.tickValue = (CurrencyAmount) newValue;
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
    public GenericFuture build() {
      return new GenericFuture(
          productId,
          expirationMonth,
          expirationDate,
          tickSize,
          tickValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the base product identifier.
     * <p>
     * The identifier that is used for the base product, also known as the symbol.
     * A future typically expires monthly or quarterly, thus the product referred to here
     * is the base product of a series of contracts. A unique identifier for the contract is formed
     * by combining the base product and the expiration month.
     * For example, 'Eurex~FGBL' could be used to refer to the Euro-Bund base product at Eurex.
     * @param productId  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder productId(StandardId productId) {
      JodaBeanUtils.notNull(productId, "productId");
      this.productId = productId;
      return this;
    }

    /**
     * Sets the expiration month.
     * <p>
     * The month used to identify the expiration of the future.
     * When the future expires, trading stops.
     * <p>
     * Futures expire on a specific date, but as there is typically only one contract per month,
     * the month is used to refer to the future. Note that it is possible for the expiration
     * date to be in a different calendar month to that used to refer to the future.
     * @param expirationMonth  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder expirationMonth(YearMonth expirationMonth) {
      JodaBeanUtils.notNull(expirationMonth, "expirationMonth");
      this.expirationMonth = expirationMonth;
      return this;
    }

    /**
     * Sets the expiration date, optional.
     * <p>
     * This is the date that the future expires.
     * A generic future is intended to be used for futures that expire monthly or quarterly.
     * As such, the {@code expirationMonth} field is used to identify the contract and this
     * date is primarily for information.
     * @param expirationDate  the new value
     * @return this, for chaining, not null
     */
    public Builder expirationDate(LocalDate expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    /**
     * Sets the size of each tick.
     * <p>
     * The tick size is defined as a decimal number.
     * If the tick size is 1/32, the tick size would be 0.03125.
     * @param tickSize  the new value
     * @return this, for chaining, not null
     */
    public Builder tickSize(double tickSize) {
      this.tickSize = tickSize;
      return this;
    }

    /**
     * Sets the monetary value of one tick.
     * <p>
     * When the price changes by one tick, this amount is gained/lost.
     * @param tickValue  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder tickValue(CurrencyAmount tickValue) {
      JodaBeanUtils.notNull(tickValue, "tickValue");
      this.tickValue = tickValue;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("GenericFuture.Builder{");
      buf.append("productId").append('=').append(JodaBeanUtils.toString(productId)).append(',').append(' ');
      buf.append("expirationMonth").append('=').append(JodaBeanUtils.toString(expirationMonth)).append(',').append(' ');
      buf.append("expirationDate").append('=').append(JodaBeanUtils.toString(expirationDate)).append(',').append(' ');
      buf.append("tickSize").append('=').append(JodaBeanUtils.toString(tickSize)).append(',').append(' ');
      buf.append("tickValue").append('=').append(JodaBeanUtils.toString(tickValue));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
