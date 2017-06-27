package co.artsoft.migraineapp.recording;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private ProgressDialog progress;
    private Response response;
    Spinner spinner;
    private SeekBar seekBar;
    private TextView nivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        play = (Button) findViewById(R.id.play);
        hold = (Button) findViewById(R.id.hold);
        hold.setBackgroundColor(0xFFD9D9D9);
        send = (Button) findViewById(R.id.send);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        nivel = (TextView) findViewById(R.id.nivel);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lista_patrones, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        nivel.setText(seekBar.getProgress() + "/" + seekBar.getMax());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                nivel.setText(progress + "/" + seekBar.getMax());
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });


        play.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        resetRecording();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        hold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        resetRecording();
                        hold.setBackgroundColor(0xFF00FF00);
                        try {
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IllegalStateException ise) {
                            ise.printStackTrace();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        //Toast.makeText(getApplicationContext(), "Grabación iniciada", Toast.LENGTH_SHORT).show();
                        return true; // if you want to handle the touch event

                    case MotionEvent.ACTION_UP:
                        hold.setBackgroundColor(0xFFD9D9D9);
                        try {
                            myAudioRecorder.stop();
                            myAudioRecorder.release();
                            myAudioRecorder = null;
                            play.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Audio almacenado", Toast.LENGTH_SHORT).show();
                        } catch (RuntimeException re) {
                            Toast.makeText(getApplicationContext(), "Manten presionado el boton para grabar", Toast.LENGTH_SHORT).show();
                        }
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

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
                    e.printStackTrace();
                }

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
                    /* *
                    * Codigo alternativo para ejecutar la peticion y capturar la respuesta
                    * NO BORRAR
                    *
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            Log.v("BK-201 URL: " , response.body().string());
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
                        }
                    });
                    *
                    *
                    * */

                    response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Reporte enviado satisfactoriamente", Toast.LENGTH_SHORT).show();
                                try {
                                    Log.v("Respuesta: " , response.body().string());
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "Hubo un inconveniente. Se hará ub reintento automaticamente", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        throw new IOException("Error : " + response);
                    }
                    progress.dismiss();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Hubo un inconveniente. Se hará ub reintento automaticamente", Toast.LENGTH_SHORT).show();
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