package com.robinfinch.sbc.core.worker;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Block;

public class Worker {

    public Block seal(Block.Builder blockBuilder)
            throws ConfigurationException {

        Block block;

        String nonce = firstNonce();

        while (!check(block = blockBuilder.withProofOfWork(nonce).build())) {
            nonce = next(nonce);
        }

        return block;
    }

    private String firstNonce() {
        return Integer.toString(0);
    }

    private String next(String nonce) {
        return Integer.toString(Integer.parseInt(nonce) + 1);
    }

    public boolean check(Block block) {
        return block.getHash().startsWithZeros(1);
    }
}
