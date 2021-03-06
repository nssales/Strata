/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.fx;

import static java.lang.Math.signum;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.collect.ArgChecker;

/**
 * An expanded FX swap transaction, the low level representation of an FX swap.
 * <p>
 * An FX swap is a financial instrument that represents the exchange of an equivalent amount
 * in two different currencies between counterparties on two different dates.
 * <p>
 * An {@code ExpandedFxSwap} may contain information based on holiday calendars.
 * If a holiday calendar changes, the adjusted dates may no longer be correct.
 * Care must be taken when placing the expanded form in a cache or persistence layer.
 * Application code should use {@link FxSwap}, not this class.
 */
@BeanDefinition(builderScope = "private")
public final class ExpandedFxSwap
    implements FxSwapProduct, ImmutableBean, Serializable {

  /**
   * The foreign exchange transaction at the earlier date.
   * <p>
   * This provides details of a single foreign exchange at a specific date.
   * The payment date of this transaction must be before that of the far leg.
   */
  @PropertyDefinition(validate = "notNull")
  private final ExpandedFx nearLeg;
  /**
   * The foreign exchange transaction at the later date.
   * <p>
   * This provides details of a single foreign exchange at a specific date.
   * The payment date of this transaction must be after that of the near leg.
   */
  @PropertyDefinition(validate = "notNull")
  private final ExpandedFx farLeg;

  //-------------------------------------------------------------------------
  /**
   * Creates an {@code ExpandedFxSwap} from two legs.
   * <p>
   * The transactions must be passed in with payment dates in the correct order.
   * The currency pair of each leg must match and have amounts flowing in opposite directions.
   * 
   * @param nearLeg  the earlier leg
   * @param farLeg  the later leg
   * @return the expanded FX swap
   */
  public static ExpandedFxSwap of(ExpandedFx nearLeg, ExpandedFx farLeg) {
    return new ExpandedFxSwap(nearLeg, farLeg);
  }

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    ArgChecker.inOrderNotEqual(
        nearLeg.getPaymentDate(), farLeg.getPaymentDate(), "nearLeg.paymentDate", "farLeg.paymentDate");
    if (!nearLeg.getBaseCurrencyPayment().getCurrency().equals(farLeg.getBaseCurrencyPayment().getCurrency()) ||
        !nearLeg.getCounterCurrencyPayment().getCurrency().equals(farLeg.getCounterCurrencyPayment().getCurrency())) {
      throw new IllegalArgumentException("Legs must have the same currency pair");
    }
    if (signum(nearLeg.getBaseCurrencyPayment().getAmount()) == signum(farLeg.getBaseCurrencyPayment().getAmount())) {
      throw new IllegalArgumentException("Legs must have payments flowing in opposite directions");
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Expands this FX swap, trivially returning {@code this}.
   * 
   * @return this
   */
  @Override
  public ExpandedFxSwap expand() {
    return this;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ExpandedFxSwap}.
   * @return the meta-bean, not null
   */
  public static ExpandedFxSwap.Meta meta() {
    return ExpandedFxSwap.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ExpandedFxSwap.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private ExpandedFxSwap(
      ExpandedFx nearLeg,
      ExpandedFx farLeg) {
    JodaBeanUtils.notNull(nearLeg, "nearLeg");
    JodaBeanUtils.notNull(farLeg, "farLeg");
    this.nearLeg = nearLeg;
    this.farLeg = farLeg;
    validate();
  }

  @Override
  public ExpandedFxSwap.Meta metaBean() {
    return ExpandedFxSwap.Meta.INSTANCE;
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
   * Gets the foreign exchange transaction at the earlier date.
   * <p>
   * This provides details of a single foreign exchange at a specific date.
   * The payment date of this transaction must be before that of the far leg.
   * @return the value of the property, not null
   */
  public ExpandedFx getNearLeg() {
    return nearLeg;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the foreign exchange transaction at the later date.
   * <p>
   * This provides details of a single foreign exchange at a specific date.
   * The payment date of this transaction must be after that of the near leg.
   * @return the value of the property, not null
   */
  public ExpandedFx getFarLeg() {
    return farLeg;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ExpandedFxSwap other = (ExpandedFxSwap) obj;
      return JodaBeanUtils.equal(getNearLeg(), other.getNearLeg()) &&
          JodaBeanUtils.equal(getFarLeg(), other.getFarLeg());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getNearLeg());
    hash = hash * 31 + JodaBeanUtils.hashCode(getFarLeg());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ExpandedFxSwap{");
    buf.append("nearLeg").append('=').append(getNearLeg()).append(',').append(' ');
    buf.append("farLeg").append('=').append(JodaBeanUtils.toString(getFarLeg()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ExpandedFxSwap}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code nearLeg} property.
     */
    private final MetaProperty<ExpandedFx> nearLeg = DirectMetaProperty.ofImmutable(
        this, "nearLeg", ExpandedFxSwap.class, ExpandedFx.class);
    /**
     * The meta-property for the {@code farLeg} property.
     */
    private final MetaProperty<ExpandedFx> farLeg = DirectMetaProperty.ofImmutable(
        this, "farLeg", ExpandedFxSwap.class, ExpandedFx.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "nearLeg",
        "farLeg");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1825755334:  // nearLeg
          return nearLeg;
        case -1281739913:  // farLeg
          return farLeg;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ExpandedFxSwap> builder() {
      return new ExpandedFxSwap.Builder();
    }

    @Override
    public Class<? extends ExpandedFxSwap> beanType() {
      return ExpandedFxSwap.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code nearLeg} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ExpandedFx> nearLeg() {
      return nearLeg;
    }

    /**
     * The meta-property for the {@code farLeg} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ExpandedFx> farLeg() {
      return farLeg;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1825755334:  // nearLeg
          return ((ExpandedFxSwap) bean).getNearLeg();
        case -1281739913:  // farLeg
          return ((ExpandedFxSwap) bean).getFarLeg();
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
   * The bean-builder for {@code ExpandedFxSwap}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<ExpandedFxSwap> {

    private ExpandedFx nearLeg;
    private ExpandedFx farLeg;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1825755334:  // nearLeg
          return nearLeg;
        case -1281739913:  // farLeg
          return farLeg;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1825755334:  // nearLeg
          this.nearLeg = (ExpandedFx) newValue;
          break;
        case -1281739913:  // farLeg
          this.farLeg = (ExpandedFx) newValue;
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
    public ExpandedFxSwap build() {
      return new ExpandedFxSwap(
          nearLeg,
          farLeg);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("ExpandedFxSwap.Builder{");
      buf.append("nearLeg").append('=').append(JodaBeanUtils.toString(nearLeg)).append(',').append(' ');
      buf.append("farLeg").append('=').append(JodaBeanUtils.toString(farLeg));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
