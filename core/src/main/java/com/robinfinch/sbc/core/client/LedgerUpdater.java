package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.worker.Worker;

import java.util.ArrayList;
import java.util.List;

public class LedgerUpdater {

    private final Network network;
    private final Worker worker;
    private final boolean verifyEntries;

    public LedgerUpdater(Network network, boolean verifyEntries) {
        this.network = network;
        this.worker = new Worker();
        this.verifyEntries = verifyEntries;
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

        if (!block.hasCompulsoryValues()) {
            // block not valid
            return false;
        }

        if (verifyEntries) {
            List<Entry> entries = block.getEntries();
            int first = 0;
            int last = entries.size() - 1;

            Block.Builder blockBuilder = new Block.Builder();
            for (int i = first; i <= last; i++) {
                if (verify(ledger.append(blockBuilder.build()),
                        entries.get(i),
                        (i == first),
                        (i == last))) {
                    blockBuilder.withTransaction(entries.get(i));
                } else {
                    // invalid transaction
                    return false;
                }
            }
        }

        if (!worker.check(block)) {
            // proof of work not valid
            return false;
        }

        return true;
    }

    public boolean verify(Ledger ledger, Entry entry, boolean firstInBlock, boolean lastInBlock) {

        if ((entry == null) || !entry.hasCompulsoryValues()) {
            // invalid entry
            return false;
        }

        Identity from = network.requestIdentity(entry.getTransaction().getFrom());

        if ((from == null) || !from.hasSigned(entry)) {
            // sender hasn't signed entry
            return false;
        }

        if (!entry.verify(ledger, firstInBlock, lastInBlock)) {
            // sender not allowed to send this transaction
            return false;
        }

        return true;
    }
}
