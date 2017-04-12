package com.robinfinch.sbc.transactor;

import com.robinfinch.sbc.core.Client;
import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.Network;

public class TransactorClient extends Client {

    public TransactorClient(UserStore userStore, LedgerStore ledgerStore, Network network) {
        super(userStore, ledgerStore, new LedgerUpdater(network, false), network);
    }

    @Override
    public synchronized void start() {
        network.register(this);
    }

    @Override
    public synchronized void stop() {
        network.deregister();
    }

    @Override
    public synchronized void onTransactionPublished(Transaction transaction) {
        // ignore
    }

    @Override
    public synchronized void onBlockPublished(Block block) {

        try {
            Ledger ledger = ledgerStore.load();

            ledger = ledgerUpdater.update(ledger, block);

            ledgerStore.store(ledger);

        } catch (ConfigurationException e) {
            // todo
        }
    }
}
