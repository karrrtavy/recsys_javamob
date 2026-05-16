package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recsys.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import data.database.RecSysDatabase;
import data.database.entities.DiaryEntryWithProduct;
import data.database.entities.Profile;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {
    private TextView textTotalCalories, textTotalMacros, textProgressPercent;
    private CircularProgressIndicator calorieProgress;
    private RecyclerView recyclerDiary;
    private DiaryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        textTotalCalories = view.findViewById(R.id.textTotalCalories);
        textTotalMacros = view.findViewById(R.id.textTotalMacros);
        textProgressPercent = view.findViewById(R.id.textProgressPercent);
        calorieProgress = view.findViewById(R.id.calorieProgress);
        View btnDetailedStats = view.findViewById(R.id.btnDetailedStats);
        
        recyclerDiary = view.findViewById(R.id.recyclerDiary);
        recyclerDiary.setLayoutManager(new LinearLayoutManager(getContext()));

        btnDetailedStats.setOnClickListener(v -> showStatisticsDialog());

        loadDiary();
        return view;
    }

    private void loadDiary() {
        if (getContext() == null) return;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long endOfDay = cal.getTimeInMillis();

        RecSysDatabase.getInstance(getContext()).diaryDao().getEntriesForDayWithProduct(startOfDay, endOfDay)
                .observe(getViewLifecycleOwner(), entries -> {
                    updateTotals(entries);
                    adapter = new DiaryAdapter(entries);
                    recyclerDiary.setAdapter(adapter);
                });
    }

    private void updateTotals(List<DiaryEntryWithProduct> entries) {
        if (getContext() == null) return;

        double totalCal = 0, totalP = 0, totalF = 0, totalC = 0;
        for (DiaryEntryWithProduct item : entries) {
            if (item.product != null) {
                double factor = item.entry.grams / 100.0;
                totalCal += item.product.caloricity * factor;
                totalP += item.product.proteins * factor;
                totalF += item.product.fats * factor;
                totalC += item.product.carbohydrates * factor;
            }
        }
        
        Profile profile = RecSysDatabase.getInstance(getContext()).profileDao().getProfile();
        double targetKcal = 2000; // default
        if (profile != null) {
            double bmr;
            if ("male".equals(profile.sex)) {
                bmr = 88.36 + (13.4 * profile.weight) + (4.8 * profile.height) - (5.7 * profile.age);
            } else {
                bmr = 447.59 + (9.2 * profile.weight) + (3.1 * profile.height) - (4.3 * profile.age);
            }
            targetKcal = bmr * 1.2;
            if ("lose".equals(profile.goal)) targetKcal -= 500;
            else if ("gain".equals(profile.goal)) targetKcal += 500;
        }

        textTotalCalories.setText(String.format(Locale.getDefault(), "%.0f / %.0f ккал", totalCal, targetKcal));
        textTotalMacros.setText(String.format(Locale.getDefault(), "Б: %.1fг | Ж: %.1fг | У: %.1fг", totalP, totalF, totalC));
        
        int percent = (targetKcal > 0) ? (int) ((totalCal / targetKcal) * 100) : 0;
        calorieProgress.setProgress(Math.min(percent, 100));
        textProgressPercent.setText(String.format(Locale.getDefault(), "%d%%", percent));
        
        // Цвет индикатора в зависимости от прогресса
        if (percent > 100) {
            calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
        } else if (percent > 90) {
            calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.accent));
        } else {
            calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.primary));
        }
    }

    private void showStatisticsDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
            .setTitle("Статистика рациона")
            .setMessage("Ваш прогресс за сегодня отображается на главном круговом индикаторе. \n\n" +
                        "Зеленый: в пределах нормы.\n" +
                        "Желтый: вы близки к пределу.\n" +
                        "Красный: превышение нормы.")
            .setPositiveButton("Понятно", null)
            .show();
    }

    class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
        private List<DiaryEntryWithProduct> entries;
        DiaryAdapter(List<DiaryEntryWithProduct> entries) { this.entries = entries; }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DiaryEntryWithProduct item = entries.get(position);
            if (item.product != null) {
                holder.name.setText(item.product.name);
                double factor = item.entry.grams / 100.0;
                double cal = item.product.caloricity * factor;
                holder.details.setText(String.format(Locale.getDefault(), "%.0fг - %.0f ккал", item.entry.grams, cal));
            }
            holder.btnDelete.setOnClickListener(v -> {
                new Thread(() -> {
                    if (getContext() != null) {
                        RecSysDatabase.getInstance(getContext()).diaryDao().deleteById(item.entry.id);
                    }
                }).start();
            });
        }
        @Override
        public int getItemCount() { return entries.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, details;
            View btnDelete;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.textProductName);
                details = v.findViewById(R.id.textEntryDetails);
                btnDelete = v.findViewById(R.id.btnDeleteEntry);
            }
        }
    }
}
