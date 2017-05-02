package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;

import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;

public interface Transaction {

    void update(Signature signature) throws SignatureException;

    void update(MessageDigest digest);

    String getFrom();

    boolean hasValueFor(String userId);

    boolean hasSource(Hash hash);

    boolean hasCompulsoryValues();

    boolean verify(Ledger ledger, boolean firstInBlock, boolean lastInBlock);
}
