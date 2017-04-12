package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LedgerUpdaterTests extends Tests {

    private LedgerUpdater updater;

    @Mock
    Network network;

    @Before
    public void setUpUpdater() throws Exception {
        MockitoAnnotations.initMocks(this);

        updater = new LedgerUpdater(network, true);
    }

    @Test
    public void addToEmptyChain() throws Exception {
        // [] + [b1] = [b1]

        Ledger ledger = new EmptyLedger();

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger updatedLedger = updater.update(ledger, b1);

        assertEquals(1, updatedLedger.size());
        assertEquals(b1, updatedLedger.getEndOfChain());
    }

    @Test
    public void dontAddToStartOfChain() throws Exception {
        // [b1] + [b2] = [b1]

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger ledger = new EmptyLedger().append(b1);

        Transaction t2 = createTransaction2();
        Transaction t4 = createTransaction4();

        Block b2 = createBlock2(t2, t4);

        Ledger updatedLedger = updater.update(ledger, b2);

        assertEquals(1, updatedLedger.size());
        assertEquals(b1, updatedLedger.getEndOfChain());
    }

    @Test
    public void addToStartOfChain() throws Exception {
        // [b1] + [b2,b3] = [b2,b3]

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger ledger = new EmptyLedger().append(b1);

        Transaction t2 = createTransaction2();
        Transaction t4 = createTransaction4();

        Block b2 = createBlock2(t2, t4);

        Transaction t5 = createTransaction5();
        Transaction t6 = createTransaction6();
        Transaction t7 = createTransaction7();

        Block b3 = createBlock3(t5, t6, t7);

        when(network.requestBlock(B2_HASH)).thenReturn(b2);

        Ledger updatedLedger = updater.update(ledger, b3);

        assertEquals(2, updatedLedger.size());
        assertEquals(b3, updatedLedger.getEndOfChain());
        assertEquals(b2, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
    }

    @Test
    public void dontAddToMiddleOfChain() throws Exception {
        // [b2,b3,b4,b5] + [b2,b3,b4,b6] = [b2,b3,b4,b5]

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

        Transaction t11 = createTransaction11();
        Transaction t12 = createTransaction12();
        Transaction t13 = createTransaction13();
        Transaction t14 = createTransaction14();
        Transaction t15 = createTransaction15();
        Transaction t16 = createTransaction16();

        Block b5 = createBlock5(t11, t13, t15, t16);

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4).append(b5);

        Block b6 = createBlock6(t12, t13, t14);

        Ledger updatedLedger = updater.update(ledger, b6);

        assertEquals(4, updatedLedger.size());
        assertEquals(b5, updatedLedger.getEndOfChain());
        assertEquals(b4, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(b3, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
        assertEquals(b2, updatedLedger.withoutNewestBlocks(3).getEndOfChain());
    }

    @Test
    public void addToMiddleOfChain() throws Exception {
        // [b2,b3,b4,b5] + [b2,b3,b4,b6,b7] = [b2,b3,b4,b6,b7]

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

        Transaction t11 = createTransaction11();
        Transaction t12 = createTransaction12();
        Transaction t13 = createTransaction13();
        Transaction t14 = createTransaction14();
        Transaction t15 = createTransaction15();
        Transaction t16 = createTransaction16();

        Block b5 = createBlock5(t11, t13, t15, t16);

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4).append(b5);

        Block b6 = createBlock6(t12, t13, t14);

        Transaction t17 = createTransaction17();
        Transaction t18 = createTransaction18();

        Block b7 = createBlock7(t15, t17, t18);

        when(network.requestBlock(B6_HASH)).thenReturn(b6);

        Ledger updatedLedger = updater.update(ledger, b7);

        assertEquals(5, updatedLedger.size());
        assertEquals(b7, updatedLedger.getEndOfChain());
        assertEquals(b6, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(b4, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
    }

    @Test
    public void addToEndOfChain() throws Exception {
        // [b2,b3] + [b2,b3,b4] = [b2,b3,b4]

        Transaction t2 = createTransaction2();
        Transaction t4 = createTransaction4();

        Block b2 = createBlock2(t2, t4);

        Transaction t5 = createTransaction5();
        Transaction t6 = createTransaction6();
        Transaction t7 = createTransaction7();

        Block b3 = createBlock3(t5, t6, t7);

        Ledger ledger = new EmptyLedger().append(b2).append(b3);

        Transaction t8 = createTransaction8();
        Transaction t9 = createTransaction9();
        Transaction t10 = createTransaction10();

        Block b4 = createBlock4(t8, t9, t10);

        Ledger updatedLedger = updater.update(ledger, b4);

        assertEquals(3, updatedLedger.size());
        assertEquals(b4, updatedLedger.getEndOfChain());
        assertEquals(b3, updatedLedger.withoutNewestBlocks(1).getEndOfChain());
        assertEquals(b2, updatedLedger.withoutNewestBlocks(2).getEndOfChain());
    }
}
