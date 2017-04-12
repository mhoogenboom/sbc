package com.robinfinch.sbc.transactor;

import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.*;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.p2p.config.ValueAsset;
import com.robinfinch.sbc.p2p.config.ValueAssetFactory;
import com.robinfinch.sbc.testdata.Tests;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactorClientTests extends Tests {

    private TransactorClient client;

    @Mock
    UserStore userStore;

    @Mock
    LedgerStore ledgerStore;

    @Mock
    Network network;

    @Before
    public void setUpClient() {
        MockitoAnnotations.initMocks(this);

        AssetFactory assetFactory = new ValueAssetFactory();

        client = new TransactorClient(userStore, ledgerStore, network);

        when(network.getAssetFactory()).thenReturn(assetFactory);
    }

    @Test
    public void transfer() throws Exception {

        User bob = createBob();

        when(userStore.load()).thenReturn(bob);

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

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load()).thenReturn(ledger);

        when(network.getTime()).thenReturn(17L);

        assertTrue(client.transfer("alice", 1, "bob-001", 1, 0));

        Transaction t13 = createTransaction13();

        verify(network).publish(t13);
    }

    @Test
    public void dontTransferTransactionNotConfirmed() throws Exception {

        User bob = createBob();

        when(userStore.load()).thenReturn(bob);

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

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load()).thenReturn(ledger);

        when(network.getTime()).thenReturn(17L);

        assertFalse(client.transfer("alice", 1, "bob-001", 1, 1));
    }

    @Test
    public void dontTransferNotEnoughUnspent() throws Exception {

        User bob = createBob();

        when(userStore.load()).thenReturn(bob);

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

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load()).thenReturn(ledger);

        when(network.getTime()).thenReturn(17L);

        assertFalse(client.transfer("alice", 3, "bob-001", 1,0));
    }

    @Test
    public void received() throws Exception {

        User alice = createAlice();

        when(userStore.load()).thenReturn(alice);

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

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load()).thenReturn(ledger);

        assertTrue(client.hasReceived("chris", 1, "chris-001", 1));
    }

    @Test
    public void notReceived() throws Exception {

        User alice = createAlice();

        when(userStore.load()).thenReturn(alice);

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

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load()).thenReturn(ledger);

        assertFalse(client.hasReceived("bob", 1, "chris-001", 1));
        assertFalse(client.hasReceived("chris", 2, "chris-001", 1));
        assertFalse(client.hasReceived("chris", 1, "bob-001", 1));
        assertFalse(client.hasReceived("chris", 1, "chris-001", 2));
    }

    @Test
    public void mergeBlock() throws Exception {

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger ledger = new EmptyLedger();

        when(ledgerStore.load()).thenReturn(ledger);

        client.onBlockPublished(b1);

        ArgumentCaptor<Ledger> arg = ArgumentCaptor.forClass(Ledger.class);
        verify(ledgerStore).store(arg.capture());

        Ledger updatedLedger = arg.getValue();

        assertEquals(1, updatedLedger.size());
        assertEquals(b1, updatedLedger.getEndOfChain());
    }

    @Test
    public void provideBlock() throws Exception {

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger ledger = new EmptyLedger().append(b1);

        when(ledgerStore.load()).thenReturn(ledger);

        Block block = client.provideBlock(B1_HASH);

        assertEquals(b1, block);
    }

    @Test
    public void dontProvideBlock() throws Exception {

        Transaction t1 = createTransaction1();
        Transaction t3 = createTransaction3();

        Block b1 = createBlock1(t1, t3);

        Ledger ledger = new EmptyLedger().append(b1);

        when(ledgerStore.load()).thenReturn(ledger);

        Block block = client.provideBlock(B2_HASH);

        assertNull(block);
    }
}
