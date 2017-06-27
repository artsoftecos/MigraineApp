package co.artsoft.migraineapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.artsoft.migraineapp.R;

/**
 * Created by darthian on 6/26/17.
 */

public class HistoryEpisode extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.history_episode, null);
    }
}
