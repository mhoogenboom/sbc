package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.core.network.IncentivePolicy;

public class BlockSizePolicy implements IncentivePolicy {

    private final int maxBlockSize;

    public BlockSizePolicy(int maxBlockSize) {
        this.maxBlockSize = maxBlockSize;
    }

    @Override
    public boolean canIntroduceAsset(int ledgerSize) {
        return false;
    }

    @Override
    public Transaction introduceAsset(String userId, long now) throws ConfigurationException {
        throw new ConfigurationException("Can't introduce value");
    }

    @Override
    public boolean verifyIntroductionTransaction(String userId, Transaction transaction) {
        return false;
    }

    @Override
    public boolean verifyFee(int fee) {
        return (fee == 0);
    }

    @Override
    public boolean canChargeFees() {
        return false;
    }

    @Override
    public Transaction chargeFees(String userId, int fees, long now) throws ConfigurationException {
        throw new ConfigurationException("Can't charge fees");
    }

    @Override
    public boolean verifyFeeTransaction(String userId, int fees, Transaction transaction) {
        return false;
    }

    @Override
    public int getMaxBlockSize() {
        return maxBlockSize;
    }
}
