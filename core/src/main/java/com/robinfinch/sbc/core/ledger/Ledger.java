package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ledger {

    private final Ledger chain;
    private final Block endOfChain;

    Ledger(Ledger chain, Block endOfChain) {
        this.chain = chain;
        this.endOfChain = endOfChain;
    }

    public int size() {
        return chain.size() + 1;
    }

    public Block getEndOfChain() {
        return endOfChain;
    }

    public Ledger findChainEndingAt(Hash hash) {
        if (endOfChain.getHash().equals(hash)) {
            return this;
        } else {
            return chain.findChainEndingAt(hash);
        }
    }

    public Ledger withoutNewestBlocks(int blocks) {
        if (blocks == 0) {
            return this;
        } else {
            return chain.withoutNewestBlocks(blocks - 1);
        }
    }

    public Ledger append(Block block) {
        return new Ledger(this, block);
    }

    public Optional<Transaction> findWithHash(Hash hash) {

        return stream()
                .build()
                .flatMap(b -> b.getTransactions().stream())
                .filter(t -> t.getHash().equals(hash))
                .findAny();
    }

    public Optional<Transaction> findWithReference(String from, Asset asset, String reference) {

        return stream()
                .build()
                .flatMap(b -> b.getTransactions().stream())
                .filter(t -> from.equals(t.getFrom())
                        && t.getAsset().covers(asset)
                        && reference.equals(t.getReference()))
                .findAny();
    }

    public List<Transaction> findSpend(String userId, Hash hash) {

        return stream()
                .build()
                .flatMap(b -> b.getTransactions().stream())
                .filter(t -> userId.equals(t.getFrom())
                        && t.getSources().contains(hash))
                .collect(Collectors.toList());
    }

    public List<Transaction> findUnspent(String userId) {

        return stream()
                .build()
                .flatMap(b -> b.getTransactions().stream())
                .filter(t -> t.hasValueFor(userId)
                        && findSpend(userId, t.getHash()).isEmpty())
                .sorted((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()))
                .collect(Collectors.toList());
    }

    Stream.Builder<Block> stream() {
        return chain.stream().add(endOfChain);
    }
}
