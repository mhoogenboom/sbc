package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.network.Network;

public class LimitedValuePolicy extends UnlimitedValuePolicy {

    private final int totalValue;

    public LimitedValuePolicy(int introductionValue, int totalValue, AssetFactory assetFactory) {
        super(introductionValue, assetFactory);
        this.totalValue = totalValue;
    }

    @Override
    public boolean canIntroduceAsset(int ledgerSize) throws ConfigurationException {
        if (assetFactory.allowsAssetIntroduction()) {
            return false;
        } else {
            ledgerSize++;
            ValueAsset introductionAsset = (ValueAsset) assetFactory.createAssetForValue(introductionValue);
            Asset totalAsset = assetFactory.createAssetForValue(totalValue);
            return totalAsset.covers(introductionAsset.times(ledgerSize), 1 * ledgerSize); // todo
        }
    }
}
