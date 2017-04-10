package com.robinfinch.sbc.core.ledger;

public interface Asset {

    boolean isValid();

    Asset plus(String userId, Transaction transaction);

    Asset times(int multiplier);

    Asset computeChange(Asset asset, int fee);

    boolean hasChange();

    boolean covers(Asset asset);

    boolean covers(Asset asset, int fee);
}
