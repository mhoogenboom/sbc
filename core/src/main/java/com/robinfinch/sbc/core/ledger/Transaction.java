package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.robinfinch.sbc.core.Hash.HASH_ALGORITHM;

public class Transaction {

    public static final String SIGNING_ALGORITHM = "SHA256withRSA";

    private final String from;
    private final String to;
    private final Asset asset;
    private final String reference;
    private final int fee;
    private final long timestamp;
    private final List<Hash> sources;
    private final byte[] signature;
    private final Hash hash;

    private Transaction(String from, String to, Asset asset, String reference, int fee,
                        long timestamp, List<Hash> sources)
            throws ConfigurationException {

        this.from = from;
        this.to = to;
        this.asset = asset;
        this.reference = reference;
        this.fee = fee;
        this.timestamp = timestamp;
        this.sources = new ArrayList<>(sources);
        this.signature = null;

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(getHeader());
            for (Hash source : getSources()) {
                digest.update(source.value);
            }
            this.hash = new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Required hash algorithm " + HASH_ALGORITHM + " not supported", e);
        }
    }

    public Transaction(Transaction transaction, byte[] signature)
            throws ConfigurationException {

        this.from = transaction.getFrom();
        this.to = transaction.getTo();
        this.asset = transaction.getAsset();
        this.fee = transaction.getFee();
        this.reference = transaction.getReference();
        this.timestamp = transaction.getTimestamp();
        this.sources = transaction.getSources();
        this.signature = signature;

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(getHeader());
            for (Hash source : getSources()) {
                digest.update(source.value);
            }
            digest.update(getSignature());
            this.hash = new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Required hash algorithm " + HASH_ALGORITHM + " not supported", e);
        }
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Asset getAsset() {
        return asset;
    }

    public boolean hasValueFor(String userId) {
        if (userId.equals(to)) {
            return true;
        }
        if (userId.equals(from) && asset.hasChange()) {
            return true;
        }
        return false;
    }

    public String getReference() {
        return reference;
    }

    public int getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getHeader() {
        return new StringBuilder()
                .append(from)
                .append(':')
                .append(to)
                .append(':')
                .append(asset)
                .append(':')
                .append(fee)  // todo swap fee and reference
                .append(':')
                .append(reference)
                .append(':')
                .append(timestamp)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    public List<Hash> getSources() {
        return sources;
    }

    public byte[] getSignature() {
        return signature;
    }

    public Hash getHash() {
        return hash;
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Transaction) {
            Transaction that = (Transaction) o;
            return this.hash.equals(that.hash);
        } else {
            return false;
        }
    }

    public static class Builder {

        private String to;
        private String from;
        private Asset asset;
        private String reference;
        private int fee;
        private long timestamp;
        private List<Hash> sources;

        public Builder() {
            sources = new ArrayList<>();
        }

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder withAsset(Asset asset) {
            this.asset = asset;
            return this;
        }

        public Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder withFee(int fee) {
            this.fee = fee;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withSource(Hash source) {
            sources.add(source);
            return this;
        }

        public Transaction build() throws ConfigurationException {
            return new Transaction(from, to, asset, reference, fee, timestamp, sources);
        }
    }
}

