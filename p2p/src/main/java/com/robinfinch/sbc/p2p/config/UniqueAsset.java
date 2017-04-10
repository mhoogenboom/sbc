package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.util.Objects;

public class UniqueAsset implements Asset {

    private final String id;

    public UniqueAsset() {
        this(null);
    }

    public UniqueAsset(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean isValid() {
        return id != null;
    }

    @Override
    public UniqueAsset plus(String userId, Transaction transaction) {
        return null;
    }

    @Override
    public UniqueAsset times(int multiplier) {
        return null;
    }

    @Override
    public UniqueAsset computeChange(Asset asset, int fee) {
        return null;
    }

    @Override
    public boolean hasChange() {
        return false;
    }

    @Override
    public boolean covers(Asset asset) {
        UniqueAsset a = (UniqueAsset) asset;
        return Objects.equals(this.id, a.id);
    }

    @Override
    public boolean covers(Asset asset, int fee) {
        return (fee == 0) && covers(asset);
    }

    @Override
    public String toString() {
        return id;
    }
}
