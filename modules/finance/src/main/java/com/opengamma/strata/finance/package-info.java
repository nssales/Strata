/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */

/**
 * Entity objects describing the domain of finance.
 * <p>
 * The trade model has three basic concepts, trades, securities and products.
 * <p>
 * A {@link com.opengamma.strata.finance.Trade Trade} is the basic element of finance,
 * a transaction between two organizations, known as counterparties.
 * Most trades represented in the system will be contracts that have been agreed on a date in the past.
 * The trade model also allows trades with a date in the future, or without any date.
 * This supports "what if" trades and aggregated trades, also known as <i>positions</i>.
 * <p>
 * A {@link com.opengamma.strata.finance.Security Security} is a standard contract that is traded,
 * such as an equity share or futures contract. Securities are typically created once and shared
 * using an identifier, represented by a {@link com.opengamma.strata.collect.id.StandardId StandardId}.
 * They are often referred to as <i>reference data</i>.
 * The standard implementation of {@code Security} is {@link com.opengamma.strata.finance.UnitSecurity UnitSecurity}.
 * <p>
 * A {@link com.opengamma.strata.finance.Product Product} is the financial details of the trade or security.
 * A product typically contains enough information to be priced, such as the dates, holidays, indices,
 * currencies and amounts. There is an implementation of {@code Product} for each distinct type
 * of financial instrument.
 * <p>
 * Trades are typically classified as Over-The-Counter (OTC) and listed.
 * <p>
 * An OTC trade directly embeds the product it refers to.
 * As such, OTC trades implement {@link com.opengamma.strata.finance.ProductTrade ProductTrade}.
 * <p>
 * For example, consider an OTC instrument such as an interest rate swap.
 * The object model consists of a {@link com.opengamma.strata.finance.rate.swap.SwapTrade SwapTrade}
 * that directly contains a {@link com.opengamma.strata.finance.rate.swap.Swap Swap},
 * where {@code SwapTrade} implements {@code ProductTrade}.
 * <p>
 * A listed trade contains a reference to the underlying security that is the basis of the trade.
 * Rather than holding the security directly, a {@link com.opengamma.strata.finance.SecurityLink SecurityLink}
 * is used to loosely connect the trade to the security. The link permits the security to either
 * be located externally, such as in a database, or to be embedded within the link.
 * The security contains details of the actual product.
 * <p>
 * For example, consider a trade in a listed equity.
 * The object model consists of a {@link com.opengamma.strata.finance.equity.EquityTrade EquityTrade}
 * that contains a link to a {@link com.opengamma.strata.finance.Security Security}.
 * The security will directly contain the underlying {@link com.opengamma.strata.finance.equity.Equity Equity}.
 * <p>
 * The key to understanding the model is appreciating the separation of products from trades and securities.
 * In many cases, it is possible to price the product without knowing any trade details.
 * This allows a product to be an underlying of another product, such as a swap within a swaption.
 * <p>
 * Note that on the listed side, it is often possible to price either against the market or against a model.
 * Details for pricing against the market are primarily held in the security.
 * Details for pricing against the model are primarily held in the product.
 */
package com.opengamma.strata.finance;

