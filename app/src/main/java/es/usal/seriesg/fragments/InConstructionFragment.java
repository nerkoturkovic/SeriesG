package es.usal.seriesg.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.usal.seriesg.activity.R;

/**
 * Created by nerko on 15/7/15.
 */
public class InConstructionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.construction, container, false);

        return v;
    }
}
