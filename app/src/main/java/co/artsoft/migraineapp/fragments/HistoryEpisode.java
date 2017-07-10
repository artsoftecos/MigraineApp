package co.artsoft.migraineapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

            PublicKey pk = cipher.stringToPublicKey("-----BEGIN PUBLIC KEY-----" +
                    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC31J578hAXLFIhxfXH0jHOYEjI" +
                    "Cn54PyNe58XUcmCicTyz4NlNNk9k1H6txhRGVSlq+OILvyZyLeJcaoveRLKJrJ25" +
                    "p1vPZC9/Yp3FqkYQvpiLjrjeQTkchtORoXDAXVf+mHPZN13bwGl9fjff7Ene65Ph" +
                    "rBewkuy9kC8PnAWK3wIDAQAB" +
                    "-----END PUBLIC KEY-----");

            cifrado = cipher.encrypt(msg, pk);
            //decifrado = cipher.decrypt(cifrado);

            cifrado = cifrado.replace("\n","");


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
                                Log.d("","");
                            } else {
                                throw new IOException("Error : " + response);
                            }
                            progress.dismiss();

                        } catch (IOException e) {
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
