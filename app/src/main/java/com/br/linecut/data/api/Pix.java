package com.br.linecut.data.api;

import android.util.Log;
import com.br.linecut.data.models.PixData;
import com.br.linecut.data.models.PixResponse;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pix {
    private static final String TAG = "PIX_API";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    public PixResponse gerarQRCodePix(double valorTotal, String chavePix) {
        try {
            PixData pixData = new PixData(valorTotal, chavePix);
            String json = gson.toJson(pixData);
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url("https://apipixlinecut.com/")
                    .post(body)
                    .build();


            try (Response response = client.newCall(request).execute()) {
                Log.d(TAG, "O response: " + response);
                if (response.body() == null) {
                    return null;
                }
                // Ler o body uma única vez e armazenar
                String responseBody = response.body().string();


                if (response.isSuccessful()) {
                    return gson.fromJson(responseBody, PixResponse.class);
                } else {
                    Log.e("REQUEST_LOG", "Erro na requisição: " + response.code());
                    throw new RuntimeException("Erro na requisição: " + response.code());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exceção ao gerar QR Code PIX", e);
            e.printStackTrace();
            return null;
        }
    }
}
