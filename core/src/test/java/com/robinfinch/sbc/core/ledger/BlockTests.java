package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class BlockTests extends TestData {

    private static Hash HASH1 = new Hash(new byte[] {52, -4, 51, -123, 2, -117, -100, -66, -81, 71, -21, -77, -65,
            124, 111, -100, 85, 34, 75, -96, -121, -95, 18, -72, -53, 45, -114, 107, 78, 30, -89, -19});

    private static Hash HASH2 = new Hash(new byte[] {-60, 44, 33, -37, 32, 59, -41, 59, 66, -84, 29, 61, 27, -8, -8,
            -21, 100, -10, 110, 50, 60, -85, 65, 82, 83, 66, 116, 17, -82, 85, -65, -13});

    @Test
    public void build() throws Exception {

//        System.out.println("block 1 hash = " + block1.getHash());
//        System.out.println("block 2 hash = " + block2.getHash());
//        System.out.println("block 3 hash = " + block3.getHash());

        Block.Builder builder = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(1L)
                .withProofOfWork("1234")
                .withTransaction(entry1)
                .withTransaction(entry2);

        {
            Block block = builder.build();

            assertNull(block.getPreviousHash());
            assertEquals("alice", block.getUserId());
            assertEquals(1L, block.getTimestamp());
            assertEquals("1234", block.getProofOfWork());
            assertEquals(asList(entry1, entry2), block.getEntries());
            assertEquals(HASH1, block.getHash());
        }

        builder
                .withPreviousHash(HASH1)
                .withFirstTransaction(entry3)
                .withoutTransactions(e -> e.getTimestamp() == 1L);

        {
            Block block = builder.build();

            assertEquals(HASH1, block.getPreviousHash());
            assertEquals("alice", block.getUserId());
            assertEquals(1L, block.getTimestamp());
            assertEquals("1234", block.getProofOfWork());
            assertEquals(asList(entry3, entry2), block.getEntries());
            assertEquals(HASH2, block.getHash());
        }
    }

    @Test
    public void checkCompulsoryValues() throws Exception {

        assertTrue(block1.hasCompulsoryValues());
    }

    @Test
    public void missingUser() throws Exception {

        Block block = new Block.Builder()
                .withTimestamp(1L)
                .withProofOfWork("1234")
                .withTransaction(entry1)
                .withTransaction(entry2)
                .withTransaction(entry3)
                .build();

        assertFalse(block.hasCompulsoryValues());
    }

    @Test
    public void missingTimestamp() throws Exception {

        Block block = new Block.Builder()
                .withUserId("alice")
                .withProofOfWork("1234")
                .withTransaction(entry1)
                .withTransaction(entry2)
                .withTransaction(entry3)
                .build();

        assertFalse(block.hasCompulsoryValues());
    }

    @Test
    public void missingProofOfWork() throws Exception {

        Block block = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(1L)
                .withTransaction(entry1)
                .withTransaction(entry2)
                .withTransaction(entry3)
                .build();

        assertFalse(block.hasCompulsoryValues());
    }

    @Test
    public void noEntries() throws Exception {

        Block block = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(1L)
                .withProofOfWork("1234")
                .build();

        assertFalse(block.hasCompulsoryValues());
    }
}
