package sharma.pankaj.myapplication;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CallBack {

    @FormUrlEncoded
    @POST("/generateChecksum.php")
    Call<ChecksumResponse> getCheackSumResponse(@FieldMap HashMap<String , String> hashMap);
}
