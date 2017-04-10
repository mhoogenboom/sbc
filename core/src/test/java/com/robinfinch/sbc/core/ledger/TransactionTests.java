package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.testdata.Tests;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class TransactionTests extends Tests {

    @Test
    public void introductionTransaction() throws Exception {

        Transaction transaction = createTransaction8();

        assertNull(transaction.getFrom());
        assertEquals("chris", transaction.getTo());
        assertEquals(1, transaction.getFee());
        assertNull(transaction.getReference());
        assertEquals(11L, transaction.getTimestamp());

        {
            byte[] expected = {110, 117, 108, 108, 58, 99, 104, 114, 105, 115, 58, 49, 58, 48,
                    58, 49, 58, 110, 117, 108, 108, 58, 49, 49};
            assertArrayEquals(expected, transaction.getHeader());
        }

        assertTrue(transaction.getSources().isEmpty());

        assertNull(transaction.getSignature());

        assertEquals(T8_HASH, transaction.getHash());
    }

    @Test
    public void signedTransaction() throws Exception {

        Transaction transaction = createTransaction9();

        assertEquals("chris", transaction.getFrom());
        assertEquals("bob", transaction.getTo());
        assertEquals(0, transaction.getFee());
        assertEquals("chris-002", transaction.getReference());
        assertEquals(12L, transaction.getTimestamp());

        {
            byte[] expected = {99, 104, 114, 105, 115, 58, 98, 111, 98, 58, 51, 58, 48, 58, 48, 58, 99, 104,
                    114, 105, 115, 45, 48, 48, 50, 58, 49, 50};
            assertArrayEquals(expected, transaction.getHeader());
        }

        assertEquals(asList(T4_HASH, T5_HASH, T7_HASH), transaction.getSources());

        assertArrayEquals(T9_SIGNATURE, transaction.getSignature());

        assertEquals(T9_HASH, transaction.getHash());
    }

    @Test
    public void feeTransaction() throws Exception {

        Transaction transaction = createTransaction10();

        assertNull(transaction.getFrom());
        assertEquals("chris", transaction.getTo());
        assertEquals(0, transaction.getFee());
        assertNull(transaction.getReference());
        assertEquals(13L, transaction.getTimestamp());

        {
            byte[] expected = {110, 117, 108, 108, 58, 99, 104, 114, 105, 115, 58, 49, 58, 48, 58, 48, 58,
                    110, 117, 108, 108, 58, 49, 51};
            assertArrayEquals(expected, transaction.getHeader());
        }

        assertTrue(transaction.getSources().isEmpty());

        assertNull(transaction.getSignature());

        assertEquals(T10_HASH, transaction.getHash());
    }
}
