/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import java.io.Serializable;
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

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.Index;

/**
 * A group of curves.
 * <p>
 * This is used to hold a group of related curves, typically forming a logical set.
 * It is often used to hold the results of a curve calibration.
 */
@BeanDefinition
public final class CurveGroup
    implements ImmutableBean, Serializable {

  /**
   * The name of the curve group.
   */
  @PropertyDefinition(validate = "notNull")
  private final CurveGroupName name;
  /**
   * The discount curves in the group, keyed by currency.
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableMap<Currency, Curve> discountCurves;
  /**
   * The forward curves in the group, keyed by index.
   */
  @PropertyDefinition(validate = "notNull", builderType = "Map<? extends Index, Curve>")
  private final ImmutableMap<Index, Curve> forwardCurves;

  //-------------------------------------------------------------------------
  /**
   * Returns a curve group containing the specified curves.
   *
   * @param name  the name of the curve group
   * @param discountCurves  the discount curves, keyed by currency
   * @param forwardCurves  the forward curves, keyed by index
   * @return a curve group containing the specified curves
   */
  public static CurveGroup of(CurveGroupName name, Map<Currency, Curve> discountCurves, Map<Index, Curve> forwardCurves) {
    return new CurveGroup(name, discountCurves, forwardCurves);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns the discount curve for the currency if there is one in the group.
   *
   * @param currency  the currency for which a discount curve is required
   * @return the discount curve for the currency if there is one in the group
   */
  public Optional<Curve> getDiscountCurve(Currency currency) {
    return Optional.ofNullable(discountCurves.get(currency));
  }

  /**
   * Returns the forward curve for the index if there is one in the group.
   *
   * @param index  the index for which a forward curve is required
   * @return the forward curve for the index if there is one in the group
   */
  public Optional<Curve> getForwardCurve(Index index) {
    return Optional.ofNullable(forwardCurves.get(index));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CurveGroup}.
   * @return the meta-bean, not null
   */
  public static CurveGroup.Meta meta() {
    return CurveGroup.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CurveGroup.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CurveGroup.Builder builder() {
    return new CurveGroup.Builder();
  }

  private CurveGroup(
      CurveGroupName name,
      Map<Currency, Curve> discountCurves,
      Map<? extends Index, Curve> forwardCurves) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(discountCurves, "discountCurves");
    JodaBeanUtils.notNull(forwardCurves, "forwardCurves");
    this.name = name;
    this.discountCurves = ImmutableMap.copyOf(discountCurves);
    this.forwardCurves = ImmutableMap.copyOf(forwardCurves);
  }

  @Override
  public CurveGroup.Meta metaBean() {
    return CurveGroup.Meta.INSTANCE;
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
   * Gets the name of the curve group.
   * @return the value of the property, not null
   */
  public CurveGroupName getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the discount curves in the group, keyed by currency.
   * @return the value of the property, not null
   */
  public ImmutableMap<Currency, Curve> getDiscountCurves() {
    return discountCurves;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the forward curves in the group, keyed by index.
   * @return the value of the property, not null
   */
  public ImmutableMap<Index, Curve> getForwardCurves() {
    return forwardCurves;
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
      CurveGroup other = (CurveGroup) obj;
      return JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getDiscountCurves(), other.getDiscountCurves()) &&
          JodaBeanUtils.equal(getForwardCurves(), other.getForwardCurves());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getDiscountCurves());
    hash = hash * 31 + JodaBeanUtils.hashCode(getForwardCurves());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("CurveGroup{");
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("discountCurves").append('=').append(getDiscountCurves()).append(',').append(' ');
    buf.append("forwardCurves").append('=').append(JodaBeanUtils.toString(getForwardCurves()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CurveGroup}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<CurveGroupName> name = DirectMetaProperty.ofImmutable(
        this, "name", CurveGroup.class, CurveGroupName.class);
    /**
     * The meta-property for the {@code discountCurves} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableMap<Currency, Curve>> discountCurves = DirectMetaProperty.ofImmutable(
        this, "discountCurves", CurveGroup.class, (Class) ImmutableMap.class);
    /**
     * The meta-property for the {@code forwardCurves} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableMap<Index, Curve>> forwardCurves = DirectMetaProperty.ofImmutable(
        this, "forwardCurves", CurveGroup.class, (Class) ImmutableMap.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "discountCurves",
        "forwardCurves");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -624113147:  // discountCurves
          return discountCurves;
        case -850086775:  // forwardCurves
          return forwardCurves;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CurveGroup.Builder builder() {
      return new CurveGroup.Builder();
    }

    @Override
    public Class<? extends CurveGroup> beanType() {
      return CurveGroup.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveGroupName> name() {
      return name;
    }

    /**
     * The meta-property for the {@code discountCurves} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableMap<Currency, Curve>> discountCurves() {
      return discountCurves;
    }

    /**
     * The meta-property for the {@code forwardCurves} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableMap<Index, Curve>> forwardCurves() {
      return forwardCurves;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((CurveGroup) bean).getName();
        case -624113147:  // discountCurves
          return ((CurveGroup) bean).getDiscountCurves();
        case -850086775:  // forwardCurves
          return ((CurveGroup) bean).getForwardCurves();
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
   * The bean-builder for {@code CurveGroup}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<CurveGroup> {

    private CurveGroupName name;
    private Map<Currency, Curve> discountCurves = ImmutableMap.of();
    private Map<? extends Index, Curve> forwardCurves = ImmutableMap.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(CurveGroup beanToCopy) {
      this.name = beanToCopy.getName();
      this.discountCurves = beanToCopy.getDiscountCurves();
      this.forwardCurves = beanToCopy.getForwardCurves();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -624113147:  // discountCurves
          return discountCurves;
        case -850086775:  // forwardCurves
          return forwardCurves;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (CurveGroupName) newValue;
          break;
        case -624113147:  // discountCurves
          this.discountCurves = (Map<Currency, Curve>) newValue;
          break;
        case -850086775:  // forwardCurves
          this.forwardCurves = (Map<? extends Index, Curve>) newValue;
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
    public CurveGroup build() {
      return new CurveGroup(
          name,
          discountCurves,
          forwardCurves);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the name of the curve group.
     * @param name  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder name(CurveGroupName name) {
      JodaBeanUtils.notNull(name, "name");
      this.name = name;
      return this;
    }

    /**
     * Sets the discount curves in the group, keyed by currency.
     * @param discountCurves  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder discountCurves(Map<Currency, Curve> discountCurves) {
      JodaBeanUtils.notNull(discountCurves, "discountCurves");
      this.discountCurves = discountCurves;
      return this;
    }

    /**
     * Sets the forward curves in the group, keyed by index.
     * @param forwardCurves  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder forwardCurves(Map<? extends Index, Curve> forwardCurves) {
      JodaBeanUtils.notNull(forwardCurves, "forwardCurves");
      this.forwardCurves = forwardCurves;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("CurveGroup.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("discountCurves").append('=').append(JodaBeanUtils.toString(discountCurves)).append(',').append(' ');
      buf.append("forwardCurves").append('=').append(JodaBeanUtils.toString(forwardCurves));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
