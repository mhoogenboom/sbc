package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.network.IncentivePolicy;
import com.robinfinch.sbc.core.network.Network;

import java.util.ArrayList;
import java.util.List;

public class LedgerUpdater {

    private final Network network;
    private final boolean verifyTransactions;

    public LedgerUpdater(Network network, boolean verifyTransactions) {
        this.network = network;
        this.verifyTransactions = verifyTransactions;
    }

    public Ledger update(Ledger ledger, Block block) throws ConfigurationException {
        List<Block> tail = new ArrayList<>();
        if (block == null) {
            return ledger;
        } else {
            tail.add(block);
            return update(ledger, tail);
        }
    }

    private Ledger update(Ledger ledger, List<Block> tail) throws ConfigurationException {

        Hash previousHash = tail.get(0).getPreviousHash();

        Ledger commonParent = ledger.findChainEndingAt(previousHash);

        if (commonParent == null) {
            Block previousBlock = network.requestBlock(previousHash);
            if (previousBlock == null) {
                return ledger;
            } else {
                tail.add(0, previousBlock);
                return update(ledger, tail);
            }
        } else {
            int myChainLength = ledger.size();
            int otherChainLength = commonParent.size() + tail.size();

            if (myChainLength < otherChainLength) {
                for (Block block : tail) {
                    if (verify(commonParent, block)) {
                        commonParent = commonParent.append(block);
                    }
                }
                return commonParent;
            } else {
                return ledger;
            }
        }
    }

    private boolean verify(Ledger ledger, Block block) throws ConfigurationException {

        if ((block == null) || block.getTransactions().isEmpty()) {
            // no block
            return false;
        }

        return true; // todo
    }

    public boolean verify(Ledger ledger, Transaction transaction, IncentivePolicy policy) {

        if (transaction == null) {
            // no transaction
            return false;
        }

        return true; // todo
    }
}
