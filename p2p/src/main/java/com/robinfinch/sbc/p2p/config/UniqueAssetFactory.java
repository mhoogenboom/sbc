package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.AssetFactory;

public class UniqueAssetFactory implements AssetFactory {

    @Override
    public Asset createAsset() {
        return new UniqueAsset();
    }

    @Override
    public Asset createAssetForId(String id) throws ConfigurationException {
        return new UniqueAsset(id);
    }

    @Override
    public Asset createAssetForValue(int value) throws ConfigurationException {
        throw new ConfigurationException("Can't create value asset");
    }

    @Override
    public boolean allowsAssetIntroduction() {
        return true;
    }

    @Override
    public String marshall(Asset asset) {
        return asset.toString();
    }

    @Override
    public Asset unmarshall(String asset) {
        return new UniqueAsset(asset);
    }
}
