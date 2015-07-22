/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.math.impl.curve;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.math.impl.function.Function;

/**
 * Class defining a spread curve, i.e. a curve that is the result of a mathematical operation
 * (see {@link CurveSpreadFunction}) on two or more curves.
 * For example, a simple spread curve could be <i>C = A - B</i>. As this curve is in the same
 * hierarchy as the other curves, a spread curve can be defined on another spread curve,
 * e.g. <i>E = C * D = D * (A - B)</i>.
 */
@BeanDefinition
public class SpreadDoublesCurve
    extends DoublesCurve {

  /**
   * The spread function.
   */
  @PropertyDefinition(get = "private", set = "private")
  private CurveSpreadFunction _spreadFunction;
  /**
   * The evaluated function.
   */
  @PropertyDefinition(get = "private", set = "private")
  private Function<Double, Double> _f;
  /**
   * The curves.
   */
  @PropertyDefinition(get = "private", set = "private")
  private DoublesCurve[] _curves;

  //-------------------------------------------------------------------------
  /**
   * Takes an array of curves that are to be operated on by the spread function.
   * The name of the spread curve is automatically generated.
   *
   * @param spreadFunction  the spread function, not null
   * @param curves  the curves, not null
   * @return the spread curve, not null
   */
  public static SpreadDoublesCurve from(final CurveSpreadFunction spreadFunction, final DoublesCurve... curves) {
    return new SpreadDoublesCurve(spreadFunction, curves);
  }

  /**
   * Takes an array of curves that are to be operated on by the spread function.
   *
   * @param spreadFunction  the spread function, not null
   * @param name  the name of the curve, not null
   * @param curves  the curves, not null
   * @return the spread curve, not null
   */
  public static SpreadDoublesCurve from(final CurveSpreadFunction spreadFunction, final String name, final DoublesCurve... curves) {
    return new SpreadDoublesCurve(spreadFunction, name, curves);
  }

  //-------------------------------------------------------------------------
  /**
   * Constructor for Joda-Beans.
   */
  protected SpreadDoublesCurve() {
  }

  /**
   * Creates a spread curve.
   *
   * @param spreadFunction  the spread function, not null
   * @param curves  the curves, not null, contains more than one curve, not null
   */
  public SpreadDoublesCurve(final CurveSpreadFunction spreadFunction, final DoublesCurve... curves) {
    super();
    ArgChecker.notNull(curves, "curves");
    ArgChecker.isTrue(curves.length > 1, "curves");
    ArgChecker.notNull(spreadFunction, "spread operator");
    _curves = curves;
    _spreadFunction = spreadFunction;
    _f = spreadFunction.evaluate(curves);
  }

  /**
   * Creates a spread curve.
   *
   * @param spreadFunction  the spread function, not null
   * @param name  the name of the curve, not null
   * @param curves  the curves, not null, contains more than one curve, not null
   */
  public SpreadDoublesCurve(final CurveSpreadFunction spreadFunction, final String name, final DoublesCurve... curves) {
    super(name);
    ArgChecker.notNull(curves, "curves");
    ArgChecker.isTrue(curves.length > 1, "curves");
    ArgChecker.notNull(spreadFunction, "spread operator");
    _curves = curves;
    _spreadFunction = spreadFunction;
    _f = spreadFunction.evaluate(curves);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a set of the <b>unique</b> names of the curves that were used to construct this curve.
   * If a constituent curve is a spread curve, then all of its underlyings are included.
   *
   * @return the set of underlying names, not null
   */
  public Set<String> getUnderlyingNames() {
    final Set<String> result = new HashSet<>();
    for (final DoublesCurve curve : _curves) {
      if (curve instanceof SpreadDoublesCurve) {
        result.addAll(((SpreadDoublesCurve) curve).getUnderlyingNames());
      } else {
        result.add(curve.getName());
      }
    }
    return result;
  }

  /**
   * Returns a string that represents the mathematical form of this curve.
   * For example, <i>D = (A + (B / C))</i>.
   *
   * @return the long name of this curve, not null
   */
  public String getLongName() {
    final StringBuilder buf = new StringBuilder(getName());
    buf.append("=");
    int i = 0;
    buf.append("(");
    for (final DoublesCurve curve : _curves) {
      if (curve instanceof SpreadDoublesCurve) {
        buf.append(((SpreadDoublesCurve) curve).getLongName().substring(2));
      } else {
        buf.append(curve.getName());
      }
      if (i != _curves.length - 1) {
        buf.append(_spreadFunction.getName());
      }
      i++;
    }
    buf.append(")");
    return buf.toString();
  }

  /**
   * Gets the underlying curves.
   *
   * @return the underlying curves, not null
   */
  public DoublesCurve[] getUnderlyingCurves() {
    return _curves;
  }

  /**
   * Throws an exception as there is no <i>x</i> data.
   *
   * @return throws UnsupportedOperationException
   * @throws UnsupportedOperationException always
   */
  @Override
  public Double[] getXData() {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an exception as there is no <i>y</i> data.
   *
   * @return throws UnsupportedOperationException
   * @throws UnsupportedOperationException always
   */
  @Override
  public Double[] getYData() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double getYValue(final Double x) {
    ArgChecker.notNull(x, "x");
    return _f.evaluate(x);
  }

  @Override
  public Double[] getYValueParameterSensitivity(final Double x) {
    if (_curves.length == 2) {
      if (_curves[0] instanceof InterpolatedDoublesCurve && _curves[1] instanceof ConstantDoublesCurve) {
        return _curves[0].getYValueParameterSensitivity(x);
      } else if (_curves[1] instanceof InterpolatedDoublesCurve && _curves[0] instanceof ConstantDoublesCurve) {
        return _curves[1].getYValueParameterSensitivity(x);
      }
    }
    throw new UnsupportedOperationException("Parameter sensitivity not supported yet for SpreadDoublesCurve");
  }

  @Override
  public double getDyDx(final double x) {
    throw new UnsupportedOperationException();
  }

  /**
   * Throws an exception as there is no <i>x</i> or <i>y</i> data.
   *
   * @return throws UnsupportedOperationException
   * @throws UnsupportedOperationException always
   */
  @Override
  public int size() {
    int size = 0;
    for (final DoublesCurve underlying : _curves) {
      if (underlying instanceof InterpolatedDoublesCurve || underlying instanceof NodalDoublesCurve || underlying instanceof SpreadDoublesCurve) {
        size += underlying.size();
      }
    }
    if (size != 0) {
      return size;
    }
    throw new UnsupportedOperationException("Size not supported for SpreadDoublesCurve " + getLongName());
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SpreadDoublesCurve other = (SpreadDoublesCurve) obj;
    if (!Arrays.equals(_curves, other._curves)) {
      return false;
    }
    return Objects.equals(_spreadFunction, other._spreadFunction);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Arrays.hashCode(_curves);
    result = prime * result + _spreadFunction.hashCode();
    return result;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SpreadDoublesCurve}.
   * @return the meta-bean, not null
   */
  public static SpreadDoublesCurve.Meta meta() {
    return SpreadDoublesCurve.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SpreadDoublesCurve.Meta.INSTANCE);
  }

  @Override
  public SpreadDoublesCurve.Meta metaBean() {
    return SpreadDoublesCurve.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the spread function.
   * @return the value of the property
   */
  private CurveSpreadFunction get_spreadFunction() {
    return _spreadFunction;
  }

  /**
   * Sets the spread function.
   * @param _spreadFunction  the new value of the property
   */
  private void set_spreadFunction(CurveSpreadFunction _spreadFunction) {
    this._spreadFunction = _spreadFunction;
  }

  /**
   * Gets the the {@code _spreadFunction} property.
   * @return the property, not null
   */
  public final Property<CurveSpreadFunction> _spreadFunction() {
    return metaBean()._spreadFunction().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the evaluated function.
   * @return the value of the property
   */
  private Function<Double, Double> get_f() {
    return _f;
  }

  /**
   * Sets the evaluated function.
   * @param _f  the new value of the property
   */
  private void set_f(Function<Double, Double> _f) {
    this._f = _f;
  }

  /**
   * Gets the the {@code _f} property.
   * @return the property, not null
   */
  public final Property<Function<Double, Double>> _f() {
    return metaBean()._f().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the curves.
   * @return the value of the property
   */
  private DoublesCurve[] get_curves() {
    return _curves;
  }

  /**
   * Sets the curves.
   * @param _curves  the new value of the property
   */
  private void set_curves(DoublesCurve[] _curves) {
    this._curves = _curves;
  }

  /**
   * Gets the the {@code _curves} property.
   * @return the property, not null
   */
  public final Property<DoublesCurve[]> _curves() {
    return metaBean()._curves().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public SpreadDoublesCurve clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("SpreadDoublesCurve{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("_spreadFunction").append('=').append(JodaBeanUtils.toString(get_spreadFunction())).append(',').append(' ');
    buf.append("_f").append('=').append(JodaBeanUtils.toString(get_f())).append(',').append(' ');
    buf.append("_curves").append('=').append(JodaBeanUtils.toString(get_curves())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SpreadDoublesCurve}.
   */
  public static class Meta extends DoublesCurve.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code _spreadFunction} property.
     */
    private final MetaProperty<CurveSpreadFunction> _spreadFunction = DirectMetaProperty.ofReadWrite(
        this, "_spreadFunction", SpreadDoublesCurve.class, CurveSpreadFunction.class);
    /**
     * The meta-property for the {@code _f} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Function<Double, Double>> _f = DirectMetaProperty.ofReadWrite(
        this, "_f", SpreadDoublesCurve.class, (Class) Function.class);
    /**
     * The meta-property for the {@code _curves} property.
     */
    private final MetaProperty<DoublesCurve[]> _curves = DirectMetaProperty.ofReadWrite(
        this, "_curves", SpreadDoublesCurve.class, DoublesCurve[].class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "_spreadFunction",
        "_f",
        "_curves");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 611325066:  // _spreadFunction
          return _spreadFunction;
        case 3047:  // _f
          return _f;
        case 1359354499:  // _curves
          return _curves;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SpreadDoublesCurve> builder() {
      return new DirectBeanBuilder<SpreadDoublesCurve>(new SpreadDoublesCurve());
    }

    @Override
    public Class<? extends SpreadDoublesCurve> beanType() {
      return SpreadDoublesCurve.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code _spreadFunction} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CurveSpreadFunction> _spreadFunction() {
      return _spreadFunction;
    }

    /**
     * The meta-property for the {@code _f} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Function<Double, Double>> _f() {
      return _f;
    }

    /**
     * The meta-property for the {@code _curves} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<DoublesCurve[]> _curves() {
      return _curves;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 611325066:  // _spreadFunction
          return ((SpreadDoublesCurve) bean).get_spreadFunction();
        case 3047:  // _f
          return ((SpreadDoublesCurve) bean).get_f();
        case 1359354499:  // _curves
          return ((SpreadDoublesCurve) bean).get_curves();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 611325066:  // _spreadFunction
          ((SpreadDoublesCurve) bean).set_spreadFunction((CurveSpreadFunction) newValue);
          return;
        case 3047:  // _f
          ((SpreadDoublesCurve) bean).set_f((Function<Double, Double>) newValue);
          return;
        case 1359354499:  // _curves
          ((SpreadDoublesCurve) bean).set_curves((DoublesCurve[]) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
