package com.example.resepmakanan.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.resepmakanan.Activities.WelcomeActivity;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.R;

public class SettingFragment extends Fragment {

    private CardView cvLogout;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setting_page, container, false);

        cvLogout = view.findViewById(R.id.logOut);

        cvLogout.setOnClickListener(v -> {
            // clear session
            SessionManager session = new SessionManager(requireContext());
            session.logout();

            // go to welcome screen
            Intent intent = new Intent(requireContext(), WelcomeActivity.class);
            startActivity(intent);

            // close main activity
            requireActivity().finish();
        });

        return view;
    }
}
