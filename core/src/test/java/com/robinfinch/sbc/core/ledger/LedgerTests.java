package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.p2p.config.ValueAsset;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class LedgerTests extends Tests {

    @Test
    public void emptyLedger() throws Exception {

        Ledger ledger = new EmptyLedger();

        assertEquals(0, ledger.size());
        assertNull(ledger.getEndOfChain());
        assertNull(ledger.findChainEndingAt(B3_HASH));
        assertEquals(ledger, ledger.withoutNewestBlocks(1));
        assertFalse(ledger.findWithHash(T2_HASH).isPresent());
        assertFalse(ledger.findWithReference("chris", new ValueAsset(3, 0), "chris-002").isPresent());
        assertEquals(asList(), ledger.findUnspent("chris"));
    }

    @Test
    public void ledger() throws Exception {

        Transaction t2 = createTransaction2();
        Transaction t4 = createTransaction4();

        Block b2 = createBlock2(t2, t4);

        Transaction t5 = createTransaction5();
        Transaction t6 = createTransaction6();
        Transaction t7 = createTransaction7();

        Block b3 = createBlock3(t5, t6, t7);

        Transaction t8 = createTransaction8();
        Transaction t9 = createTransaction9();
        Transaction t10 = createTransaction10();

        Block b4 = createBlock4(t8, t9, t10);

        Ledger ledgerEndingAtBlock3 = new EmptyLedger().append(b2).append(b3);

        Ledger ledger = ledgerEndingAtBlock3.append(b4);

        assertEquals(3, ledger.size());
        assertEquals(b4, ledger.getEndOfChain());
        assertEquals(ledgerEndingAtBlock3, ledger.findChainEndingAt(B3_HASH));
        assertEquals(ledgerEndingAtBlock3, ledger.withoutNewestBlocks(1));
        assertEquals(t2, ledger.findWithHash(T2_HASH).get());
        assertFalse(ledger.findWithReference("alice", new ValueAsset(3, 0), "chris-002").isPresent());
        assertFalse(ledger.findWithReference("chris", new ValueAsset(4, 0), "chris-002").isPresent());
        assertFalse(ledger.findWithReference("chris", new ValueAsset(3, 0), "alice-001").isPresent());
        assertEquals(t9, ledger.findWithReference("chris", new ValueAsset(3, 0), "chris-002").get());
        assertEquals(asList(t8, t10), ledger.findUnspent("chris"));
    }
}
