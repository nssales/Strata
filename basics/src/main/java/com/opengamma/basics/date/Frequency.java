/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.basics.date;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;

import org.joda.convert.FromString;
import org.joda.convert.ToString;

import com.opengamma.collect.ArgChecker;

/**
 * A periodic frequency used by financial products that have a specific event every so often.
 * <p>
 * Frequency is primarily intended to be used to subdivide events within a year.
 * <p>
 * A frequency is allowed to be any non-negative period of days, weeks, month or years.
 * This class provides constants for common frequencies which are best used by static import.
 * <p>
 * A special value, 'Term', is provided for when there are no subdivisions of the entire term.
 * This is also know as 'zero-coupon' or 'once'. It is represented using the period 10,000 years,
 * which allows addition/subtraction to work, producing a date after the end of the term.
 * <p>
 * {@code Frequency} implements {@code TemporalAmount} allowing it to be directly added to a date.
 */
public class Frequency
    implements TemporalAmount, Serializable {

  /**
   * Serialization version.
   */
  private static final long serialVersionUID = 1;

  /**
   * A periodic frequency of one day.
   * Also known as daily.
   */
  public static final Frequency P1D = ofDays(1);
  /**
   * A periodic frequency of 1 week (7 days).
   * Also known as weekly.
   */
  public static final Frequency P1W = ofWeeks(1);
  /**
   * A periodic frequency of 2 weeks (14 days).
   * Also known as bi-weekly.
   */
  public static final Frequency P2W = ofWeeks(2);
  /**
   * A periodic frequency of 4 weeks (28 days).
   * Also known as lunar.
   */
  public static final Frequency P4W = ofWeeks(4);
  /**
   * A periodic frequency of 1 month.
   * Also known as monthly.
   */
  public static final Frequency P1M = ofMonths(1);
  /**
   * A periodic frequency of 2 months.
   * Also known as bi-monthly.
   */
  public static final Frequency P2M = ofMonths(2);
  /**
   * A periodic frequency of 3 months.
   * Also known as quarterly.
   */
  public static final Frequency P3M = ofMonths(3);
  /**
   * A periodic frequency of 4 months.
   */
  public static final Frequency P4M = ofMonths(4);
  /**
   * A periodic frequency of 6 months.
   * Also known as semi-annual.
   */
  public static final Frequency P6M = ofMonths(6);
  /**
   * A periodic frequency of 1 year.
   * Also known as annual.
   */
  public static final Frequency P1Y = ofYears(1);
  /**
   * A periodic frequency of 91 days.
   */
  public static final Frequency P91D = ofDays(91);
  /**
   * A periodic frequency of 182 days.
   */
  public static final Frequency P182D = ofDays(182);
  /**
   * A periodic frequency of 364 days.
   */
  public static final Frequency P364D = ofDays(364);
  /**
   * A periodic frequency matching the term.
   * Also known as zero-coupon.
   * This is represented using the period 10,000 years.
   */
  public static final Frequency TERM = new Frequency(Period.ofYears(10000), "Term");

  /**
   * The period of the frequency.
   */
  private final Period period;
  /**
   * The name of the frequency.
   */
  private final String name;

  //-------------------------------------------------------------------------
  /**
   * Obtains a periodic frequency from a {@code Period}.
   * <p>
   * The period normally consists of either days and weeks, or months and years.
   * It must also be positive and non-zero.
   *
   * @param period  the period to convert to a periodic frequency
   * @return the periodic frequency
   * @throws IllegalArgumentException if the period is negative or zero
   */
  public static Frequency of(Period period) {
    ArgChecker.notNull(period, "period");
    int days = period.getDays();
    long months = period.toTotalMonths();
    if (months == 120000 && days == 0) {
      return TERM;
    } else if (months != 0) {
      if (months > 12000) {
        throw new IllegalArgumentException("Total months must not exceed 12000");
      }
      if (days == 0) {
        return ofMonths(Math.toIntExact(months));
      } else {
        return new Frequency(period);
      }
    } else if (days != 0) {
      return ofDays(days);
    } else {
      throw new IllegalArgumentException("Period must not be zero");
    }
  }

  /**
   * Returns a periodic frequency backed by a period of days.
   * <p>
   * If the number of months is an exact multiple of 7 it will be converted to weeks.
   *
   * @param days  the number of days
   * @return the periodic frequency
   * @throws IllegalArgumentException if days is negative
   */
  public static Frequency ofDays(int days) {
    if (days % 7 == 0) {
      return ofWeeks(days / 7);
    }
    return new Frequency(Period.ofDays(days));
  }

  /**
   * Returns a periodic frequency backed by a period of weeks.
   *
   * @param weeks  the number of weeks
   * @return the periodic frequency
   * @throws IllegalArgumentException if weeks is negative
   */
  public static Frequency ofWeeks(int weeks) {
    return new Frequency(Period.ofWeeks(weeks), "P" + weeks + "W");
  }

  /**
   * Returns a periodic frequency backed by a period of months.
   * <p>
   * If the number of months is an exact multiple of 12 it will be converted to years.
   *
   * @param months  the number of months
   * @return the periodic frequency
   * @throws IllegalArgumentException if months is negative or over 12000
   */
  public static Frequency ofMonths(int months) {
    if (months > 12000) {
      throw new IllegalArgumentException("Months must not exceed 12000");
    }
    if (months % 12 == 0) {
      return ofYears(months / 12);
    }
    return new Frequency(Period.ofMonths(months));
  }

  /**
   * Returns a periodic frequency backed by a period of years.
   *
   * @param years  the number of years
   * @return the periodic frequency
   * @throws IllegalArgumentException if years is negative or over 1000
   */
  public static Frequency ofYears(int years) {
    if (years > 1000) {
      throw new IllegalArgumentException("Years must not exceed 1000");
    }
    return new Frequency(Period.ofYears(years));
  }

  //-------------------------------------------------------------------------
  /**
   * Parses a formatted string representing the frequency.
   * <p>
   * The format can either be based on ISO-8601, such as 'P3M'
   * or without the 'P' prefix e.g. '2W'.
   * <p>
   * The period must be positive and non-zero.
   *
   * @param toParse  the string representing the frequency
   * @return the frequency
   * @throws IllegalArgumentException if the frequency cannot be parsed
   */
  @FromString
  public static Frequency parse(String toParse) {
    ArgChecker.notNull(toParse, "toParse");
    if (toParse.equals("Term")) {
      return TERM;
    }
    String prefixed = toParse.startsWith("P") ? toParse : "P" + toParse;
    try {
      return Frequency.of(Period.parse(prefixed));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Creates a periodic frequency.
   *
   * @param period  the period to represent
   */
  private Frequency(Period period) {
    this(period, period.toString());
 }

  /**
   * Creates a periodic frequency.
   *
   * @param period  the period to represent
   * @param name  the name
   */
  private Frequency(Period period, String name) {
    ArgChecker.notNull(period, "period");
    ArgChecker.isFalse(period.isZero(), "Period must not be zero");
    ArgChecker.isFalse(period.isNegative(), "Period must not be negative");
    this.period = period;
    this.name = name;
  }

  // safe deserialization
  private Object readResolve() {
    if (this.equals(TERM)) {
      return TERM;
    }
    return of(period);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the underlying period of the frequency.
   *
   * @return the period
   */
  public Period getPeriod() {
    return period;
  }

  /**
   * Checks if the periodic frequency is the 'Term' instance.
   * <p>
   * The term instance corresponds to there being no subdivisions of the entire term.
   *
   * @return true if this is the 'Term' instance
   */
  public boolean isTerm() {
    return this == TERM;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the value of the specified unit.
   * <p>
   * This will return a value for the years, months and days units.
   * Note that weeks are not included.
   * All other units throw an exception.
   * <p>
   * The 'Term' period is returned as a period of 10,000 years.
   *
   * @param unit  the unit to query
   * @return the value of the unit
   * @throws UnsupportedTemporalTypeException if the unit is not supported
   */
  @Override
  public long get(TemporalUnit unit) {
    return period.get(unit);
  }

  /**
   * Gets the unit of this periodic frequency.
   * <p>
   * This returns a list containing years, months and days.
   * Note that weeks are not included.
   * <p>
   * The 'Term' period is returned as a period of 10,000 years.
   *
   * @return a list containing the years, months and days units
   */
  @Override
  public List<TemporalUnit> getUnits() {
    return period.getUnits();
  }

  /**
   * Adds the period of this frequency to the specified date.
   * <p>
   * This is an implementation method used by {@link LocalDate#plus(TemporalAmount)}.
   * See {@link Period#addTo(Temporal)} for more details.
   *
   * @param temporal  the temporal object to add to
   * @return the result with this frequency added
   * @throws DateTimeException if unable to add
   * @throws ArithmeticException if numeric overflow occurs
   */
  @Override
  public Temporal addTo(Temporal temporal) {
    return period.addTo(temporal);
  }

  /**
   * Subtracts the period of this frequency from the specified date.
   * <p>
   * This is an implementation method used by {@link LocalDate#minus(TemporalAmount)}.
   * See {@link Period#subtractFrom(Temporal)} for more details.
   *
   * @param temporal  the temporal object to subtract from
   * @return the result with this frequency subtracted
   * @throws DateTimeException if unable to subtract
   * @throws ArithmeticException if numeric overflow occurs
   */
  @Override
  public Temporal subtractFrom(Temporal temporal) {
    return period.subtractFrom(temporal);
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if this periodic frequency equals another periodic frequency.
   * <p>
   * The comparison checks the frequency period.
   * 
   * @param obj  the other frequency, null returns false
   * @return true if equal
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Frequency) {
      Frequency other = (Frequency) obj;
      return period.equals(other.period);
    }
    return false;
  }

  /**
   * Returns a suitable hash code for the periodic frequency.
   * 
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return period.hashCode();
  }

  /**
   * Returns a formatted string representing the periodic frequency.
   * <p>
   * The format is a combination of the quantity and unit, such as P1D, P2W, P3M, P4Y.
   * The 'Term' amount is returned as 'Term'.
   *
   * @return the formatted frequency
   */
  @ToString
  @Override
  public String toString() {
    return name;
  }

}