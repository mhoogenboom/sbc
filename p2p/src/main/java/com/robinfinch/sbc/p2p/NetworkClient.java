package com.robinfinch.sbc.p2p;

import com.google.gson.Gson;
import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.ledger.Asset;
import com.robinfinch.sbc.core.ledger.AssetFactory;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.core.network.IncentivePolicy;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.network.Node;
import com.robinfinch.sbc.p2p.config.NetworkConfig;
import com.robinfinch.sbc.p2p.identity.IdentityService;
import com.robinfinch.sbc.p2p.identity.IdentityTo;
import com.robinfinch.sbc.p2p.peer.BlockTo;
import com.robinfinch.sbc.p2p.peer.Peer;
import com.robinfinch.sbc.p2p.peer.TransactionTo;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.undertow.Handlers.path;
import static java.util.stream.Collectors.toList;

public class NetworkClient implements Network {

    private final NetworkConfig config;

    private final IdentityService identityService;

    private final List<Peer> peers;

    private final Undertow server;

    private final Gson gson;

    private final Logger logger;

    private Node node;

    public NetworkClient(NetworkConfig config) {

        this.config = config;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getIdentityServiceUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        identityService = retrofit.create(IdentityService.class);

        peers = new ArrayList<>();

        for (String url : config.getPeerUrls()) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Peer peer = retrofit.create(Peer.class);
            peers.add(peer);
        }

        server = Undertow.builder()
                .addHttpListener(config.getPort(), "localhost")
                .setHandler(path()
                        .addPrefixPath("/transaction", this::onTransactionRequest)
                        .addPrefixPath("/block", this::onBlockRequest))
                .build();

        gson = new Gson();

        logger = Logger.getLogger("com.robinfinch.sbc.log");
        logger.setLevel(config.getLogLevel());

        logger.log(Level.INFO, "Network Client configured");
    }

    @Override
    public void register(Identity identity) {

        try {
            IdentityTo identityTo = marshall(identity);

            Call<Void> call = identityService.register(identityTo);
            call.execute();

            logger.log(Level.INFO, "Identity for {0} registered", identity.getUserId());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to register identity", e);
        }
    }

    @Override
    public Identity requestIdentity(String userId) {

        try {
            Call<IdentityTo> call = identityService.requestIdentity(userId);

            IdentityTo identityTo = call.execute().body();

            logger.log(Level.INFO, "Got identity for {0}", userId);
            return unmarshall(identityTo);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to get identity", e);
            return null;
        }
    }

    @Override
    public IncentivePolicy getIncentivePolicy() {
        return config.getIncentivePolicy();
    }

    @Override
    public void register(Node node) {

        this.node = node;

        server.start();

        logger.log(Level.INFO, "Network Client started");
    }

    @Override
    public void deregister() {

        server.stop();

        this.node = null;

        logger.log(Level.INFO, "Network Client stopped");
    }

    @Override
    public AssetFactory getAssetFactory() {
        return config.getAssetFactory();
    }

    @Override
    public void publish(Transaction transaction) {

        TransactionTo transactionTo = marshall(transaction);

        for (Peer peer : peers) {
            try {
                Call<Void> call = peer.sendTransaction(transactionTo);
                call.execute();

                logger.log(Level.INFO, "Send transaction {0} to {1}", new Object[]{transaction.getHash(), peer});
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to send transaction", e);
            }
        }
    }

    @Override
    public void publish(Block block) {

        BlockTo blockTo = marshall(block);

        for (Peer peer : peers) {
            try {
                Call<Void> call = peer.sendBlock(blockTo);
                call.execute();

                logger.log(Level.INFO, "Send block {0} to {1}", new Object[]{block.getHash(), peer});
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to send block", e);
            }
        }
    }

    @Override
    public Block requestBlock(Hash hash) {

        for (Peer peer : peers) {
            try {
                Call<BlockTo> call = peer.requestBlock(Base64.getEncoder().encodeToString(hash.value));

                BlockTo blockTo = call.execute().body();

                logger.log(Level.INFO, "Got block {0} from {1}", new Object[]{hash, peer});
                return unmarshall(blockTo);
            } catch (ConfigurationException | IOException e) {
                logger.log(Level.WARNING, "Failed to get block", e);
            }
        }

        return null;
    }

    private void onTransactionRequest(HttpServerExchange exchange) {

        exchange.dispatch(() -> {
            exchange.startBlocking();

            InputStream in = exchange.getInputStream();

            TransactionTo transactionTo = gson.fromJson(new InputStreamReader(in), TransactionTo.class);

            exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                    .endExchange();

            try {
                node.onTransactionPublished(unmarshall(transactionTo));

                logger.log(Level.INFO, "Received transaction");
            } catch (ConfigurationException e) {
                logger.log(Level.WARNING, "Failed to receive transaction", e);
            }
        });
    }

    private void onBlockRequest(HttpServerExchange exchange) {

        exchange.dispatch(() -> {
            if (exchange.getRequestMethod().equals(Methods.POST)) {
                exchange.startBlocking();

                InputStream in = exchange.getInputStream();

                BlockTo blockTo = gson.fromJson(new InputStreamReader(in), BlockTo.class);

                exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                        .endExchange();

                try {
                    node.onBlockPublished(unmarshall(blockTo));

                    logger.log(Level.INFO, "Received block");
                } catch (ConfigurationException e) {
                    logger.log(Level.WARNING, "Failed to receive transaction", e);
                }
            } else {
                String hash = exchange.getRelativePath().substring(1);

                Block block = node.provideBlock(new Hash(Base64.getDecoder().decode(hash)));

                if (block == null) {
                    exchange.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                            .endExchange();
                } else {
                    exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                            .getResponseSender().send(gson.toJson(marshall(block)));
                }
            }
        });
    }

    private IdentityTo marshall(Identity identity) {

        byte[] publicKeyData = identity.getPublicKey().getEncoded();

        IdentityTo identityTo = new IdentityTo();
        identityTo.setUserId(identity.getUserId());
        identityTo.setPublicKeyData(Base64.getEncoder().encodeToString(publicKeyData));

        return identityTo;
    }

    private Identity unmarshall(IdentityTo identityTo) {

        byte[] publicKeyData = Base64.getDecoder().decode(identityTo.getPublicKeyData());

        PublicKey publicKey;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);

            publicKey = kf.generatePublic(publicKeySpec);
        } catch (GeneralSecurityException e) {
            publicKey = null;         // todo
        }

        return new Identity(identityTo.getUserId(), publicKey);
    }

    private BlockTo marshall(Block block) {

        List<TransactionTo> transactions = block.getTransactions()
                .stream()
                .map(this::marshall)
                .collect(toList());

        BlockTo blockTo = new BlockTo();
        if (block.getPreviousHash() != null) {
            blockTo.setPreviousHash(Base64.getEncoder().encodeToString(block.getPreviousHash().value));
        }
        blockTo.setUserId(block.getUserId());
        blockTo.setTimestamp(block.getTimestamp());
        blockTo.setProofOfWork(block.getProofOfWork());
        blockTo.setTransactions(transactions);

        return blockTo;
    }

    private Block unmarshall(BlockTo to) throws ConfigurationException {

        Block.Builder blockBuilder = new Block.Builder()
                .withUserId(to.getUserId())
                .withTimestamp(to.getTimestamp())
                .withProofOfWork(to.getProofOfWork());

        if (to.getPreviousHash() != null) {
            blockBuilder.withPreviousHash(new Hash(Base64.getDecoder().decode(to.getPreviousHash())));
        }

        for (TransactionTo transactionTo : to.getTransactions()) {
            blockBuilder.withTransaction(unmarshall(transactionTo));
        }

        return blockBuilder.build();
    }

    private TransactionTo marshall(Transaction transaction) {

        List<String> sources = transaction.getSources()
                .stream()
                .map(h -> Base64.getEncoder().encodeToString(h.value))
                .collect(toList());

        String asset = getAssetFactory().marshall(transaction.getAsset());

        TransactionTo transactionTo = new TransactionTo();
        transactionTo.setFrom(transaction.getFrom());
        transactionTo.setTo(transaction.getTo());
        transactionTo.setAsset(asset);
        transactionTo.setReference(transaction.getReference());
        transactionTo.setFee(transaction.getFee());
        transactionTo.setTimestamp(transaction.getTimestamp());
        transactionTo.setSources(sources);
        if (transaction.getSignature() != null) {
            transactionTo.setSignature(Base64.getEncoder().encodeToString(transaction.getSignature()));
        }

        return transactionTo;
    }

    private Transaction unmarshall(TransactionTo transactionTo) throws ConfigurationException {

        Asset asset = getAssetFactory().unmarshall(transactionTo.getAsset());

        Transaction.Builder transactionBuilder = new Transaction.Builder()
                .withFrom(transactionTo.getFrom())
                .withTo(transactionTo.getTo())
                .withAsset(asset)
                .withFee(transactionTo.getFee())
                .withReference(transactionTo.getReference())
                .withTimestamp(transactionTo.getTimestamp());

        for (String source : transactionTo.getSources()) {
            transactionBuilder.withSource(new Hash(Base64.getDecoder().decode(source)));
        }

        Transaction transaction = transactionBuilder.build();

        if (transactionTo.getSignature() != null) {
            transaction = new Transaction(transaction,
                    Base64.getDecoder().decode(transactionTo.getSignature()));
        }

        return transaction;
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
