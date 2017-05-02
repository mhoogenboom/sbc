package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.EmptyLedger;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LedgerUpdaterTests extends TestData {

    private LedgerUpdater updater;

    @Mock
    Network network;

    @Before
    public void setUpUpdater() throws Exception {

        updater = new LedgerUpdater(network, false);
    }

    @Test
    public void addToEmptyChain() throws Exception {
        // [] + [b1] = [b1]

        Ledger ledger = new EmptyLedger();

        Ledger updatedLedger = updater.update(ledger, block1);

        assertEquals(1, updatedLedger.size());
        assertEquals(block1, updatedLedger.getEndOfChain());
    }

    @Test
    public void dontAddToStartOfChain() throws Exception {
        // [b1] + [b4] = [b1]

        Ledger ledger = new EmptyLedger().append(block1);

        Block block4 = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("407")
                .withTransaction(entry1)
                .build();

        Ledger updatedLedger = updater.update(ledger, block4);

        assertEquals(1, updatedLedger.size());
        assertEquals(block1, updatedLedger.getEndOfChain());
    }

    @Test
    public void addToStartOfChain() throws Exception {
        // [b1] + [b4,b5] = [b4,b5]

        Ledger ledger = new EmptyLedger().append(block1);

        Block block4 = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("407")
                .withTransaction(entry1)
                .build();

        Block block5 = new Block.Builder()
                .withPreviousHash(block4.getHash())
                .withUserId("bob")
                .withTimestamp(5L)
                .withProofOfWork("244")
                .withTransaction(entry1)
                .build();

        when(network.requestBlock(block4.getHash())).thenReturn(block4);

        Ledger updatedLedger = updater.update(ledger, block5);

        assertEquals(2, updatedLedger.size());
        assertEquals(block5, updatedLedger.getEndOfChain());
        assertEquals(block4, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
    }

    @Test
    public void dontAddToMiddleOfChain() throws Exception {
        // [b1,b2,b3] + [b1,b4,b5] = [b1,b2,b3]

        Block block4 = new Block.Builder()
                .withPreviousHash(block1.getHash())
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("192")
                .withTransaction(entry1)
                .build();

        Block block5 = new Block.Builder()
                .withPreviousHash(block4.getHash())
                .withUserId("bob")
                .withTimestamp(5L)
                .withProofOfWork("236")
                .withTransaction(entry1)
                .build();

        when(network.requestBlock(block4.getHash())).thenReturn(block4);

        Ledger updatedLedger = updater.update(ledger, block5);

        assertEquals(3, updatedLedger.size());
        assertEquals(block3, updatedLedger.getEndOfChain());
        assertEquals(block2, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(block1, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
    }

    @Test
    public void addToMiddleOfChain() throws Exception {
        // [b1,b2,b3] + [b1,b4,b5,b6] = [b1,b4,b5,b6]

        Block block4 = new Block.Builder()
                .withPreviousHash(block1.getHash())
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("192")
                .withTransaction(entry1)
                .build();

        Block block5 = new Block.Builder()
                .withPreviousHash(block4.getHash())
                .withUserId("bob")
                .withTimestamp(5L)
                .withProofOfWork("236")
                .withTransaction(entry1)
                .build();

        Block block6 = new Block.Builder()
                .withPreviousHash(block5.getHash())
                .withUserId("chris")
                .withTimestamp(6L)
                .withProofOfWork("104")
                .withTransaction(entry1)
                .build();

        when(network.requestBlock(block4.getHash())).thenReturn(block4);
        when(network.requestBlock(block5.getHash())).thenReturn(block5);

        Ledger updatedLedger = updater.update(ledger, block6);

        assertEquals(4, updatedLedger.size());
        assertEquals(block6, updatedLedger.getEndOfChain());
        assertEquals(block5, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(block4, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
        assertEquals(block1, updatedLedger.withoutNewestBlocks(3).getEndOfChain());
    }

    @Test
    public void addToEndOfChain() throws Exception {
        // [b1,b2,b3] + [b1,b2,b3,b4] = [b1,b2,b3,b4]

        Block block4 = new Block.Builder()
                .withPreviousHash(block3.getHash())
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("729")
                .withTransaction(entry1)
                .build();

        Ledger updatedLedger = updater.update(ledger, block4);

        assertEquals(4, updatedLedger.size());
        assertEquals(block4, updatedLedger.getEndOfChain());
        assertEquals(block3, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(block2, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
        assertEquals(block1, updatedLedger.withoutNewestBlocks(3).getEndOfChain());
    }

    @Test
    public void invalidBlock() throws Exception {

        Ledger ledger = new EmptyLedger();

        // no block
        Block block = null;

        assertEquals(ledger, updater.update(ledger, block));

        // empty block
        block = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("522")
                .build();

        assertEquals(ledger, updater.update(ledger, block));

        // block with wrong proof-of-work
        block = new Block.Builder()
                .withUserId("alice")
                .withTimestamp(4L)
                .withProofOfWork("406")
                .withTransaction(entry1)
                .build();

        assertEquals(ledger, updater.update(ledger, block));
    }

    @Test
    public void invalidEntry() throws Exception {

        // entry = null
        assertFalse(updater.verify(ledger, null, false, false));

        // entry doesn't have compulsory values
        when(t4.hasCompulsoryValues()).thenReturn(false);

        assertFalse(updater.verify(ledger, entry4, false, false));

        //entry has unknown sender
        when(t3.hasCompulsoryValues()).thenReturn(true);

        when(t3.getFrom()).thenReturn("unknown");

        when(network.requestIdentity("unknown")).thenReturn(null);

        assertFalse(updater.verify(ledger, entry3, false, false));

        // entry not signed by sender
        when(t2.hasCompulsoryValues()).thenReturn(true);

        when(t2.getFrom()).thenReturn("alice");

        when(network.requestIdentity("alice")).thenReturn(alice.getIdentity());

        assertFalse(updater.verify(ledger, entry2, false, false));

        // entry not valid

        when(t1.hasCompulsoryValues()).thenReturn(true);

        when(t1.getFrom()).thenReturn("alice");

        when(network.requestIdentity("alice")).thenReturn(alice.getIdentity());

        when(t1.verify(ledger, false, false)).thenReturn(false);

        assertFalse(updater.verify(ledger, entry1, false, false));

        // entry valid

        when(t1.hasCompulsoryValues()).thenReturn(true);

        when(t1.getFrom()).thenReturn("alice");

        when(network.requestIdentity("alice")).thenReturn(alice.getIdentity());

        when(t1.verify(ledger, false, false)).thenReturn(true);

        assertTrue(updater.verify(ledger, entry1, false, false));
    }
}
