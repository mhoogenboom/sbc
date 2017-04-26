package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.network.IncentivePolicy;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.worker.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LedgerUpdater {

    private final Network network;
    private final Worker worker;
    private final boolean verifyTransactions;

    public LedgerUpdater(Network network, boolean verifyTransactions) {
        this.network = network;
        this.worker = new Worker();
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

        if (isEmpty(block.getUserId())
                || (block.getTimestamp() <= 0L)
                || isEmpty(block.getProofOfWork())) {
            // compulsory values empty
            return false;
        }

        if (verifyTransactions) {
            IncentivePolicy policy = network.getIncentivePolicy();

            int firstIndex = 0;
            int lastIndex = block.getTransactions().size() - 1;

            List<Transaction> transactions;

            if (policy.canIntroduceAsset(ledger.size())) {
                Transaction introductionTransaction = block.getTransactions().get(firstIndex);
                if (!policy.verifyIntroductionTransaction(block.getUserId(), introductionTransaction)) {
                    return false;
                }

                transactions = block.getTransactions().subList(firstIndex + 1, lastIndex);
            } else {
                transactions = block.getTransactions().subList(firstIndex, lastIndex);
            }

            Block.Builder blockBuilder = new Block.Builder();
            for (Transaction transaction : transactions) {
                if (verify(ledger.append(blockBuilder.build()), transaction, policy)) {
                    blockBuilder.withTransaction(transaction);
                } else {
                    // invalid transaction
                    return false;
                }
            }

            if (policy.canChargeFees()) {
                int fees = block.getTransactions().stream().mapToInt(Transaction::getFee).sum();

                Transaction feeTransaction = block.getTransactions().get(lastIndex);
                if (!policy.verifyFeeTransaction(block.getUserId(), fees, feeTransaction)) {
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

    public boolean verify(Ledger ledger, Transaction transaction, IncentivePolicy policy) {

        if (transaction == null) {
            // no transaction
            return false;
        }

        if (isEmpty(transaction.getFrom())
                || isEmpty(transaction.getTo())
                || !transaction.getAsset().isValid()
                || !policy.verifyFee(transaction.getFee())
                || (transaction.getTimestamp() <= 0L)) {
            // compulsory values empty
            return false;
        }

        if (transaction.getFrom().equals(transaction.getTo())) {
            // transaction to self
            return false;
        }

        Identity from = network.requestIdentity(transaction.getFrom());

        if ((from == null) || !from.hasSigned(transaction)) {
            // sender hasn't signed transaction
            return false;
        }

        if (network.getAssetFactory().allowsAssetIntroduction() && transaction.getSources().isEmpty()) {
            // asset introduction
        } else {
            Asset sourceAsset = network.getAssetFactory().createAsset();

            for (Hash source : transaction.getSources()) {
                Optional<Transaction> optionalSourceTransaction = ledger.findWithHash(source);

                if (optionalSourceTransaction.isPresent()) {
                    Transaction sourceTransaction = optionalSourceTransaction.get();

                    if (sourceTransaction.hasValueFor(from.getUserId())) {
                        sourceAsset = sourceAsset.plus(from.getUserId(), sourceTransaction);
                    } else {
                        // sender doesn't own source transaction
                        return false;
                    }

                    if (!ledger.findSpend(from.getUserId(), transaction.getHash()).isEmpty()) {
                        // sender has already spent source transaction
                        return false;
                    }
                } else {
                    // source transaction not in ledger
                    return false;
                }
            }

            if (!sourceAsset.covers(transaction.getAsset(), transaction.getFee())) {
                // source transactions don't cover the transaction
                return false;
            }
        }

        return true;
    }

    private boolean isEmpty(String s) {
        return (s == null) || s.trim().isEmpty();
    }
}
