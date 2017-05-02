package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;

import java.util.stream.Stream;

public class EmptyLedger extends Ledger {

    public EmptyLedger() {
        super(null, null);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Ledger findChainEndingAt(Hash hash) {
        if (hash == null) {
            return this;
        } else {
            return null;
        }
    }

    @Override
    public Ledger withoutNewestBlocks(int blocks) {
        return this;
    }

    @Override
    Stream.Builder<Block> stream() {
        return Stream.builder();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}