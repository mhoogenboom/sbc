package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class LedgerTests extends TestData {

    private static Hash HASH = new Hash(new byte[] {3, -43, 35, -99, -94, -9, -125, 110, -63, -53, -76, -42, 11,
            96, -113, 28, -47, -25, -55, 102, 37, -104, 84, 91, 54, 125, -76, 92, 67, 20, -87, -4});

    @Test
    public void build() throws Exception {

        assertEquals(block3, ledger.getEndOfChain());
        assertEquals(3, ledger.size());
    }

    @Test
    public void findParent() throws Exception {

        Ledger ledger2 = ledger.findChainEndingAt(BLOCK2_HASH);
        assertEquals(block2, ledger2.getEndOfChain());

        ledger2 = ledger.findChainEndingAt(null);
        assertEquals(0, ledger2.size());

        ledger2 = ledger.findChainEndingAt(HASH);
        assertNull(ledger2);
    }
    
    @Test
    public void withoutNewestBlocks() throws Exception {
        
        Ledger ledger2 = ledger.withoutNewestBlocks(2);
        assertEquals(block1, ledger2.getEndOfChain());
        
        ledger2 = ledger.withoutNewestBlocks(4);
        assertEquals(0, ledger2.size());
    }

    @Test
    public void findEntry() {

    }
    
}
