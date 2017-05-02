package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import static com.robinfinch.sbc.core.Hash.HASH_ALGORITHM;

public class Entry {

    public static final String SIGNING_ALGORITHM = "SHA256withRSA";

    private final Transaction transaction;
    private final long timestamp;
    private final byte[] signature;
    private final Hash hash;

    private Entry(Transaction transaction, long timestamp)
            throws ConfigurationException {

        this.transaction = transaction;
        this.timestamp = timestamp;
        this.signature = null;

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            if (transaction != null) {
                transaction.update(digest);
            }
            updateWithData(digest);
            this.hash = new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Required hash algorithm " + HASH_ALGORITHM + " not supported", e);
        }
    }

    public Entry(Entry entry, byte[] signature)
            throws ConfigurationException {

        this.transaction = entry.transaction;
        this.timestamp = entry.timestamp;
        this.signature = signature;

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            if (transaction != null) {
                transaction.update(digest);
            }
            updateWithData(digest);
            updateWithSignature(digest);
            this.hash = new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Required hash algorithm " + HASH_ALGORITHM + " not supported", e);
        }
    }

    public void update(Signature signature) throws SignatureException {
        transaction.update(signature);
        signature.update(getData());
    }

    private void updateWithData(MessageDigest digest) {
        digest.update(getData());
    }

    private byte[] getData() {
        return new StringBuilder()
                .append(timestamp)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    private void updateWithSignature(MessageDigest digest) {
        digest.update(signature);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getSignature() {
        return signature;
    }

    public Hash getHash() {
        return hash;
    }

    public boolean hasCompulsoryValues() {
        return (transaction != null) && transaction.hasCompulsoryValues()
                && (timestamp > 0L);
    }

    public boolean verify(Ledger ledger, boolean firstInBlock, boolean lastInBlock) {
        return transaction.verify(ledger, firstInBlock, lastInBlock);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Entry) {
            Entry that = (Entry) o;
            return this.hash.equals(that.hash);
        } else {
            return false;
        }
    }

    public static class Builder {

        private Transaction transaction;
        private long timestamp;

        public Builder withTransaction(Transaction transaction) {
            this.transaction = transaction;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Entry build() throws ConfigurationException {
            return new Entry(transaction, timestamp);
        }
    }
}

