package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UserTests extends Tests {

    @Test
    public void bobSignsTransaction() throws Exception {

        User user = createBob();

        assertEquals("bob", user.getId());

        Transaction transaction = createTransaction13();

        assertArrayEquals(T13_SIGNATURE, user.sign(transaction).getSignature());
    }
}
