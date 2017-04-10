package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class BlockTests extends Tests {

    @Test
    public void firstBlock() throws Exception {

        Transaction t2 = createTransaction2();
        Transaction t4 = createTransaction4();

        Block block = createBlock2(t2, t4);

        assertNull(block.getPreviousHash());
        assertEquals("chris", block.getUserId());
        assertEquals(6L, block.getTimestamp());
        assertEquals("68", block.getProofOfWork());
        assertEquals(asList(t2, t4), block.getTransactions());

        {
            byte[] expected = {99, 104, 114, 105, 115, 58, 54, 58, 54, 56};
            assertArrayEquals(expected, block.getHeader());
        }

        assertEquals(B2_HASH, block.getHash());
    }

    @Test
    public void nextBlock() throws Exception {

        Transaction t5 = createTransaction5();
        Transaction t6 = createTransaction6();
        Transaction t7 = createTransaction7();

        Block block = createBlock3(t5, t6, t7);

        assertEquals(B2_HASH, block.getPreviousHash());
        assertEquals("chris", block.getUserId());
        assertEquals(10L, block.getTimestamp());
        assertEquals("40", block.getProofOfWork());
        assertEquals(asList(t5, t6, t7), block.getTransactions());

        {
            byte[] expected = {99, 104, 114, 105, 115, 58, 49, 48, 58, 52, 48};
            assertArrayEquals(expected, block.getHeader());
        }

        assertEquals(B3_HASH, block.getHash());
    }

    @Test
    public void removeTransactionsFromBlockBuilder() throws Exception {

        Transaction t13 = createTransaction13();
        Transaction t15 = createTransaction15();
        Transaction t17 = createTransaction15();

        Block block = new Block.Builder()
                .withTransaction(t13)
                .withTransaction(t15)
                .withoutTransactions(t -> (t == t13))
                .withTransaction(t17)
                .build();

        assertEquals(asList(t15, t17), block.getTransactions());
    }
}
