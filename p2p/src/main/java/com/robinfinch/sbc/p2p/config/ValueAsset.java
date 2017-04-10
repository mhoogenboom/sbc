package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.Transaction;

public class ValueAsset implements Asset {

    private final int value;
    private final int change;

    public ValueAsset() {
        this(0, 0);
    }

    public ValueAsset(int value, int change) {
        this.value = value;
        this.change = change;
    }

    public int getValue() {
        return value;
    }

    public int getChange() {
        return change;
    }

    @Override
    public boolean isValid() {
        return (value > 0) && (change >= 0);
    }

    @Override
    public ValueAsset plus(String userId, Transaction transaction) {
        ValueAsset a = (ValueAsset) transaction.getAsset();
        if (userId.equals(transaction.getTo())) {
            return new ValueAsset(value + a.value, change);
        } else {
            return new ValueAsset(value + a.change, change);
        }
    }

    @Override
    public ValueAsset times(int multiplier) {
        return new ValueAsset(multiplier * value, multiplier * change);
    }

    @Override
    public ValueAsset computeChange(Asset asset, int fee) {
        ValueAsset a = (ValueAsset) asset;
        return new ValueAsset(a.value, value + change - a.value - fee);
    }

    @Override
    public boolean hasChange() {
        return (change > 0);
    }

    @Override
    public boolean covers(Asset asset) {
        ValueAsset a = (ValueAsset) asset;
        return this.value >= a.value + a.change;
    }

    @Override
    public boolean covers(Asset asset, int fee) {
        ValueAsset a = (ValueAsset) asset;
        return this.value >= a.value + a.change + fee;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(value)
                .append(':')
                .append(change)
                .toString();
    }
}
