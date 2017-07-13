package co.artsoft.migraineapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.artsoft.migraineapp.R;
import co.artsoft.migraineapp.cipher.RSACipher;
import co.artsoft.migraineapp.recording.Recording;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by darthian on 6/26/17.
 */

public class HistoryEpisode extends Fragment {

    private Button btnIrAReportar;
    private Response response;
    private ProgressDialog progress;
    private RSACipher cipher;
    String msg;
    String cifrado;
    String decifrado;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View root =  inflater.inflate(R.layout.history_episode, null);

        btnIrAReportar = (Button) root.findViewById(R.id.btnTestRSA);

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Enviando");
        progress.setMessage("Por favor espera...");


        try {
            cipher = new RSACipher();

            msg = "{\"pain_level\": 4,\"intensity_level\": 3}";

            /*
            PublicKey pk = cipher.stringToPublicKey("-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtmnWRUnEBH5xJhh1WeYe" +
                    "YofO9nUTTcEK4FBTjucaCUxVAxfDXf+r2OgM8J+2oXcWjJh5kmlulUlRYXRuFYZE" +
                    "7Hi6WKQjVHbO02hzJbiA/JdxhEoOWUtRhUilUR0CxtZLOZ4sG/yQFFnkUt1T6ixY" +
                    "vbxaoZ5RfjoETZuzeDOtwlvjnAQF4AlcUqGNwIvIvzHN8paS4ixC02UQovuGXdY4" +
                    "YOJWZ18JUq4IMXbcmUXszQ1xPOiMbREfL6jjtXfj+laCEmMExTI2uBM3dJSwiCI8" +
                    "rkhqj7m3/eB8BUFxpX/+xQTzAeNmVOZJQQ7gBK6VafDVB45FTR47qkAcRd0FiKGJ" +
                    "AwIDAQAB" +
                    "-----END PUBLIC KEY-----");
            */

            PublicKey pk = cipher.stringToPublicKey("-----BEGIN PUBLIC KEY-----" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp/kou6sGbTe9xa9MYkeOXGGjmj7f22NNbhWnoPHnr/OPfdRaT6KuJDve0sZXkRgrq7i7JLzhwAB3S8GdhLNe3hPaEvDA3DPbeOgRhlLyT4a+Lg+Vn92a5Dm44iXUwKU847XRw0HTNIpgUjS1Sow7vQWJpfy6KcLu4DHmwgHLSJefrercrXefdids/yrZVN6fGCrAQk2I0+KH1QAsI2qQ8SQK5RV3fUEL5tbVYj24NbZQ1WKeQsR8nHXSdKy6FG6ljV02WxtRQSTvuDER0klTFdC+CACK+qSh/ka2u4u2qQ45I0yKudG/JXGBoGPwGU4lofuOYDwbFHWC7kD8Svk2XQIDAQAB" +
                    "-----END PUBLIC KEY-----");


            cifrado = cipher.encrypt(msg, pk);
            //decifrado = cipher.decrypt(cifrado);


            Log.d("I","llave: "+cipher.getPublicKey("pkcs8-pem"));
            Log.d( "I", msg );
            Log.d( "I", cifrado );

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        btnIrAReportar.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {



                progress = new ProgressDialog(getActivity());
                progress.setTitle("Enviando");
                progress.setMessage("Por favor espera...");
                progress.show();

                Thread hiloEnvioArvhivo = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody request_body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                //{"painLevel": 2,"sleepPattern": "poquito","foods": [{"id": 1 },{"id": 2 }], "user" : { "documentNumber" : "1014207336" }}
                                .addFormDataPart("req", cifrado)
                                .build();

                        Request request = new Request.Builder()
                                //.url("http://192.168.1.9:8080/episode/register/")
                                //.url("http://34.212.247.226:8080/migraineFeatures/episode/register/")
                                .url("https://arquitectura-uniandes-melga.c9users.io/")
                                .post(request_body)
                                .build();

                        try {

                            response = client.newCall(request).execute();

                            if (response.isSuccessful()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(), "Reporte enviado satisfactoriamente", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                        try {
                                            Log.v("Respuesta: ", response.body().string());
                                        } catch (IOException e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "Hubo un inconveniente. Se hará un reintento automaticamente", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        } catch (android.os.NetworkOnMainThreadException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                Log.d("","");
                            } else {
                                throw new IOException("Error : " + response);
                            }
                            progress.dismiss();

                        } catch (IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(), "Hubo un inconveniente. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                                    //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                                    getActivity().onBackPressed();
                                    //getActivity().finish();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                });

                hiloEnvioArvhivo.start();

            }
        });
        return root;
    }
}
