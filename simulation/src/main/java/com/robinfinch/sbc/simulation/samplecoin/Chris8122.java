package com.robinfinch.sbc.simulation.samplecoin;

import com.robinfinch.sbc.aggregator.AggregatorClient;
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

public class Chris8122 {

    public static void main(String[] args) {

        AssetFactory assetFactory = new ValueAssetFactory();

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withIncentivePolicy(new LimitedValuePolicy(25, 1000, assetFactory))
                .withAssetFactory(assetFactory)
                .withPeerUrl("http://localhost:8121")
                .withPeerUrl("http://localhost:8122")
                .withPort(8123)
                .build();

        AggregatorClient client = new AggregatorClient(
                new MemoryUserStore(),
                new MemoryLedgerStore(),
                3,
                new NetworkClient(config));

        try {
            client.init("chris");

            client.start();

            Thread.sleep(200);

            client.publishEmptyBlock();

            Thread.sleep(200);

            client.publishEmptyBlock();

            Thread.sleep(200);

            client.transfer("bob", 20, "chris-001", 2, 0);

            Thread.sleep(200);

            client.transfer("bob", 20, "chris-002", 2, 0);

            Thread.sleep(10000);

            client.stop();

        } catch (ConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
