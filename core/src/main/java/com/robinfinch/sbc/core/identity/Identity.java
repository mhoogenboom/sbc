package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.security.*;

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

    public boolean hasSigned(Transaction transaction) {

        boolean result;
        try {
            if (transaction.getSignature() == null) {
                result = false;
            } else {
                Signature signature = Signature.getInstance(Transaction.SIGNING_ALGORITHM);
                signature.initVerify(publicKey);
                signature.update(transaction.getHeader());
                for (Hash source : transaction.getSources()) {
                    signature.update(source.value);
                }
                result = signature.verify(transaction.getSignature());
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            result = false;
        }
        return result;
    }
}
