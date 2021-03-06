/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import static com.opengamma.strata.collect.Guavate.toImmutableList;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import org.joda.beans.BeanBuilder;

/**
 * A curve node ID for a futures node.
 * <p>
 * The node is identified by the year and month of the futures expiry.
 */
@BeanDefinition(builderScope = "private")
public final class FuturesExpiryCurveNodeId
    implements CurveNodeId, ImmutableBean, Serializable {

  /**
   * The year and month of the futures expiry date.
   */
  @PropertyDefinition(validate = "notNull")
  private final YearMonth expiry;

  //-------------------------------------------------------------------------
  /**
   * Returns an ID for a curve node for futures that expire in the specified year and month.
   *
   * @param year  the year of the futures expiry
   * @param month  the month of the futures expiry
   * @return an ID for a curve node for futures that expire in the specified year and month
   */
  public static FuturesExpiryCurveNodeId of(int year, int month) {
    return new FuturesExpiryCurveNodeId(YearMonth.of(year, month));
  }

  /**
   * Returns an ID for a curve node for futures that expire in the specified year and month.
   *
   * @param expiry  the year and month of the futures expiry
   * @return an ID for a curve node for futures that expire in the specified year and month
   */
  public static FuturesExpiryCurveNodeId of(YearMonth expiry) {
    return new FuturesExpiryCurveNodeId(expiry);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a list of IDs for futures with the specified expiries.
   *
   * @param expiries  the expiries of the other curve nodes
   * @return IDs for curve nodes for futures with the specified expiries
   */
  public static List<FuturesExpiryCurveNodeId> listOf(YearMonth... expiries) {
    return Arrays.stream(expiries).map(FuturesExpiryCurveNodeId::of).collect(toImmutableList());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FuturesExpiryCurveNodeId}.
   * @return the meta-bean, not null
   */
  public static FuturesExpiryCurveNodeId.Meta meta() {
    return FuturesExpiryCurveNodeId.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FuturesExpiryCurveNodeId.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private FuturesExpiryCurveNodeId(
      YearMonth expiry) {
    JodaBeanUtils.notNull(expiry, "expiry");
    this.expiry = expiry;
  }

  @Override
  public FuturesExpiryCurveNodeId.Meta metaBean() {
    return FuturesExpiryCurveNodeId.Meta.INSTANCE;
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
   * Gets the year and month of the futures expiry date.
   * @return the value of the property, not null
   */
  public YearMonth getExpiry() {
    return expiry;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FuturesExpiryCurveNodeId other = (FuturesExpiryCurveNodeId) obj;
      return JodaBeanUtils.equal(getExpiry(), other.getExpiry());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getExpiry());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("FuturesExpiryCurveNodeId{");
    buf.append("expiry").append('=').append(JodaBeanUtils.toString(getExpiry()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FuturesExpiryCurveNodeId}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code expiry} property.
     */
    private final MetaProperty<YearMonth> expiry = DirectMetaProperty.ofImmutable(
        this, "expiry", FuturesExpiryCurveNodeId.class, YearMonth.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "expiry");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1289159373:  // expiry
          return expiry;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FuturesExpiryCurveNodeId> builder() {
      return new FuturesExpiryCurveNodeId.Builder();
    }

    @Override
    public Class<? extends FuturesExpiryCurveNodeId> beanType() {
      return FuturesExpiryCurveNodeId.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code expiry} property.
     * @return the meta-property, not null
     */
    public MetaProperty<YearMonth> expiry() {
      return expiry;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1289159373:  // expiry
          return ((FuturesExpiryCurveNodeId) bean).getExpiry();
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
   * The bean-builder for {@code FuturesExpiryCurveNodeId}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<FuturesExpiryCurveNodeId> {

    private YearMonth expiry;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1289159373:  // expiry
          return expiry;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1289159373:  // expiry
          this.expiry = (YearMonth) newValue;
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
    public FuturesExpiryCurveNodeId build() {
      return new FuturesExpiryCurveNodeId(
          expiry);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("FuturesExpiryCurveNodeId.Builder{");
      buf.append("expiry").append('=').append(JodaBeanUtils.toString(expiry));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
