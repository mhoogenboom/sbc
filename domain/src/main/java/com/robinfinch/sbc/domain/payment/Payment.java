package com.robinfinch.sbc.domain.payment;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Payment implements Transaction {

    private final String from;
    private final String to;
    private final int value;
    private final int change;
    private final String reference;
    private final int fee;
    private final List<Hash> sources;

    private Payment(String from, String to, int value, int change, String reference,
                    int fee, List<Hash> sources)
            throws ConfigurationException {

        this.from = from;
        this.to = to;
        this.value = value;
        this.change = change;
        this.reference = reference;
        this.fee = fee;
        this.sources = new ArrayList<>(sources);
    }

    @Override
    public void update(Signature signature) throws SignatureException {
        signature.update(getData());
        for (Hash source : sources) {
            signature.update(source.getValue());
        }
    }

    @Override
    public void update(MessageDigest digest) {
        digest.update(getData());
        for (Hash source : sources) {
            digest.update(source.getValue());
        }
    }

    private byte[] getData() {
        return new StringBuilder()
                .append(from)
                .append(':')
                .append(to)
                .append(':')
                .append(value)
                .append(':')
                .append(change)
                .append(':')
                .append(reference)
                .append(':')
                .append(fee)
                .toString()
                .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getValue() {
        return value;
    }

    public int getChange() {
        return change;
    }

    public String getReference() {
        return reference;
    }

    public int getFee() {
        return fee;
    }

    public List<Hash> getSources() {
        return sources;
    }

    @Override
    public boolean hasValueFor(String userId) {
        if (to.equals(userId)) {
            return true;
        }
        if (from.equals(userId) && (change > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasSource(Hash hash) {
        return sources.contains(hash);
    }

    @Override
    public boolean hasCompulsoryValues() {
        return isPresent(from)
                && isPresent(to)
                && (value > 0)
                && (change >= 0)
                && (fee >= 0);
    }

    private boolean isPresent(String s) {
        return (s != null) && !s.trim().isEmpty();
    }

    @Override
    public boolean verify(Ledger ledger, boolean firstInBlock, boolean lastInBlock) {

        if (from.equals(to)) {
            if ((change != 0) || (fee != 0) || !sources.isEmpty()) {
                // payment to self cannot have change, fee or sources
                return false;
            }
            if (firstInBlock) {
                return (value == 0); // todo introduction value
            } else if (lastInBlock) {
                return (value == 0); // todo fees
            } else {
                return false;
            }
        } else {
            int totalValue = 0;

            for (Hash source : sources) {
                Optional<Entry> optionalSourceEntry = ledger.findWithHash(source);

                if (optionalSourceEntry.isPresent()) {
                    Entry sourceEntry = optionalSourceEntry.get();

                    Payment sourcePayment = (Payment) sourceEntry.getTransaction();

                    if (ledger.findSpend(from, sourceEntry.getHash()).isEmpty()) {
                        if (sourcePayment.getTo().equals(from)) {
                            totalValue += sourcePayment.getValue();
                        } else if (sourcePayment.getFrom().equals(from)) {
                            totalValue += sourcePayment.getChange();
                        } else {
                            // user hasn't received source
                            return false;
                        }
                    } else {
                        // user has already spend source
                        return false;
                    }
                } else {
                    // source transaction not in ledger
                    return false;
                }
            }

            if (totalValue != value + change + fee) {
                // sources don't cover this transaction
                return false;
            }
        }
        return true;
    }

    public static class Builder {

        private String to;
        private String from;
        private int value;
        private int change;
        private String reference;
        private int fee;
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

        public Builder withValue(int value) {
            this.value = value;
            return this;
        }

        public Builder withChange(int change) {
            this.change = change;
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

        public Builder withSource(Hash source) {
            sources.add(source);
            return this;
        }

        public Payment build() throws ConfigurationException {
            return new Payment(from, to, value, change, reference, fee, sources);
        }
    }
}

