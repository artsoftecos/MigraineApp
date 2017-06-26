package co.artsoft.migraineapp.recording;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.IOException;

import co.artsoft.migraineapp.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by darthian on 6/24/17.
 */

public class Recording extends AppCompatActivity {

    private Button play, hold, send;
    private MediaRecorder myAudioRecorder;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        play = (Button) findViewById(R.id.play);
        hold = (Button) findViewById(R.id.hold);
        send = (Button) findViewById(R.id.send);
        play.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        resetRecording();


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(getApplicationContext(), "Reproduciendo Audio", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    // make something
                }

            }
        });

        hold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        resetRecording();
                        try {
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IllegalStateException ise) {
                            // make something ...
                        } catch (IOException ioe) {
                            // make something
                        }

                        Toast.makeText(getApplicationContext(), "Grabación iniciada", Toast.LENGTH_SHORT).show();
                        return true; // if you want to handle the touch event

                    case MotionEvent.ACTION_UP:
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;
                        play.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Audio guardado satisfactoriamente", Toast.LENGTH_SHORT).show();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        activarBotonEnvio();

    }

    private void activarBotonEnvio() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarArchivo();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            activarBotonEnvio();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    ProgressDialog progress;
    Response response;


    private void enviarArchivo() {
        progress = new ProgressDialog(Recording.this);
        progress.setTitle("Enviando");
        progress.setMessage("Por favor espera...");
        progress.show();

        Thread hiloEnvioArvhivo = new Thread(new Runnable() {

            @Override
            public void run() {
                File f = new File(outputFile);
                //Se obtiene la extension del archivo
                String content_type = getMimeType(f.getPath());

                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", content_type)
                        .addFormDataPart("data", "{\"id\": null,\"painLevel\": 2,\"sleepPattern\": \"poquito\",\"urlAudioFile\": null,\"foods\": [{\"id\": null,\"name\": \"carne5\"},{\"id\": null,\"name\": \"carne6\"}]}")
                        .addFormDataPart("audioFile", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("http://192.168.1.9:8080/episode/register/")
                        .post(request_body)
                        .build();

                try {
                    response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Reporte enviado satisfactoriamente", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    //Metodo que obtiene la extension del archivo en que se guarda el audio
    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    void resetRecording() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }


}
