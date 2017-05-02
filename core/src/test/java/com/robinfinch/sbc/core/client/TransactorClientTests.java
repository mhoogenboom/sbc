package com.robinfinch.sbc.core.client;

import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.EmptyLedger;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactorClientTests extends TestData {

    private TransactorClient client;

    @Mock
    UserStore userStore;

    @Mock
    LedgerStore ledgerStore;

    @Mock
    Network network;

    @Test
    public void getWallet() {

        client = new TransactorClient(userStore, ledgerStore, network);

        when(userStore.load()).thenReturn(alice);

        when(ledgerStore.load()).thenReturn(ledger);

        when(t1.getFrom()).thenReturn("bob");
        when(t1.hasValueFor("alice")).thenReturn(true);

        when(t2.getFrom()).thenReturn("bob");

        when(t3.getFrom()).thenReturn("bob");

        when(t4.getFrom()).thenReturn("bob");
        when(t4.hasValueFor("alice")).thenReturn(true);

        when(t5.getFrom()).thenReturn("bob");
        when(t5.hasValueFor("alice")).thenReturn(true);

        when(t6.getFrom()).thenReturn("alice");
        when(t6.hasSource(entry4.getHash())).thenReturn(true);

        assertEquals(asList(entry1), client.getWallet(1));

        assertEquals(asList(entry1, entry5), client.getWallet(0));
    }

    @Test
    public void onBlockPublished() {

        client = new TransactorClient(userStore, ledgerStore, network);

        when(ledgerStore.load()).thenReturn(new EmptyLedger());

        client.onBlockPublished(block1);

        Ledger updatedLedger = new EmptyLedger().append(block1);

        verify(ledgerStore).store(updatedLedger);
    }

    @Test
    public void provideBlock() {

        client = new TransactorClient(userStore, ledgerStore, network);

        when(ledgerStore.load()).thenReturn(ledger);

        assertEquals(block1, client.provideBlock(BLOCK1_HASH));
    }
}
