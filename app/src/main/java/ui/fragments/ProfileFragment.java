package ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.recsys.R;
import ui.activities.EditProfileActivity;
import data.database.RecSysDatabase;
import data.database.entities.Profile;

public class ProfileFragment extends Fragment {
    private TextView textAgeValue, textWeightValue, textHeightValue, textGenderValue, textGoalValue;
    private Button btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        textAgeValue = view.findViewById(R.id.textAgeValue);
        textWeightValue = view.findViewById(R.id.textWeightValue);
        textHeightValue = view.findViewById(R.id.textHeightValue);
        textGenderValue = view.findViewById(R.id.textGenderValue);
        textGoalValue = view.findViewById(R.id.textGoalValue);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProfile();
    }

    private void loadProfile() {
        if (getContext() == null) return;
        
        // Выполняем запрос в фоновом потоке, так как allowMainThreadQueries не всегда спасает от лагов
        new Thread(() -> {
            Profile profile = RecSysDatabase.getInstance(getContext()).profileDao().getProfile();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (profile != null && isAdded()) {
                        updateUi(profile);
                    }
                });
            }
        }).start();
    }

    private void updateUi(Profile profile) {
        try {
            if (textAgeValue != null) textAgeValue.setText(String.valueOf(profile.age));
            if (textWeightValue != null) textWeightValue.setText(String.format("%.1f кг", profile.weight));
            if (textHeightValue != null) textHeightValue.setText(String.format("%d см", profile.height));
            if (textGenderValue != null) {
                textGenderValue.setText("male".equals(profile.sex) ? "Мужской" : "Женский");
            }
            
            if (textGoalValue != null) {
                String goalStr;
                if ("gain".equals(profile.goal)) goalStr = "Набор массы";
                else if ("lose".equals(profile.goal)) goalStr = "Похудение";
                else goalStr = "Поддержание веса";
                textGoalValue.setText(goalStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
}
