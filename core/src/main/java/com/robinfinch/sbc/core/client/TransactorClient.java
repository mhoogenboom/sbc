package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.LedgerStore;
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
    public synchronized void onEntryPublished(Entry entry) {
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
