package com.robinfinch.sbc.domain.vote;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;

public class Vote implements Transaction {

    private final String voterId;
    private final String vote;

    private Vote(String voterId, String vote) {
        this.voterId = voterId;
        this.vote = vote;
    }

    @Override
    public void update(Signature signature) throws SignatureException {
        signature.update(getData());
    }

    @Override
    public void update(MessageDigest digest) {
        digest.update(getData());
    }

    private byte[] getData() {
        return new StringBuilder()
                .append(voterId)
                .append(':')
                .append(vote)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFrom() {
        return voterId;
    }

    public String getVote() {
        return vote;
    }

    @Override
    public boolean hasValueFor(String userId) {
        return false;
    }

    @Override
    public boolean hasSource(Hash hash) {
        return false;
    }

    @Override
    public boolean hasCompulsoryValues() {
        return isPresent(voterId)
                && isPresent(vote);
    }

    private boolean isPresent(String s) {
        return (s != null) && !s.trim().isEmpty();
    }

    @Override
    public boolean verify(Ledger ledger, boolean firstInBlock, boolean lastInBlock) {
        return ledger.find(t -> t.getFrom().equals(voterId)).isEmpty();
    }

    public static class Builder {

        private String voterId;
        private String vote;

        public Builder withVoterId(String voterId) {
            this.voterId = voterId;
            return this;
        }

        public Builder withVote(String vote) {
            this.vote = vote;
            return this;
        }

        public Vote build() throws ConfigurationException {
            return new Vote(voterId, vote);
        }
    }
}

