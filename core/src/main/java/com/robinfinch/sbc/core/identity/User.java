package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Entry;

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

    public Entry sign(Entry entry) throws ConfigurationException {

        byte[] signed;
        try {
            Signature signature = Signature.getInstance(Entry.SIGNING_ALGORITHM);
            signature.initSign(keyPair.getPrivate());
            entry.update(signature);
            signed = signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new ConfigurationException("Signing algorithm " + Entry.SIGNING_ALGORITHM + " not supported", e);
        }

        return new Entry(entry, signed);
    }
}
