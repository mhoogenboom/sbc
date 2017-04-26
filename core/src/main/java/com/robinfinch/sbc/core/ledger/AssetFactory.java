package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;

public interface AssetFactory {

    Asset createAsset();

    Asset createAssetForId(String id) throws ConfigurationException;

    Asset createAssetForValue(int value) throws ConfigurationException;

    boolean allowsAssetIntroduction();

    String marshall(Asset asset);

    Asset unmarshall(String asset);
}
