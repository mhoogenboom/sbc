package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.core.network.IncentivePolicy;
import com.robinfinch.sbc.core.network.Network;

public class UnlimitedValuePolicy implements IncentivePolicy {

    protected final int introductionValue;
    protected final AssetFactory assetFactory;

    public UnlimitedValuePolicy(int introductionValue, AssetFactory assetFactory) {
        this.introductionValue = introductionValue;
        this.assetFactory = assetFactory;
    }

    @Override
    public boolean canIntroduceAsset(int ledgerSize)
            throws ConfigurationException {

        return !assetFactory.allowsAssetIntroduction();
    }

    @Override
    public Transaction introduceAsset(String userId, long now)
            throws ConfigurationException {

        Asset asset = assetFactory.createAssetForValue(introductionValue);

        Transaction transaction = new Transaction.Builder()
                .withTo(userId)
                .withAsset(asset)
                .withFee(1)  // todo remove
                .withTimestamp(now)
                .build();

        return transaction;
    }

    @Override
    public boolean verifyIntroductionTransaction(String userId, Transaction transaction)
            throws ConfigurationException {

        Asset asset = assetFactory.createAssetForValue(introductionValue);

        return (transaction.getFrom() == null)
                && transaction.getTo().equals(userId)
                && asset.covers(transaction.getAsset())
                && !transaction.getAsset().hasChange()
                && (transaction.getFee() == 1);  // todo: remove
    }

    @Override
    public boolean verifyFee(int fee) {
        return (fee >= 0);
    }

    @Override
    public boolean canChargeFees() {
        return true;
    }

    @Override
    public Transaction chargeFees(String userId, int fees, long now)
            throws ConfigurationException {

        Asset asset = assetFactory.createAssetForValue(fees);

        Transaction transaction = new Transaction.Builder()
                .withTo(userId)
                .withAsset(asset)
                .withTimestamp(now)
                .build();

        return transaction;
    }

    @Override
    public boolean verifyFeeTransaction(String userId, int fees, Transaction transaction)
            throws ConfigurationException {

        Asset asset = assetFactory.createAssetForValue(fees);

        return (transaction.getFrom() == null)
                && transaction.getTo().equals(userId)
                && asset.covers(transaction.getAsset())
                && !transaction.getAsset().hasChange()
                && (transaction.getFee() == 0);
    }

    @Override
    public int getMaxBlockSize() {
        return 0;
    }
}
