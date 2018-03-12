package com.jsdttec.sensordata.service;

import com.jsdttec.sensordata.domain.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/03/09 上午 10:37
 * Desc   : description
 */

public interface SensorDataService {

    @POST
    Call<ResponseBody> getSensorData(@Url String url, @Body String jsonParams);

}
