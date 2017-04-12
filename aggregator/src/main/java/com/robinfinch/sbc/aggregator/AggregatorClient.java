package com.robinfinch.sbc.aggregator;

import com.robinfinch.sbc.core.Client;
import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.IncentivePolicy;
import com.robinfinch.sbc.core.network.Network;

public class AggregatorClient extends Client {

    private final int minimumFees;

    private Block.Builder blockBuilder;
    private int totalFees;

    public AggregatorClient(UserStore userStore, LedgerStore ledgerStore, int minimumFees, Network network) {

        super(userStore, ledgerStore, new LedgerUpdater(network, true), network);

        this.minimumFees = minimumFees;

        blockBuilder = new Block.Builder();
        totalFees = 0;
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

    public void publishEmptyBlock() {
        try {
            publishBlockInProgress();
        } catch (ConfigurationException e) {
            // todo
        }
    }

    @Override
    public synchronized void onTransactionPublished(Transaction transaction) {

        try {
            Ledger ledger = ledgerStore.load()
                    .append(blockBuilder.build());

            IncentivePolicy policy = network.getIncentivePolicy();

            if (ledgerUpdater.verify(ledger, transaction, policy)) {

                blockBuilder.withTransaction(transaction);

                totalFees += transaction.getFee();

                if ((totalFees >= minimumFees) || blockBuilder.getTransactionCount() == policy.getMaxBlockSize()) {
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

        User user = userStore.load();

        IncentivePolicy policy = network.getIncentivePolicy();

        if (policy.canIntroduceAsset(ledger.size())) {
            blockBuilder.withFirstTransaction(policy.introduceAsset(user.getId(), network.getTime()));
        }

        if (policy.canChargeFees()) {
            blockBuilder.withTransaction(policy.chargeFees(user.getId(), totalFees, network.getTime()));
        }

        blockBuilder.withTimestamp(network.getTime());

        Block block = blockBuilder.withProofOfWork("190").build();

        network.publish(block);

        ledger = ledger.append(block);

        ledgerStore.store(ledger);

        blockBuilder
                .withPreviousHash(null)
                .withoutTransactions(t -> true);

        totalFees = 0;
    }

    private void resetBlockInProgress(Ledger ledger) {

        blockBuilder
                .withoutTransactions(t -> {
                    if (ledger.findWithHash(t.getHash()).isPresent()) {
                        totalFees -= t.getFee();
                        return true;
                    } else {
                        return false;
                    }
                });
    }
}
