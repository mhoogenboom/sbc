package com.robinfinch.sbc.simulation.samplecoin;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.config.LimitedValuePolicy;
import com.robinfinch.sbc.p2p.config.NetworkConfig;
import com.robinfinch.sbc.p2p.config.ValueAssetFactory;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;
import com.robinfinch.sbc.transactor.TransactorClient;

public class Bob8121 {

    public static void main(String[] args) {

        AssetFactory assetFactory = new ValueAssetFactory();

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withIncentivePolicy(new LimitedValuePolicy(25, 1000, assetFactory))
                .withAssetFactory(assetFactory)
                .withPeerUrl("http://localhost:8121")
                .withPort(8122)
                .withPeerUrl("http://localhost:8123")
                .build();


        TransactorClient client = new TransactorClient(
                new MemoryUserStore(),
                new MemoryLedgerStore(),
                new NetworkClient(config));

        try {
            client.init("bob");

            client.start();

            Thread.sleep(1000);

            while (!client.transfer("alice", 30, "bob-001", 3, 0)) {
                System.out.println("Waiting to have enough to pay alice...");

                Thread.sleep(1000);
            }

            client.stop();

        } catch (ConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
