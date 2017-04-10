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
    private final List<Transaction> transactions;
    private final Hash hash;

    private Block(Hash previousHash, String userId, long timestamp, String proofOfWork, List<Transaction> transactions)
            throws ConfigurationException {

        this.previousHash = previousHash;
        this.userId = userId;
        this.timestamp = timestamp;
        this.proofOfWork = proofOfWork;
        this.transactions = new ArrayList<>(transactions);

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            if (previousHash != null) {
                digest.update(previousHash.value);
            }
            digest.update(getHeader());
            for (Transaction transaction : getTransactions()) {
                digest.update(transaction.getHash().value);
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

    public byte[] getHeader() {
        return new StringBuilder()
                .append(userId)
                .append(':')
                .append(timestamp)
                .append(':')
                .append(proofOfWork)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    public List<Transaction> getTransactions() {
        return transactions;
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
        private List<Transaction> transactions;

        public Builder() {
            transactions = new ArrayList<>();
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

        public Builder withFirstTransaction(Transaction transaction) {
            transactions.add(0, transaction);
            return this;
        }

        public Builder withTransaction(Transaction transaction) {
            transactions.add(transaction);
            return this;
        }

        public Builder withoutTransactions(Predicate<Transaction> remove) {
            transactions = transactions.stream().filter(remove.negate()).collect(Collectors.toList());
            return this;
        }

        public int getTransactionCount() {
            return transactions.size();
        }

        public Block build() throws ConfigurationException {
            return new Block(previousHash, userId, timestamp, proofOfWork, transactions);
        }
    }
}
