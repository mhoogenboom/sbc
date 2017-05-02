package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.network.Node;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class Client implements Node {

    protected final UserStore userStore;
    protected final LedgerStore ledgerStore;
    protected final LedgerUpdater ledgerUpdater;
    protected final Network network;

    public Client(UserStore userStore, LedgerStore ledgerStore, LedgerUpdater ledgerUpdater, Network network) {
        this.userStore = userStore;
        this.ledgerStore = ledgerStore;
        this.ledgerUpdater = ledgerUpdater;
        this.network = network;
    }

    public synchronized void init(String userId) throws ConfigurationException {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

            KeyPair kp = kpg.generateKeyPair();

            User user = new User(userId, kp);

            userStore.store(user);

            Ledger ledger = new EmptyLedger();

            ledgerStore.store(ledger);

            network.register(user.getIdentity());

        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Public/private key algorithm RSA not supported", e);
        }
    }

    public abstract void start();

    public abstract void stop();

    public synchronized List<Entry> getWallet(int confirmationLevel) {

        User user = userStore.load();

        Ledger ledger = ledgerStore.load();

        return ledger.findUnspent(user.getId(), confirmationLevel);
    }

    protected synchronized boolean publish(Transaction transaction) throws ConfigurationException {

        User user = userStore.load();

        Entry entry = new Entry.Builder()
                .withTransaction(transaction)
                .withTimestamp(network.getTime())
                .build();

        Entry signedEntry = user.sign(entry);

        Ledger ledger = ledgerStore.load();

        if (signedEntry.verify(ledger, false, false)) {
            network.publish(signedEntry);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized Block provideBlock(Hash hash) {

        Ledger ledger = ledgerStore.load().findChainEndingAt(hash);

        return (ledger == null) ? null : ledger.getEndOfChain();
    }
}
