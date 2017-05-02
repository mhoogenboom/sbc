package com.robinfinch.sbc.p2p.peer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Peer {

    @POST("/transaction")
    Call<Void> sendEntry(@Body EntryTo transaction);

    @POST("/block")
    Call<Void> sendBlock(@Body BlockTo block);

    @GET("/block/{hash}")
    Call<BlockTo> requestBlock(@Path(value = "hash", encoded = true) String hash);
}
