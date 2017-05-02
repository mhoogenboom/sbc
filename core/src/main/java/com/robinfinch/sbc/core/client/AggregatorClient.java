package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.worker.Worker;

public abstract class AggregatorClient extends Client {

    private final Worker worker;

    private Block.Builder blockBuilder;

    public AggregatorClient(UserStore userStore, LedgerStore ledgerStore, Network network) {

        super(userStore, ledgerStore, new LedgerUpdater(network, true), network);

        this.worker = new Worker();

        blockBuilder = new Block.Builder();
        onNewBlockInProgress();
    }

    @Override
    public synchronized void start() {

        User user = userStore.load();

        blockBuilder.withUserId(user.getId());

        network.register(this);
    }

    @Override
    public synchronized void stop() {
        network.deregister();
    }

    @Override
    public synchronized void onEntryPublished(Entry entry) {

        try {
            Ledger ledger = ledgerStore.load()
                    .append(blockBuilder.build());

            if (ledgerUpdater.verify(ledger, entry, false, false)) {

                blockBuilder.withTransaction(entry);
                onAddedToBlockInProgress(entry.getTransaction());

                if (shouldPublishBlockInProgress()) {
                    publishBlockInProgress();
                }
            }
        } catch (ConfigurationException e) {
            // todo
        }
    }

    @Override
    public synchronized void onBlockPublished(Block block) {

        try {
            Ledger ledger = ledgerStore.load();

            ledger = ledgerUpdater.update(ledger, block);

            ledgerStore.store(ledger);

            resetBlockInProgress(ledger);

        } catch (ConfigurationException e) {
            // todo
        }
    }

    private void publishBlockInProgress() throws ConfigurationException {

        Ledger ledger = ledgerStore.load();

        Block endOfChain = ledger.getEndOfChain();
        if (endOfChain != null) {
            blockBuilder.withPreviousHash(endOfChain.getHash());
        }

        // add intro

        // add fees

        blockBuilder.withTimestamp(network.getTime());

        Block block = worker.seal(blockBuilder);

        network.publish(block);

        ledger = ledger.append(block);

        ledgerStore.store(ledger);

        blockBuilder
                .withPreviousHash(null)
                .withoutTransactions(entry -> true);
        onNewBlockInProgress();
    }

    private void resetBlockInProgress(Ledger ledger) {

        blockBuilder
                .withoutTransactions(entry -> {
                    if (ledger.findWithHash(entry.getHash()).isPresent()) {
                        onRemovedFromBlockInProgress(entry.getTransaction());
                        return true;
                    } else {
                        return false;
                    }
                });
    }

    protected abstract void onNewBlockInProgress();

    protected abstract void onAddedToBlockInProgress(Transaction transaction);

    protected abstract void onRemovedFromBlockInProgress(Transaction transaction);

    protected abstract boolean shouldPublishBlockInProgress();
}
