package com.robinfinch.sbc.simulation.evotes;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.domain.vote.PollingStation;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.NetworkConfig;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;

public class Chris8123 {

    public static void main(String[] args) {

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withPeerUrl("http://localhost:8121")
                .withPeerUrl("http://localhost:8122")
                .withPort(8123)
                .build();

        PollingStation client = new PollingStation(
                new MemoryUserStore(),
                new MemoryLedgerStore(),
                NetworkClient.configure(config),
                3);

        try {
            client.init("chris");

            client.start();

            Thread.sleep(10000);

            client.stop();

        } catch (ConfigurationException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
