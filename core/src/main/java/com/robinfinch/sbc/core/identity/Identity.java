package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.ledger.Entry;

import java.security.*;
import java.util.Objects;

public class Identity {

    private final String userId;

    private final PublicKey publicKey;

    public Identity(String userId, PublicKey publicKey) {
        this.userId = userId;
        this.publicKey = publicKey;
    }

    public String getUserId() {
        return userId;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean hasSigned(Entry entry) {

        boolean result;
        try {
            if (entry.getSignature() == null) {
                result = false;
            } else {
                Signature signature = Signature.getInstance(Entry.SIGNING_ALGORITHM);
                signature.initVerify(publicKey);
                entry.update(signature);
                result = signature.verify(entry.getSignature());
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            result = false;
        }
        return result;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(userId)
                + 3 * Objects.hashCode(publicKey);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Identity) {
            Identity that = (Identity) o;
            return Objects.equals(this.userId, that.userId)
                    && Objects.equals(this.publicKey, that.publicKey);
        } else {
            return false;
        }
    }
}
