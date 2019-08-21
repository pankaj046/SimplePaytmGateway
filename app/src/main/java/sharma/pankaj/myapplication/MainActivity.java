package sharma.pankaj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Random random = new Random();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int OrderId = random.nextInt(1000000);

                getChecksum(""+OrderId);
            }
        });


    }

    public void getChecksum(String OrderId){

        Toast.makeText(this, "Call Retrofit", Toast.LENGTH_SHORT).show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CallBack callBack = retrofit.create(CallBack.class);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("MID", Constants.M_ID);
        hashMap.put("ORDER_ID", OrderId);
        hashMap.put("CUST_ID", "CUST123");
        hashMap.put("CHANNEL_ID", Constants.CHANNEL_ID);
        hashMap.put("TXN_AMOUNT", "1000.32");
        hashMap.put("WEBSITE", Constants.WEBSITE);
        hashMap.put("CALLBACK_URL", Constants.CALLBACK_URL+OrderId);
        hashMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID);

        Call<ChecksumResponse> call = callBack.getCheackSumResponse(hashMap);
        call.enqueue(new Callback<ChecksumResponse>() {
            @Override
            public void onResponse(Call<ChecksumResponse> call, Response<ChecksumResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, ""+response.body().getChecksumHash(), Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    PayWithPaytm(response.body().getOrderId(), response.body().getChecksumHash());
                }
            }

            @Override
            public void onFailure(Call<ChecksumResponse> call, Throwable t) {

            }
        });
    }


    public void PayWithPaytm(String OrderId, String CheckSum){

        PaytmPGService Service = PaytmPGService.getStagingService();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("MID", Constants.M_ID);
        hashMap.put("ORDER_ID", OrderId);
        hashMap.put("CUST_ID", "CUST123");
        hashMap.put("CHANNEL_ID", Constants.CHANNEL_ID);
        hashMap.put("TXN_AMOUNT", "1000.32");
        hashMap.put("WEBSITE", Constants.WEBSITE);
        hashMap.put("CHECKSUMHASH",  CheckSum);
        hashMap.put("CALLBACK_URL", Constants.CALLBACK_URL+OrderId);
        hashMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID);

        PaytmOrder order = new PaytmOrder(hashMap);
        Service.initialize(order, null);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Toast.makeText(MainActivity.this, inResponse.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(MainActivity.this, "Check Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Toast.makeText(MainActivity.this, inErrorMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(MainActivity.this, inErrorMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Toast.makeText(MainActivity.this,iniErrorCode+"\n"+inErrorMessage.toString()+"\n"+inFailingUrl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {

            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Toast.makeText(MainActivity.this, inErrorMessage+"\n"+inResponse, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
