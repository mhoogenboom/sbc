package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueAssetTests extends Tests {

    @Test
    public void getters() {

        ValueAsset asset1 = new ValueAsset();
        assertEquals(0, asset1.getValue());
        assertEquals(0, asset1.getChange());

        ValueAsset asset2 = new ValueAsset(10, 1);
        assertEquals(10, asset2.getValue());
        assertEquals(1, asset2.getChange());
    }

    @Test
    public void checkIfValid() {

        assertTrue(new ValueAsset(10, 0).isValid());
        assertTrue(new ValueAsset(10, 1).isValid());
        assertFalse(new ValueAsset(0, 0).isValid());
        assertFalse(new ValueAsset(10, -1).isValid());
    }

    @Test
    public void plus() throws Exception {

        Transaction transaction = createTransaction13();

        ValueAsset asset1 = new ValueAsset(10, 1).plus("bob", transaction);
        assertEquals(11, asset1.getValue());
        assertEquals(1, asset1.getChange());

        ValueAsset asset2 = new ValueAsset(10, 1).plus("alice", transaction);
        assertEquals(11, asset2.getValue());
        assertEquals(1, asset2.getChange());
    }

    @Test
    public void times() {

        ValueAsset asset = new ValueAsset(10, 1).times(3);
        assertEquals(30, asset.getValue());
        assertEquals(3, asset.getChange());
    }

    @Test
    public void computeChange() {

        ValueAsset asset = (ValueAsset) new ValueAsset(900, 100).computeChange(new ValueAsset(800, 0), 80);
        assertEquals(800, asset.getValue());
        assertEquals(120, asset.getChange());
    }

    @Test
    public void checkForChange() {

        assertFalse(new ValueAsset(10, 0).hasChange());
        assertTrue(new ValueAsset(10, 1).hasChange());
    }

    @Test
    public void covers() {

        assertTrue(new ValueAsset(900, 100).covers(new ValueAsset(800, 100)));
        assertFalse(new ValueAsset(900, 100).covers(new ValueAsset(801, 100)));

        assertTrue(new ValueAsset(900, 100).covers(new ValueAsset(800, 20), 80));
        assertFalse(new ValueAsset(900, 100).covers(new ValueAsset(801, 20), 80));
    }
}
