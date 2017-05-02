package com.robinfinch.sbc.simulation.samplecoin;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.client.AggregatorClient;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.NetworkConfig;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;

public class Chris8122 {

    public static void main(String[] args) {

        NetworkConfig config = new NetworkConfig.Builder()
                .withIdentityServiceUrl("http://localhost:8120")
                .withPeerUrl("http://localhost:8121")
                .withPeerUrl("http://localhost:8122")
                .withPort(8123)
                .build();

//        AggregatorClient client = new AggregatorClient(
//                new MemoryUserStore(),
//                new MemoryLedgerStore(),
//                NetworkClient.configure(config),
//                3);
//
//        try {
//            client.init("chris");
//
//            client.start();
//
//            Thread.sleep(200);
//
//            client.stop();
//
//        } catch (ConfigurationException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
