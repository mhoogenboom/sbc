package com.robinfinch.sbc.p2p;

import com.robinfinch.sbc.core.identity.Identity;
import com.robinfinch.sbc.p2p.identity.IdentityService;
import com.robinfinch.sbc.p2p.identity.IdentityTo;
import com.robinfinch.sbc.p2p.peer.EntryTo;
import com.robinfinch.sbc.p2p.peer.Peer;
import com.robinfinch.sbc.testdata.TestData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.logging.Level;

import retrofit2.Call;
import retrofit2.Response;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NetworkClientTests extends TestData {

    @Mock
    IdentityService identityService;

    @Mock
    Peer peer1;

    @Mock
    Peer peer2;

    @Mock
    Call call;

    private NetworkClient client;

    @Before
    public void configureClient() {

        NetworkConfig config = new NetworkConfig.Builder()
                .withPort(8000)
                .withLogLevel(Level.OFF)
                .build();

        client = new NetworkClient(identityService, asList(peer1, peer2), config);
    }

    @Test
    public void registerIdentity() throws Exception {

        IdentityTo identityTo = new IdentityTo();
        identityTo.setUserId("alice");
        identityTo.setPublicKeyData("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWdQxSh4VA42NuLn9qeMvaPC5p2GHI1nFGUh29Jqm9G3oiUxtksZkdy4nn/hKeWqXI4DgHqm9xHb9MSxGMbEtunBfM/oSkci6eRHBB+B8OGuRgzjTpNAN1q8w5/G3flMCCQ1FHknTTSNeWWgDsU0Xnc61knn4lcOjTARLfwP6SuwIDAQAB");

        when(identityService.register(identityTo)).thenReturn(call);

        client.register(alice.getIdentity());

        verify(call).execute();
    }

    @Test
    public void requestIdentity() throws Exception {

        IdentityTo identityTo = new IdentityTo();
        identityTo.setUserId("alice");
        identityTo.setPublicKeyData("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWdQxSh4VA42NuLn9qeMvaPC5p2GHI1nFGUh29Jqm9G3oiUxtksZkdy4nn/hKeWqXI4DgHqm9xHb9MSxGMbEtunBfM/oSkci6eRHBB+B8OGuRgzjTpNAN1q8w5/G3flMCCQ1FHknTTSNeWWgDsU0Xnc61knn4lcOjTARLfwP6SuwIDAQAB");

        when(identityService.requestIdentity("alice")).thenReturn(call);

        when(call.execute()).thenReturn(Response.success(identityTo));

        assertEquals(alice.getIdentity(), client.requestIdentity("alice"));
    }

    @Test
    public void publishEntry() throws Exception {

        EntryTo entryTo = new EntryTo();
        entryTo.setTransaction(t1);
        entryTo.setTimestamp(1L);

        when(peer1.sendEntry(entryTo)).thenReturn(call);

        client.publish(entry1);

        verify(call).execute();
    }


}
