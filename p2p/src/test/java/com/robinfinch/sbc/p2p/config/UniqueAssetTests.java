package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UniqueAssetTests extends Tests {

    @Test
    public void getters() {

        assertNull(new UniqueAsset().getId());
        assertEquals("BD12 PXZ", new UniqueAsset("BD12 PXZ").getId());
    }

    @Test
    public void checkIfValid() {

        assertFalse(new UniqueAsset().isValid());
        assertTrue(new UniqueAsset("BD12 PXZ").isValid());
    }

    @Test
    public void plus() throws Exception {

        Transaction transaction = createTransaction19();

        UniqueAsset asset = new UniqueAsset("BD12 PXZ").plus("bob", transaction);
        assertNull(asset);
    }

    @Test
    public void times() throws Exception {

        UniqueAsset asset = new UniqueAsset("BD12 PXZ").times(3);
        assertNull(asset);
    }

    @Test
    public void computeChange() {

        UniqueAsset asset = new UniqueAsset("BD12 PXZ").computeChange(new UniqueAsset("WG07 RJX"), 1);
        assertNull(asset);
    }

    @Test
    public void checkForChange() {

        assertFalse(new UniqueAsset("BD12 PXZ").hasChange());
    }

    @Test
    public void covers() {

        assertTrue(new UniqueAsset("BD12 PXZ").covers(new UniqueAsset("BD12 PXZ")));
        assertFalse(new UniqueAsset("BD12 PXZ").covers(new UniqueAsset("WG07 RJX")));

        assertTrue(new UniqueAsset("BD12 PXZ").covers(new UniqueAsset("BD12 PXZ"), 0));
        assertFalse(new UniqueAsset("BD12 PXZ").covers(new UniqueAsset("BD12 PXZ"), 1));
    }
}
