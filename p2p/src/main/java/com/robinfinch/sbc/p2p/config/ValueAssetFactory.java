package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.AssetFactory;

public class ValueAssetFactory implements AssetFactory {

    @Override
    public Asset createAsset() {
        return new ValueAsset();
    }

    @Override
    public Asset createAssetForId(String id) throws ConfigurationException {
        throw new ConfigurationException("Can't create unique asset");
    }

    @Override
    public Asset createAssetForValue(int value) throws ConfigurationException {
        return new ValueAsset(value, 0);
    }

    @Override
    public boolean allowsAssetIntroduction() {
        return false;
    }

    @Override
    public String marshall(Asset asset) {
        return asset.toString();
    }

    @Override
    public Asset unmarshall(String asset) {
        int i = asset.indexOf(':');
        int value = Integer.parseInt(asset.substring(0, i));
        int change = Integer.parseInt(asset.substring(i + 1));
        return new ValueAsset(value, change);
    }
}
