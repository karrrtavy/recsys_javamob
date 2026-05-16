package ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recsys.R;
import data.database.RecSysDatabase;
import data.database.entities.Profile;

public class SetupActivity extends AppCompatActivity {
    private RadioGroup radioSex, radioGoal;
    private EditText editAge, editHeight, editWeight;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        radioSex = findViewById(R.id.radioSex);
        radioGoal = findViewById(R.id.radioGoal);
        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String sex = radioSex.getCheckedRadioButtonId() == R.id.radioMale ? "male" : "female";
            int age = Integer.parseInt(editAge.getText().toString());
            int height = Integer.parseInt(editHeight.getText().toString());
            double weight = Double.parseDouble(editWeight.getText().toString());
            String goal;
            int checkedGoal = radioGoal.getCheckedRadioButtonId();
            if (checkedGoal == R.id.radioGain) goal = "gain";
            else if (checkedGoal == R.id.radioLose) goal = "lose";
            else goal = "main";

            Profile profile = new Profile(sex, age, height, weight, goal);
            profile.calorieAdjustment = 0; // Устанавливаем значение по умолчанию
            RecSysDatabase.getInstance(this).profileDao().insert(profile);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}