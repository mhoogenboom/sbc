package com.robinfinch.sbc.p2p.identity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IdentityService {

    @POST("identity")
    Call<Void> register(@Body IdentityTo identity);

    @GET("identity/{userId}")
    Call<IdentityTo> requestIdentity(@Path(value="userId", encoded=true) String userId);
}
