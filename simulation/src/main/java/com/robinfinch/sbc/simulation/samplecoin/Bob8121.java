package com.robinfinch.sbc.simulation.samplecoin;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.client.TransactorClient;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.NetworkConfig;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;

public class Bob8121 {

    public static void main(String[] args) {

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withPeerUrl("http://localhost:8121")
                .withPort(8122)
                .withPeerUrl("http://localhost:8123")
                .build();


        TransactorClient client = new TransactorClient(
                new MemoryUserStore(),
                new MemoryLedgerStore(),
                NetworkClient.configure(config));

        try {
            client.init("bob");

            client.start();

            Thread.sleep(1000);

            client.stop();

        } catch (ConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
