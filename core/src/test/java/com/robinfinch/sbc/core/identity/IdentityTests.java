package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityTests extends TestData {

    @Test
    public void verifyAlicesSignature() throws Exception {

        assertEquals("alice", alice.getIdentity().getUserId());

        assertTrue(alice.getIdentity().hasSigned(entry1));
        assertFalse(bob.getIdentity().hasSigned(entry1));
    }

    @Test
    public void verifyBobsSignature() throws Exception {

        assertEquals("bob", bob.getIdentity().getUserId());

        assertFalse(alice.getIdentity().hasSigned(entry2));
        assertTrue(bob.getIdentity().hasSigned(entry2));
    }

    @Test
    public void noSignature() throws Exception {

        assertFalse(alice.getIdentity().hasSigned(entry3));
        assertFalse(bob.getIdentity().hasSigned(entry3));
    }
}
