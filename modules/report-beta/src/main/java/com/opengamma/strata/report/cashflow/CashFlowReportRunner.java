/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.report.cashflow;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.engine.Column;
import com.opengamma.strata.engine.config.Measure;
import com.opengamma.strata.market.explain.ExplainKey;
import com.opengamma.strata.market.explain.ExplainMap;
import com.opengamma.strata.report.Report;
import com.opengamma.strata.report.ReportCalculationResults;
import com.opengamma.strata.report.ReportRequirements;
import com.opengamma.strata.report.ReportRunner;

/**
 * Report runner for cash flow reports.
 */
public class CashFlowReportRunner implements ReportRunner<CashFlowReportTemplate> {

  // TODO - when the cashflow report INI file supports specific columns, the following maps should
  // be represented by a built-in report template INI file.
  
  private static final ExplainKey<?> INTERIM_AMOUNT_KEY = ExplainKey.of("InterimAmount");
  
  private static final Map<ExplainKey<?>, String> HEADER_MAP = ImmutableMap.of(
      ExplainKey.ENTRY_TYPE, "Flow Type",
      ExplainKey.ENTRY_INDEX, "Leg Number",
      ExplainKey.FUTURE_VALUE, "Flow Amount");
  
  private static final List<ExplainKey<?>> COLUMN_ORDER = ImmutableList.<ExplainKey<?>>builder()
      .add(ExplainKey.ENTRY_TYPE)
      .add(ExplainKey.ENTRY_INDEX)
      .add(ExplainKey.LEG_TYPE)
      .add(ExplainKey.PAY_RECEIVE)
      .add(ExplainKey.PAYMENT_CURRENCY)
      .add(ExplainKey.NOTIONAL)
      .add(ExplainKey.TRADE_NOTIONAL)
      .add(ExplainKey.UNADJUSTED_START_DATE)
      .add(ExplainKey.UNADJUSTED_END_DATE)
      .add(ExplainKey.START_DATE)
      .add(ExplainKey.END_DATE)
      .add(ExplainKey.FIXED_RATE)
      .add(ExplainKey.INDEX)
      .add(ExplainKey.FIXING_DATE)
      .add(ExplainKey.INDEX_VALUE)
      .add(ExplainKey.GEARING)
      .add(ExplainKey.SPREAD)
      .add(ExplainKey.WEIGHT)
      .add(ExplainKey.COMBINED_RATE)
      .add(ExplainKey.PAY_OFF_RATE)
      .add(ExplainKey.UNADJUSTED_PAYMENT_DATE)
      .add(ExplainKey.PAYMENT_DATE)
      .add(ExplainKey.ACCRUAL_DAYS)
      .add(ExplainKey.ACCRUAL_DAY_COUNT)
      .add(ExplainKey.ACCRUAL_YEAR_FRACTION)
      .add(ExplainKey.COMPOUNDING)
      .add(ExplainKey.UNIT_AMOUNT)
      .add(INTERIM_AMOUNT_KEY)
      .add(ExplainKey.FUTURE_VALUE)
      .add(ExplainKey.DISCOUNT_FACTOR)
      .add(ExplainKey.PRESENT_VALUE)
      .build();
  
  private static final List<ExplainKey<?>> INHERITED_KEYS = ImmutableList.<ExplainKey<?>>builder()
      .add(ExplainKey.ENTRY_INDEX)
      .add(ExplainKey.LEG_TYPE)
      .add(ExplainKey.PAY_RECEIVE)
      .build();
  
  @Override
  public ReportRequirements requirements(CashFlowReportTemplate reportTemplate) {
    return ReportRequirements.builder()
        .tradeMeasureRequirements(Column.of(Measure.EXPLAIN_PRESENT_VALUE))
        .build();
  }

  @Override
  public Report runReport(ReportCalculationResults calculationResults, CashFlowReportTemplate reportTemplate) {
    
    int tradeCount = calculationResults.getCalculationResults().getRowCount();
    if (tradeCount == 0) {
      throw new IllegalArgumentException("Calculation results is empty");
    }
    if (tradeCount > 1) {
      throw new IllegalArgumentException(
          Messages.format("Unable to show cashflow report for {} trades at once. " +
              "Please filter the portfolio to a single trade.", tradeCount));
    }
    
    int columnIdx = calculationResults.getColumns().indexOf(Column.of(Measure.EXPLAIN_PRESENT_VALUE));
    if (columnIdx == -1) {
      throw new IllegalArgumentException(
          Messages.format("Unable to find column for required measure '{}' in calculation results",
              Measure.EXPLAIN_PRESENT_VALUE));
    }
    
    Result<?> result = calculationResults.getCalculationResults().get(0, columnIdx);
    if (result.isFailure()) {
      throw new IllegalArgumentException(
          Messages.format("Failure result found for required measure '{}': {}",
              Measure.EXPLAIN_PRESENT_VALUE, result.getFailure().getMessage()));
    }
    ExplainMap explainMap = (ExplainMap) result.getValue();
    
    return runReport(explainMap, calculationResults.getValuationDate());
  }

  private Report runReport(ExplainMap explainMap, LocalDate valuationDate) {
    List<ExplainMap> flatMap = flatten(explainMap);
    
    List<ExplainKey<?>> keys = getKeys(flatMap);
    String[] headers = keys.stream()
        .map(k -> mapHeader(k))
        .toArray(size -> new String[size]);

    Object[][] data = getData(flatMap, keys);

    return CashFlowReport.builder()
        .runInstant(Instant.now())
        .valuationDate(valuationDate)
        .columnKeys(keys)
        .columnHeaders(headers)
        .data(data)
        .build();
  }

  private List<ExplainMap> flatten(ExplainMap explainMap) {
    List<ExplainMap> flattenedMap = new ArrayList<ExplainMap>();
    flatten(explainMap, false, ImmutableMap.of(), Maps.newHashMap(), 0, flattenedMap);
    return flattenedMap;
  }

  @SuppressWarnings("unchecked")
  private void flatten(ExplainMap explainMap, boolean parentVisible, Map<ExplainKey<?>, Object> parentRow, Map<ExplainKey<?>, Object> currentRow, int level, List<ExplainMap> accumulator) {
    
    boolean hasParentFlow = currentRow.containsKey(ExplainKey.FUTURE_VALUE);
    boolean isFlow = explainMap.get(ExplainKey.PAYMENT_DATE).isPresent();
    boolean visible = parentVisible || isFlow;
    
    Set<ExplainKey<List<ExplainMap>>> nestedListKeys = explainMap.getMap().keySet().stream()
        .filter(k -> List.class.isAssignableFrom(explainMap.get(k).get().getClass()))
        .map(k -> (ExplainKey<List<ExplainMap>>) k)
        .collect(Collectors.toSet());
    
    // Populate the base data
    for (Map.Entry<ExplainKey<?>, Object> entry : explainMap.getMap().entrySet()) {
      ExplainKey<?> key = entry.getKey();
      if (nestedListKeys.contains(key)) {
        continue;
      }
      if (key.equals(ExplainKey.FUTURE_VALUE)) {
        if (hasParentFlow) {
          // Collapsed rows, so flow must be the same as we already have
          continue;
        } else if (isFlow) {
          // This is first child flow row, so flow is equal to, and replaces, calculated amount
          currentRow.remove(INTERIM_AMOUNT_KEY);
        }
      }
      ExplainKey<?> mappedKey = mapKey(key, isFlow);
      Object mappedValue = mapValue(mappedKey, entry.getValue(), level);
      if (isFlow) {
        currentRow.put(mappedKey, mappedValue);
      } else {
        currentRow.putIfAbsent(mappedKey, mappedValue);
      }
    }
    
    // Repeat the inherited entries from the parent row if this row hasn't overridden them
    for (ExplainKey<?> inheritedKey : INHERITED_KEYS) {
      if (parentRow.containsKey(inheritedKey)) {
        currentRow.putIfAbsent(inheritedKey, parentRow.get(inheritedKey));
      }
    }
    
    if (nestedListKeys.size() > 0) {
      List<ExplainMap> nestedListEntries = nestedListKeys.stream()
          .flatMap(k -> explainMap.get(k).get().stream())
          .sorted(this::compareNestedEntries)
          .collect(Collectors.toList());

      if (nestedListEntries.size() == 1) {
        // Soak it up into this row
        flatten(nestedListEntries.get(0), visible, currentRow, currentRow, level, accumulator);
      } else {
        // Add child rows
        for (int i = 0; i < nestedListEntries.size(); i++) {
          flatten(nestedListEntries.get(i), visible, currentRow, Maps.newHashMap(), level + 1, accumulator);
        }
        // Add parent row after child rows (parent flows are a result of the children)
        if (visible) {
          accumulator.add(ExplainMap.of(currentRow));
        }
      }
    } else {
      if (visible) {
        accumulator.add(ExplainMap.of(currentRow));
      }
    }
  }

  private int compareNestedEntries(ExplainMap m1, ExplainMap m2) {
    Optional<LocalDate> paymentDate1 = m1.get(ExplainKey.PAYMENT_DATE);
    Optional<LocalDate> paymentDate2 = m2.get(ExplainKey.PAYMENT_DATE);
    if (paymentDate1.isPresent() && paymentDate1.isPresent()) {
      return paymentDate1.get().compareTo(paymentDate2.get());
    }
    if (!paymentDate2.isPresent()) {
      return paymentDate1.isPresent() ? 1 : 0;
    }
    return -1;
  }
  
  private ExplainKey<?> mapKey(ExplainKey<?> key, boolean isFlow) {
    if (!isFlow && key.equals(ExplainKey.FUTURE_VALUE)) {
      return INTERIM_AMOUNT_KEY;
    }
    return key;
  }
  
  private Object mapValue(ExplainKey<?> key, Object value, int level) {
    if (ExplainKey.ENTRY_TYPE.equals(key) && level > 0) {
      return humanizeUpperCamelCase((String) value);
    }
    return value;
  }
  
  private String mapHeader(ExplainKey<?> key) {
    String header = HEADER_MAP.get(key);
    if (header != null) {
      return header;
    }
    return humanizeUpperCamelCase(key.toString());
  }

  private String humanizeUpperCamelCase(String str) {
    StringBuilder buf = new StringBuilder(str.length() + 4);
    int lastIndex = 0;
    for (int i = 2; i < str.length(); i++) {
      char cur = str.charAt(i);
      char last = str.charAt(i - 1);
      if (Character.getType(last) == Character.UPPERCASE_LETTER && Character.getType(cur) == Character.LOWERCASE_LETTER) {
        buf.append(str.substring(lastIndex, i - 1)).append(' ');
        lastIndex = i - 1;
      }
    }
    buf.append(str.substring(lastIndex));
    return buf.toString();
  }

  private List<ExplainKey<?>> getKeys(List<ExplainMap> explainMap) {
    return explainMap.stream()
        .flatMap(m -> m.getMap().keySet().stream())
        .distinct()
        .sorted((k1, k2) -> COLUMN_ORDER.indexOf(k1) - COLUMN_ORDER.indexOf(k2))
        .collect(Collectors.toList());
  }

  private Object[][] getData(List<ExplainMap> flatMap, List<ExplainKey<?>> keys) {
    Object[][] data = new Object[flatMap.size()][keys.size()];
    for (int rowIdx = 0; rowIdx < data.length; rowIdx++) {
      ExplainMap rowMap = flatMap.get(rowIdx);
      for (int colIdx = 0; colIdx < keys.size(); colIdx++) {
        data[rowIdx][colIdx] = rowMap.get(keys.get(colIdx));
      }
    }
    return data;
  }

}
