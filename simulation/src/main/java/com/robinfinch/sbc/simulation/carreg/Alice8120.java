package com.robinfinch.sbc.simulation.carreg;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.client.AggregatorClient;
import com.robinfinch.sbc.p2p.NetworkClient;
import com.robinfinch.sbc.p2p.NetworkConfig;
import com.robinfinch.sbc.simulation.MemoryLedgerStore;
import com.robinfinch.sbc.simulation.MemoryUserStore;

public class Alice8120 {

    public static void main(String[] args) {

//        AssetFactory assetFactory = new UniqueAssetFactory();
//
//        NetworkConfig config = new NetworkConfig.Builder()
//                .withIdentityServiceUrl("http://localhost:8120")
//                .withIncentivePolicy(new BlockSizePolicy(2))
//                .withAssetFactory(assetFactory)
//                .withPort(8121)
//                .withPeerUrl("http://localhost:8122")
//                .build();
//
//        AggregatorClient client = new AggregatorClient(
//                new MemoryUserStore(),
//                new MemoryLedgerStore(),
//                0,
//                new NetworkClient(config));
//
//        try {
//            client.init("alice");
//
//            client.start();
//
//            Thread.sleep(800);
//
//            client.introduce("WG17 RXJ", "bought new car", 0);
//
//            Thread.sleep(800);
//
//            client.transfer("bob", "WG17 RXJ", "sold to bob", 0);
//
//            Thread.sleep(800);
//
//            System.out.println("Wallet: " + client.getWallet(0));
//
//            client.stop();
//
//        } catch (ConfigurationException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
