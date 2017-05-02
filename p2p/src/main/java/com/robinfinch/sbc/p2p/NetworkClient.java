package com.robinfinch.sbc.p2p;

import com.google.gson.Gson;
import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.core.ledger.Block;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.core.network.Node;
import com.robinfinch.sbc.p2p.identity.IdentityService;
import com.robinfinch.sbc.p2p.identity.IdentityTo;
import com.robinfinch.sbc.p2p.peer.BlockTo;
import com.robinfinch.sbc.p2p.peer.EntryTo;
import com.robinfinch.sbc.p2p.peer.Peer;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private final IdentityService identityService;

    private final List<Peer> peers;

    private final Undertow server;

    private final Gson gson;

    private final Logger logger;

    private Node node;

    public static NetworkClient configure(NetworkConfig config) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getIdentityServiceUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IdentityService identityService = retrofit.create(IdentityService.class);

        List<Peer> peers = new ArrayList<>();

        for (String url : config.getPeerUrls()) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Peer peer = retrofit.create(Peer.class);
            peers.add(peer);
        }

        return new NetworkClient(identityService, peers, config);
    }

    NetworkClient(IdentityService identityService, List<Peer> peers, NetworkConfig config) {

        this.identityService = identityService;
        this.peers = peers;

        server = Undertow.builder()
                .addHttpListener(config.getPort(), "localhost")
                .setHandler(path()
                        .addPrefixPath("/transaction", this::onEntryRequest)
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
    public void publish(Entry entry) {

        EntryTo entryTo = marshall(entry);

        for (Peer peer : peers) {
            try {
                Call<Void> call = peer.sendEntry(entryTo);
                call.execute();

                logger.log(Level.INFO, "Send transaction {0} to {1}", new Object[]{entry.getHash(), peer});
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
                Call<BlockTo> call = peer.requestBlock(Base64.getEncoder().encodeToString(hash.getValue()));

                BlockTo blockTo = call.execute().body();

                logger.log(Level.INFO, "Got block {0} from {1}", new Object[]{hash, peer});
                return unmarshall(blockTo);
            } catch (ConfigurationException | IOException e) {
                logger.log(Level.WARNING, "Failed to get block", e);
            }
        }

        return null;
    }

    private void onEntryRequest(HttpServerExchange exchange) {

        exchange.dispatch(() -> {
            exchange.startBlocking();

            InputStream in = exchange.getInputStream();

            EntryTo entryTo = gson.fromJson(new InputStreamReader(in), EntryTo.class);

            exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                    .endExchange();

            try {
                node.onEntryPublished(unmarshall(entryTo));

                logger.log(Level.INFO, "Received entry");
            } catch (ConfigurationException e) {
                logger.log(Level.WARNING, "Failed to receive entry", e);
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

        List<EntryTo> transactions = block.getEntries()
                .stream()
                .map(this::marshall)
                .collect(toList());

        BlockTo blockTo = new BlockTo();
        if (block.getPreviousHash() != null) {
            blockTo.setPreviousHash(Base64.getEncoder().encodeToString(block.getPreviousHash().getValue()));
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

        for (EntryTo entryTo : to.getTransactions()) {
            blockBuilder.withTransaction(unmarshall(entryTo));
        }

        return blockBuilder.build();
    }

    private EntryTo marshall(Entry entry) {

        EntryTo entryTo = new EntryTo();
        entryTo.setTransaction(entry.getTransaction());
        entryTo.setTimestamp(entry.getTimestamp());
        entryTo.setSignature(Base64.getEncoder().encodeToString(entry.getSignature()));

        return entryTo;
    }

    private Entry unmarshall(EntryTo entryTo) throws ConfigurationException {

        Entry entry = new Entry.Builder()
                .withTransaction(entryTo.getTransaction())
                .withTimestamp(entryTo.getTimestamp())
                .build();

        if (entryTo.getSignature() != null) {
            entry = new Entry(entry,
                    Base64.getDecoder().decode(entryTo.getSignature()));
        }

        return entry;
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
