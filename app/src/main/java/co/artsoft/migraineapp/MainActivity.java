package co.artsoft.migraineapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.artsoft.migraineapp.recording.Recording;

public class MainActivity extends AppCompatActivity {

    Button btnIrAReportar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIrAReportar = (Button) findViewById(R.id.btnIrAReportar);

        irAReportar(this);
    }

    private void irAReportar(final android.content.Context content) {
        btnIrAReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(content, Recording.class);
                startActivity(intent);
            }
        });
    }


}
