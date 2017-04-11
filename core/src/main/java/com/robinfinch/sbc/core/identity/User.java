package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Transaction;

import java.security.*;

public class User {

    private final String id;

    private final KeyPair keyPair;

    public User(String id, KeyPair keyPair) {
        this.id = id;
        this.keyPair = keyPair;
    }

    public String getId() {
        return id;
    }

    public Identity getIdentity() {
        return new Identity(id, keyPair.getPublic());
    }

    public Transaction sign(Transaction transaction) throws ConfigurationException {

        byte[] signed;
        try {
            Signature signature = Signature.getInstance(Transaction.SIGNING_ALGORITHM);
            signature.initSign(keyPair.getPrivate());
            signature.update(transaction.getHeader());
            for (Hash source : transaction.getSources()) {
                signature.update(source.value);
            }
            signed = signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new ConfigurationException("Signing algorithm " + Transaction.SIGNING_ALGORITHM + " not supported", e);
        }

        return new Transaction(transaction, signed);
    }
}
