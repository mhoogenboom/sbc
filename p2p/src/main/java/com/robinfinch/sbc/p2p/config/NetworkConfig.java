package com.robinfinch.sbc.p2p.config;

import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.network.IncentivePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class NetworkConfig {

    private final String identityServiceUrl;
    private final IncentivePolicy incentivePolicy;
    private final AssetFactory assetFactory;
    private final int port;
    private final List<String> peerUrls;
    private final Level logLevel;

    private NetworkConfig(String identityServiceUrl, IncentivePolicy incentivePolicy, AssetFactory assetFactory, int port,
                          List<String> peerUrls, Level logLevel) {

        this.identityServiceUrl = identityServiceUrl;
        this.incentivePolicy = incentivePolicy;
        this.assetFactory = assetFactory;
        this.port = port;
        this.peerUrls = new ArrayList<>(peerUrls);
        this.logLevel = logLevel;
    }

    public String getIdentityServiceUrl() {
        return identityServiceUrl;
    }

    public IncentivePolicy getIncentivePolicy() {
        return incentivePolicy;
    }

    public AssetFactory getAssetFactory() {
        return assetFactory;
    }

    public int getPort() {
        return port;
    }

    public List<String> getPeerUrls() {
        return peerUrls;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public static class Builder {

        private String identityServiceUrl;
        private IncentivePolicy incentivePolicy;
        private AssetFactory assetFactory;
        private int port;
        private List<String> peerUrls;
        private Level logLevel;

        public Builder() {
            peerUrls = new ArrayList<>();
        }

        public Builder withIdentityServiceUrl(String identityServiceUrl) {
            this.identityServiceUrl = identityServiceUrl;
            return this;
        }

        public Builder withIncentivePolicy(IncentivePolicy incentivePolicy) {
            this.incentivePolicy = incentivePolicy;
            return this;
        }

        public Builder withAssetFactory(AssetFactory assetFactory) {
            this.assetFactory = assetFactory;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withPeerUrl(String peerUrl) {
            this.peerUrls.add(peerUrl);
            return this;
        }

        public Builder withLogLevel(Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public NetworkConfig build() {
            return new NetworkConfig(identityServiceUrl, incentivePolicy, assetFactory, port, peerUrls, logLevel);
        }
    }
}
