package com.robinfinch.sbc.domain.transfer;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Optional;

public class Transfer implements Transaction {

    private final String from;
    private final String to;
    private final String asset;
    private final String reference;
    private final Hash source;

    private Transfer(String from, String to, String asset, String reference,
                     Hash source)
            throws ConfigurationException {

        this.from = from;
        this.to = to;
        this.asset = asset;
        this.reference = reference;
        this.source = source;
    }

    @Override
    public void update(Signature signature) throws SignatureException {
        signature.update(getData());
        signature.update(source.getValue());
    }

    @Override
    public void update(MessageDigest digest) {
        digest.update(getData());
        digest.update(source.getValue());
    }

    private byte[] getData() {
        return new StringBuilder()
                .append(from)
                .append(':')
                .append(to)
                .append(':')
                .append(asset)
                .append(':')
                .append(reference)
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

    public String getAsset() {
        return asset;
    }

    public String getReference() {
        return reference;
    }

    public Hash getSource() {
        return source;
    }

    @Override
    public boolean hasValueFor(String userId) {
        return to.equals(userId);
    }

    @Override
    public boolean hasSource(Hash hash) {
        return source.equals(hash);
    }

    @Override
    public boolean hasCompulsoryValues() {
        return isPresent(from)
                && isPresent(to)
                && isPresent(asset);
    }

    private boolean isPresent(String s) {
        return (s != null) && !s.trim().isEmpty();
    }

    @Override
    public boolean verify(Ledger ledger, boolean firstInBlock, boolean lastInBlock) {

        if (from.equals(to)) {
            return (source == null)
                    && ledger.find(t -> ((Transfer) t).getAsset().equals(asset)).isEmpty();
        } else {
            Optional<Entry> optionalSourceEntry = ledger.findWithHash(source);

            if (optionalSourceEntry.isPresent()) {
                Entry sourceEntry = optionalSourceEntry.get();

                Transfer sourceTransfer = (Transfer) sourceEntry.getTransaction();

                if (sourceTransfer.getTo().equals(from)
                        && sourceTransfer.getAsset().equals(asset)
                        && ledger.findSpend(from, sourceEntry.getHash()).isEmpty()) {
                } else {
                    // user hasn't received asset or has already spend asset
                    return false;
                }
            } else {
                // source transaction not in ledger
                return false;
            }
        }

        return true;
    }

    public static class Builder {

        private String to;
        private String from;
        private String asset;
        private String reference;
        private Hash source;

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder withAsset(String asset) {
            this.asset = asset;
            return this;
        }

        public Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder withSource(Hash source) {
            this.source = source;
            return this;
        }

        public Transfer build() throws ConfigurationException {
            return new Transfer(from, to, asset, reference, source);
        }
    }
}

