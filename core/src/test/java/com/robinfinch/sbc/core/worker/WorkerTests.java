package com.robinfinch.sbc.core.worker;

import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerTests extends Tests {

    private Worker worker;

    @Before
    public void setUpWorker() {
        worker = new Worker();
    }

    @Test
    public void seal() throws Exception {

        Block.Builder blockBuilder = new Block.Builder()
                .withPreviousHash(B4_HASH);

        Block block = worker.seal(blockBuilder);

        assertEquals("13", block.getProofOfWork());
    }

    @Test
    public void check() throws Exception {

        Block.Builder blockBuilder = new Block.Builder()
                .withPreviousHash(B4_HASH);


        assertTrue(worker.check(blockBuilder.withProofOfWork("13").build()));
        assertFalse(worker.check(blockBuilder.withProofOfWork("12").build()));
    }
}
