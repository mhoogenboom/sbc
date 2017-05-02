package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
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

    public Optional<Entry> findWithHash(Hash hash) {

        return stream()
                .build()
                .flatMap(block -> block.getEntries().stream())
                .filter(entry -> entry.getHash().equals(hash))
                .findAny();
    }

    public List<Entry> findSpend(String userId, Hash hash) {

        return find(t -> t.getFrom().equals(userId) && t.hasSource(hash));
    }

    public List<Entry> findUnspent(String userId, int confirmationLevel) {

        // find entry received in confirmed blocks, but not spend in any block
        return withoutNewestBlocks(confirmationLevel)
                .stream()
                .build()
                .flatMap(block -> block.getEntries().stream())
                .filter(entry -> entry.getTransaction().hasValueFor(userId)
                        && findSpend(userId, entry.getHash()).isEmpty())
                .sorted((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<Entry> find(Predicate<Transaction> filter) {

        return stream()
                .build()
                .flatMap(block -> block.getEntries().stream())
                .filter(entry -> filter.test(entry.getTransaction()))
                .collect(Collectors.toList());
    }

    Stream.Builder<Block> stream() {
        return chain.stream().add(endOfChain);
    }

    @Override
    public int hashCode() {
        return 2 * chain.hashCode() + endOfChain.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ledger) {
            Ledger that = (Ledger) o;
            return Objects.equals(this.chain, that.chain)
                    && Objects.equals(this.endOfChain, that.endOfChain);
        } else {
            return false;
        }
    }
}
