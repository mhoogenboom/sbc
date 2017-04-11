package com.robinfinch.sbc.core.network;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Transaction;

public interface Node {

    void onTransactionPublished(Transaction transaction);

    void onBlockPublished(Block block);

    Block provideBlock(Hash hash);

}
