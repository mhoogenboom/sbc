package com.robinfinch.sbc.domain.transfer;

import com.robinfinch.sbc.core.client.AggregatorClient;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.core.network.Network;

public class Registrar extends AggregatorClient {

    private final int minimumBlockSize;

    private int counter;

    public Registrar(UserStore userStore, LedgerStore ledgerStore, Network network, int minimumBlockSize) {
        super(userStore, ledgerStore, network);
        this.minimumBlockSize = minimumBlockSize;
    }

    @Override
    protected void onNewBlockInProgress() {
        counter = 0;
    }

    @Override
    protected void onAddedToBlockInProgress(Transaction transaction) {
        counter++;
    }

    @Override
    protected void onRemovedFromBlockInProgress(Transaction transaction) {
        counter--;
    }

    @Override
    protected boolean shouldPublishBlockInProgress() {
        return counter >= minimumBlockSize;
    }
}
