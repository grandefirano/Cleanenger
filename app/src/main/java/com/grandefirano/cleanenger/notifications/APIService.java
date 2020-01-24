package com.grandefirano.cleanenger.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyCnmAbR0UeoB5KYGC_lQdsOrkX0hdUGV0c"

    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
