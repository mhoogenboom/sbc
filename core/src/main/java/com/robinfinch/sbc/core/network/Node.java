package com.robinfinch.sbc.core.network;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Entry;

public interface Node {

    void onEntryPublished(Entry entry);

    void onBlockPublished(Block block);

    Block provideBlock(Hash hash);

}
