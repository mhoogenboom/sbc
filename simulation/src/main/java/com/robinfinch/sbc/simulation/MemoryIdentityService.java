package com.robinfinch.sbc.simulation;

import com.google.gson.Gson;
import com.robinfinch.sbc.p2p.identity.IdentityTo;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static io.undertow.Handlers.path;

public class MemoryIdentityService {

    public static void main(String[] args) {

        new MemoryIdentityService().start();
    }

    private final Undertow server;

    private final Gson gson;

    private final Map<String, IdentityTo> identities;

    private MemoryIdentityService() {

        server = Undertow.builder()
                .addHttpListener(8120, "localhost")
                .setHandler(path()
                        .addPrefixPath("/identity", this::onIdentityRequest)
                        .addPrefixPath("/stop", this::stop))
                .build();

        gson = new Gson();

        identities = new HashMap<>();
    }

    private void start() {
        server.start();
    }

    private void onIdentityRequest(HttpServerExchange exchange) {
        exchange.dispatch(() -> {
            if (exchange.getRequestMethod().equals(Methods.POST)) {
                exchange.startBlocking();

                InputStream in = exchange.getInputStream();

                IdentityTo identityTo = gson.fromJson(new InputStreamReader(in), IdentityTo.class);

                exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                        .endExchange();

                identities.put("/" + identityTo.getUserId(), identityTo);
            } else {
                String id = exchange.getRelativePath();

                IdentityTo identityTo = identities.get(id);

                if (identityTo == null) {
                    exchange.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND)
                            .endExchange();
                } else {
                    exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                            .getResponseSender().send(gson.toJson(identityTo));
                }
            }
        });
    }

    private void stop(HttpServerExchange exchange) {

        exchange.dispatch(() -> {

            exchange.setStatusCode(HttpURLConnection.HTTP_OK)
                    .endExchange();

            server.stop();
        });
    }
}
