package ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.recsys.R;
import ui.fragments.CatalogFragment;
import ui.fragments.DiaryFragment;
import ui.fragments.ProfileFragment;
import ui.fragments.RecommendationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Настройка отступов для системных панелей (шторка и навигация)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_catalog) selected = new CatalogFragment();
            else if (id == R.id.nav_recommendations) selected = new RecommendationsFragment();
            else if (id == R.id.nav_diary) selected = new DiaryFragment();
            else if (id == R.id.nav_profile) selected = new ProfileFragment();
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
            return true;
        });
        if (savedInstanceState == null) {
            checkProfile();
        }
    }

    private void checkProfile() {
        new Thread(() -> {
            data.database.entities.Profile profile = data.database.RecSysDatabase.getInstance(this).profileDao().getProfile();
            runOnUiThread(() -> {
                if (profile == null) {
                    android.content.Intent intent = new android.content.Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    bottomNav.setSelectedItemId(R.id.nav_catalog);
                }
            });
        }).start();
    }
}