package com.robinfinch.sbc.core.network;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Entry;

public interface Network {

    void register(Identity identity);

    Identity requestIdentity(String userId);

    void register(Node node);

    void deregister();

    void publish(Entry entry);

    void publish(Block block);

    Block requestBlock(Hash hash);

    long getTime();
}
