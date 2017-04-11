package com.robinfinch.sbc.core.network;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Transaction;

public interface Network {

    void register(Identity identity);

    Identity requestIdentity(String userId);

    IncentivePolicy getIncentivePolicy();

    void register(Node node);

    void deregister();

    AssetFactory getAssetFactory();

    void publish(Transaction transaction);

    void publish(Block block);

    Block requestBlock(Hash hash);

    long getTime();
}
