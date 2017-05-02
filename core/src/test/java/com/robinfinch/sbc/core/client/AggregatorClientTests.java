package com.robinfinch.sbc.core.client;

public class AggregatorClientTests {
/*
    private AggregatorClient client;

    @Mock
    UserStore userStore;

    @Mock
    LedgerStore ledgerStore;

    @Mock
    Network network;

    @Before
    public void setUpClient() {
        MockitoAnnotations.initMocks(this);

        client = new AggregatorClient(userStore, ledgerStore, 2, network);
    }

    @Test
    public void publishBlock() throws Exception {

        AssetFactory assetFactory = new ValueAssetFactory();

        IncentivePolicy policy = new LimitedValuePolicy(1, 8, assetFactory);

        when(network.getIncentivePolicy()).thenReturn(policy);

        when(network.getAssetFactory()).thenReturn(assetFactory);

        User bob = createBob();
        User chris = createChris();
        User dave = createDave();

        when(network.requestIdentity("bob")).thenReturn(bob.getIdentity());
        when(network.requestIdentity("chris")).thenReturn(chris.getIdentity());

        when(userStore.load()).thenReturn(dave);

        Payment t2 = createTransaction2();
        Payment t4 = createTransaction4();

        Block b2 = createBlock2(t2, t4);

        Payment t5 = createTransaction5();
        Payment t6 = createTransaction6();
        Payment t7 = createTransaction7();

        Block b3 = createBlock3(t5, t6, t7);

        Payment t8 = createTransaction8();
        Payment t9 = createTransaction9();
        Payment t10 = createTransaction10();

        Block b4 = createBlock4(t8, t9, t10);

        Payment t12 = createTransaction12();
        Payment t13 = createTransaction13();
        Payment t14 = createTransaction14();
        Payment t15 = createTransaction15();

        Block b6 = createBlock6(t12, t13, t14);

        Payment t17 = createTransaction17();
        Payment t18 = createTransaction18();

        Block b7 = createBlock7(t15, t17, t18);

        Ledger ledger = new EmptyLedger().append(b2).append(b3).append(b4);

        when(ledgerStore.load())
                .thenReturn(ledger)
                .thenReturn(ledger)
                .thenReturn(ledger)
                .thenReturn(ledger.append(b6))
                .thenReturn(ledger.append(b6));

        when(network.getTime())
                .thenReturn(24L)
                .thenReturn(25L);

        client.start();

        client.onTransactionPublished(t13);

        client.onTransactionPublished(t15);

        client.onBlockPublished(b6);

        client.onTransactionPublished(t17);

        verify(network).publish(b7);
    }
    */
}
