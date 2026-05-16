package ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recsys.R;
import data.database.RecSysDatabase;
import data.database.entities.Profile;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editAge, editWeight, editHeight;
    private RadioGroup radioGender, radioGoal;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        
        // Инициализация views
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        radioGender = findViewById(R.id.radioGender);
        radioGoal = findViewById(R.id.radioGoal);
        btnSave = findViewById(R.id.btnSave);
        
        // Скрываем группу активности, так как она не используется в Profile
        findViewById(R.id.radioActivity).setVisibility(android.view.View.GONE);
        findViewById(R.id.textActivity).setVisibility(android.view.View.GONE);
        
        // Настройка отступов (Safe Area)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_scroll), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Загрузка существующего профиля
        loadProfile();
        
        // Обработка нажатия на кнопку сохранения
        btnSave.setOnClickListener(v -> saveProfile());
    }

    @Override
    public void onBackPressed() {
        new Thread(() -> {
            Profile profile = RecSysDatabase.getInstance(this).profileDao().getProfile();
            runOnUiThread(() -> {
                if (profile == null) {
                    // Если профиля нет, не даем выйти из активности
                    android.widget.Toast.makeText(this, "Пожалуйста, заполните профиль", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    super.onBackPressed();
                }
            });
        }).start();
    }
    
    private void loadProfile() {
        Profile profile = RecSysDatabase.getInstance(this).profileDao().getProfile();
        if (profile != null) {
            editAge.setText(String.valueOf(profile.age));
            editWeight.setText(String.valueOf(profile.weight));
            editHeight.setText(String.valueOf(profile.height));
            
            // Установка пола
            if (profile.sex.equals("male")) {
                radioGender.check(R.id.radioMale);
            } else {
                radioGender.check(R.id.radioFemale);
            }
            
            // Установка цели
            if (profile.goal.equals("gain")) {
                radioGoal.check(R.id.radioGain);
            } else if (profile.goal.equals("lose")) {
                radioGoal.check(R.id.radioLose);
            } else {
                radioGoal.check(R.id.radioMaintain);
            }
        }
    }
    
    private void saveProfile() {
        // Получение значений из полей
        int age = Integer.parseInt(editAge.getText().toString());
        double weight = Double.parseDouble(editWeight.getText().toString());
        double height = Double.parseDouble(editHeight.getText().toString());
        
        // Получение пола
        String gender;
        if (radioGender.getCheckedRadioButtonId() == R.id.radioMale) {
            gender = "male";
        } else {
            gender = "female";
        }
        
        // Получение цели
        String goal;
        int goalId = radioGoal.getCheckedRadioButtonId();
        if (goalId == R.id.radioGain) {
            goal = "gain";
        } else if (goalId == R.id.radioLose) {
            goal = "lose";
        } else {
            goal = "main"; // Profile использует "main" вместо "maintain"
        }
        
        // Создание и сохранение профиля
        Profile profile = new Profile(gender, age, (int)height, weight, goal);
        profile.calorieAdjustment = 0; // Устанавливаем значение по умолчанию
        
        RecSysDatabase.getInstance(this).profileDao().deleteAll();
        RecSysDatabase.getInstance(this).profileDao().insert(profile);
        
        // Закрытие активности
        finish();
    }
}