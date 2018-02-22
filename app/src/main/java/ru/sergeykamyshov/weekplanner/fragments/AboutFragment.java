package ru.sergeykamyshov.weekplanner.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.sergeykamyshov.weekplanner.BuildConfig;
import ru.sergeykamyshov.weekplanner.R;

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView versionTextView = view.findViewById(R.id.txt_version);
        String versionText = getString(R.string.about_version) + " " + BuildConfig.VERSION_NAME;
        versionTextView.setText(versionText);

        return view;
    }
}
