package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityTests extends Tests {

    private Identity bob;

    private Identity chris;

    @Before
    public void setUpUsers() throws Exception {

        bob = createBob().getIdentity();

        chris = createChris().getIdentity();
    }

    @Test
    public void verifyBobsSignature() throws Exception {

        assertEquals("bob", bob.getUserId());

        Transaction transaction = createTransaction13();

        assertTrue(bob.hasSigned(transaction));
        assertFalse(chris.hasSigned(transaction));
    }

    @Test
    public void verifyChrisSignature() throws Exception {

        assertEquals("chris", chris.getUserId());

        Transaction transaction = createTransaction15();

        assertFalse(bob.hasSigned(transaction));
        assertTrue(chris.hasSigned(transaction));
    }

    @Test
    public void noSignature() throws Exception {

        Transaction transaction = createTransaction18();

        assertFalse(bob.hasSigned(transaction));
        assertFalse(chris.hasSigned(transaction));
    }
}
