package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.robinfinch.sbc.core.Hash.HASH_ALGORITHM;

public class Block {

    private final Hash previousHash;
    private final String userId;
    private final long timestamp;
    private final String proofOfWork;
    private final List<Entry> entries;
    private final Hash hash;

    private Block(Hash previousHash, String userId, long timestamp, String proofOfWork, List<Entry> entries)
            throws ConfigurationException {

        this.previousHash = previousHash;
        this.userId = userId;
        this.timestamp = timestamp;
        this.proofOfWork = proofOfWork;
        this.entries = new ArrayList<>(entries);

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            if (previousHash != null) {
                digest.update(previousHash.getValue());
            }
            digest.update(getData());
            for (Entry transaction : getEntries()) {
                digest.update(transaction.getHash().getValue());
            }
            this.hash = new Hash(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfigurationException("Required hash algorithm " + HASH_ALGORITHM + " not supported", e);
        }
    }

    public Hash getPreviousHash() {
        return previousHash;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getProofOfWork() {
        return proofOfWork;
    }

    private byte[] getData() {
        return new StringBuilder()
                .append(userId)
                .append(':')
                .append(timestamp)
                .append(':')
                .append(proofOfWork)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public Hash getHash() {
        return hash;
    }

    public boolean hasCompulsoryValues() {
        return isPresent(userId)
                && (timestamp > 0L)
                && isPresent(proofOfWork)
                && !entries.isEmpty();
    }

    private boolean isPresent(String s) {
        return (s != null) && !s.trim().isEmpty();
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Block) {
            Block that = (Block) o;
            return this.hash.equals(that.hash);
        } else {
            return false;
        }
    }

    public static class Builder {

        private Hash previousHash;
        private String userId;
        private long timestamp;
        private String proofOfWork;
        private List<Entry> entries;

        public Builder() {
            entries = new ArrayList<>();
        }

        public Builder withPreviousHash(Hash previousHash) {
            this.previousHash = previousHash;
            return this;
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withProofOfWork(String proofOfWork) {
            this.proofOfWork = proofOfWork;
            return this;
        }

        public Builder withFirstTransaction(Entry transaction) {
            entries.add(0, transaction);
            return this;
        }

        public Builder withTransaction(Entry entry) {
            entries.add(entry);
            return this;
        }

        public Builder withoutTransactions(Predicate<Entry> remove) {
            entries = entries.stream().filter(remove.negate()).collect(Collectors.toList());
            return this;
        }

        public Block build() throws ConfigurationException {
            return new Block(previousHash, userId, timestamp, proofOfWork, entries);
        }
    }
}
