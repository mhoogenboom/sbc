package com.robinfinch.sbc.core.worker;

import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkerTests extends TestData {

    private Worker worker;

    @Before
    public void setUpWorker() {
        worker = new Worker();
    }

    @Test
    public void seal() throws Exception {

//        {
//            Block.Builder blockBuilder = new Block.Builder()
//                    .withUserId("alice")
//                    .withTimestamp(1L)
//                    .withTransaction(entry1)
//                    .withTransaction(entry2)
//                    .withTransaction(entry3);
//
//            System.out.println("block 1 pow = " + worker.seal(blockBuilder).getProofOfWork());
//        }

//        {
//            Block.Builder blockBuilder = new Block.Builder()
//                    .withPreviousHash(BLOCK1_HASH)
//                    .withUserId("bob")
//                    .withTimestamp(2L)
//                    .withProofOfWork("1234")
//                    .withTransaction(entry4);
//
//            System.out.println("block 2 pow = " + worker.seal(blockBuilder).getProofOfWork());
//        }

        Block.Builder blockBuilder = new Block.Builder()
                .withPreviousHash(BLOCK2_HASH)
                .withUserId("chris")
                .withTimestamp(3L)
                .withTransaction(entry5)
                .withTransaction(entry6);

        Block block = worker.seal(blockBuilder);

        assertEquals(BLOCK3_HASH, block.getHash());

        assertEquals("306", block.getProofOfWork());
    }

    @Test
    public void check() throws Exception {

        Block.Builder blockBuilder = new Block.Builder()
                .withPreviousHash(BLOCK2_HASH)
                .withUserId("chris")
                .withTimestamp(3L)
                .withTransaction(entry5)
                .withTransaction(entry6);

        assertTrue(worker.check(blockBuilder.withProofOfWork("306").build()));
        assertFalse(worker.check(blockBuilder.withProofOfWork("305").build()));
    }
}
