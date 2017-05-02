package com.robinfinch.sbc.core.ledger;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class EntryTests extends TestData {

    private static Hash HASH = new Hash(new byte[] {107, -122, -78, 115, -1, 52, -4, -31, -99, 107, -128, 78, -1,
            90, 63, 87, 71, -83, -92, -22, -94, 47, 29, 73, -64, 30, 82, -35, -73, -121, 91, 75});

    @Mock
    Ledger ledger;

    @Test
    public void buildEntry() throws Exception {

//        System.out.println("entry 1 hash = " + entry1.getHash());
//        System.out.println("entry 2 hash = " + entry2.getHash());
//        System.out.println("entry 3 hash = " + entry3.getHash());
//        System.out.println("entry 4 hash = " + entry4.getHash());
//        System.out.println("entry 5 hash = " + entry5.getHash());
//        System.out.println("entry 6 hash = " + entry6.getHash());

        assertEquals(t3, entry3.getTransaction());
        assertEquals(3L, entry3.getTimestamp());
        assertNull(entry3.getSignature());
        assertEquals(ENTRY3_HASH, entry3.getHash());
    }

    @Test
    public void signEntry() throws Exception {

        assertEquals(t1, entry1.getTransaction());
        assertEquals(1L, entry1.getTimestamp());
        assertArrayEquals(ENTRY1_SIGNATURE, entry1.getSignature());
        assertEquals(ENTRY1_HASH, entry1.getHash());
    }

    @Test
    public void checkCompulsoryValues() throws Exception {

        when(t1.hasCompulsoryValues()).thenReturn(true);

        assertTrue(entry1.hasCompulsoryValues());
    }

    @Test
    public void missingTransaction() throws Exception {

        when(t1.hasCompulsoryValues()).thenReturn(true);

        Entry entry = new Entry.Builder()
                .withTimestamp(1L)
                .build();

        assertFalse(entry.hasCompulsoryValues());
    }

    @Test
    public void transactionMissesCompulsoryValues() throws Exception {

        when(t1.hasCompulsoryValues()).thenReturn(false);

        Entry entry = new Entry.Builder()
                .withTransaction(t1)
                .withTimestamp(1L)
                .build();

        assertFalse(entry.hasCompulsoryValues());
    }

    @Test
    public void missingTimestamp() throws Exception {

        when(t1.hasCompulsoryValues()).thenReturn(true);

        Entry entry = new Entry.Builder()
                .withTransaction(t1)
                .build();

        assertFalse(entry.hasCompulsoryValues());
    }

    @Test
    public void verify() throws Exception {

        when(t1.verify(ledger, false, false))
                .thenReturn(false)
                .thenReturn(true);

        assertFalse(entry1.verify(ledger, false, false));

        assertTrue(entry1.verify(ledger, false, false));
    }
}
