package co.artsoft.migraineapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.artsoft.migraineapp.R;
import co.artsoft.migraineapp.recording.Recording;

/**
 * Created by darthian on 6/26/17.
 */

public class Home extends Fragment{

    Button btnIrAReportar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View root = inflater.inflate(R.layout.home_layout, null);
        btnIrAReportar = (Button) root.findViewById(R.id.btnIrAReportar);

        btnIrAReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Recording.class);
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0,0);
            }
        });

        return root;
    }

}
