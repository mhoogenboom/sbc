package com.robinfinch.sbc.core;

import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.network.Node;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Client implements Node {

    private enum TransferType {
        NO_SOURCE, SINGLE_SOURCE, MULTIPLE_SOURCES
    }

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

    public synchronized boolean introduce(String id, String reference, int confirmationLevel)
            throws ConfigurationException {

        if (network.getAssetFactory().allowsAssetIntroduction()) {
            User user = userStore.load();

            Asset requiredAsset = network.getAssetFactory().createAssetForId(id);

            return transfer(TransferType.NO_SOURCE, null, user.getId(), requiredAsset, reference, 0, confirmationLevel);
        } else {
            return false;
        }
    }

    public synchronized boolean transfer(String to, String id, String reference, int confirmationLevel)
            throws ConfigurationException {

        User user = userStore.load();

        Asset requiredAsset = network.getAssetFactory().createAssetForId(id);

        return transfer(TransferType.SINGLE_SOURCE, user.getId(), to, requiredAsset, reference, 0, confirmationLevel);
    }

    public synchronized boolean transfer(String to, int value, String reference, int fee, int confirmationLevel)
            throws ConfigurationException {

        User user = userStore.load();

        Asset requiredAsset = network.getAssetFactory().createAssetForValue(value);

        return transfer(TransferType.MULTIPLE_SOURCES, user.getId(), to, requiredAsset, reference, fee, confirmationLevel);
    }

    private boolean transfer(TransferType transferType, String from, String to, Asset requiredAsset, String reference, int fee, int confirmationLevel)
            throws ConfigurationException {

        Ledger ledger = ledgerStore.load().withoutNewestBlocks(confirmationLevel);

        Transaction.Builder builder = new Transaction.Builder()
                .withFrom(from)
                .withTo(to);

        if (transferType == TransferType.NO_SOURCE) {
            // asset introduction
        } else {
            List<Transaction> transactions = ledger.findUnspent(from);

            Asset asset = network.getAssetFactory().createAsset();

            if (transferType == TransferType.SINGLE_SOURCE) {
                for (Transaction transaction : transactions) {

                    if (transaction.getAsset().covers(requiredAsset, fee)) {
                        builder.withSource(transaction.getHash());

                        asset = transaction.getAsset();
                        break;
                    }
                }
            } else {
                for (Transaction transaction : transactions) {
                    builder.withSource(transaction.getHash());

                    asset = asset.plus(from, transaction);

                    if (asset.covers(requiredAsset, fee)) {
                        break;
                    }
                }
            }

            asset = asset.computeChange(requiredAsset, fee);

            if (!asset.isValid()) {
                return false;
            }

            builder.withAsset(asset);
        }

        Transaction transaction = builder
                .withReference(reference)
                .withFee(fee)
                .withTimestamp(network.getTime())
                .build();

        User user = userStore.load();

        Transaction signedTransaction = user.sign(transaction);

        network.publish(signedTransaction);

        onTransactionPublished(signedTransaction);

        return true;
    }

    public synchronized boolean hasReceived(String from, String id, String reference, int confirmationLevel)
            throws ConfigurationException {

        Asset asset = network.getAssetFactory().createAssetForId(id);

        return hasReceived(from, asset, reference, confirmationLevel);
    }

    public synchronized boolean hasReceived(String from, int value, String reference, int confirmationLevel)
            throws ConfigurationException {

        Asset asset = network.getAssetFactory().createAssetForValue(value);

        return hasReceived(from, asset, reference, confirmationLevel);
    }

    private synchronized boolean hasReceived(String from, Asset asset, String reference, int confirmationLevel)
            throws ConfigurationException {

        Ledger ledger = ledgerStore.load().withoutNewestBlocks(confirmationLevel);

        return ledger.findWithReference(from, asset, reference).isPresent();
    }

    public List<Asset> getWallet(int confirmationLevel) {

        User user = userStore.load();

        Ledger ledger = ledgerStore.load().withoutNewestBlocks(confirmationLevel);

        return ledger.findUnspent(user.getId())
                .stream()
                .map(Transaction::getAsset)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Block provideBlock(Hash hash) {

        Ledger ledger = ledgerStore.load().findChainEndingAt(hash);

        return (ledger == null) ? null : ledger.getEndOfChain();
    }
}
