package com.robinfinch.sbc.simulation.carreg;

import com.robinfinch.sbc.aggregator.AggregatorClient;
import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.config.*;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;
import com.robinfinch.sbc.transactor.TransactorClient;

public class Bob8121 {

    public static void main(String[] args) {

        AssetFactory assetFactory = new UniqueAssetFactory();

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withIncentivePolicy(new BlockSizePolicy(2))
                .withAssetFactory(assetFactory)
                .withPeerUrl("http://localhost:8121")
                .withPort(8122)
                .build();

        AggregatorClient client = new AggregatorClient(
                new MemoryUserStore(),
                new MemoryLedgerStore(),
                0,
                new NetworkClient(config));

        try {
            client.init("bob");

            client.start();

            Thread.sleep(800);

            client.introduce("BD12 PXZ", "bought new car", 0);

            Thread.sleep(800);

            client.transfer("alice", "BD12 PXZ", "sold to alice", 0);

            Thread.sleep(800);

            System.out.println("Wallet: " + client.getWallet(0));

            client.stop();

        } catch (ConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
