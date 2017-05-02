package com.robinfinch.sbc.p2p.peer;

import com.robinfinch.sbc.core.ledger.Transaction;

import java.util.Objects;

public class EntryTo {

    private Transaction transaction;
    private long timestamp;
    private String signature;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transaction)
                + 3 * Objects.hashCode(timestamp)
                + 5 * Objects.hashCode(signature);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EntryTo) {
            EntryTo that = (EntryTo) o;
            return Objects.equals(this.transaction, that.transaction)
                    && (this.timestamp == that.timestamp)
                    && Objects.equals(this.signature, that.signature);
        } else {
            return false;
        }
    }
}
