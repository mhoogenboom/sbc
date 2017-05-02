package com.robinfinch.sbc.p2p.peer;

import java.util.List;

public class BlockTo {

    private String previousHash;
    private String userId;
    private long timestamp;
    private String proofOfWork;
    private List<EntryTo> transactions;

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProofOfWork() {
        return proofOfWork;
    }

    public void setProofOfWork(String proofOfWork) {
        this.proofOfWork = proofOfWork;
    }

    public List<EntryTo> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<EntryTo> transactions) {
        this.transactions = transactions;
    }
}
