package ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recsys.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import data.database.RecSysDatabase;
import data.database.entities.Category;
import data.database.entities.DiaryEntry;
import data.database.entities.ExcludedProductCrossRef;
import data.database.entities.Product;
import data.database.entities.Profile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Category> allCategories = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private MaterialButton btnFilters;
    private ChipGroup chipGroupActiveFilters;

    private boolean fHighProt = false, fLowProt = false;
    private boolean fHighCarb = false, fLowCarb = false;
    private boolean fHighFat = false, fLowFat = false;
    private boolean fHighCal = false, fLowCal = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnFilters = view.findViewById(R.id.btnFilters);
        chipGroupActiveFilters = view.findViewById(R.id.chipGroupActiveFilters);
        btnFilters.setOnClickListener(v -> showFilterDialog());

        loadData();
        return view;
    }

    private void showFilterDialog() {
        String[] options = {
            "Много белков (>15г)", "Мало белков (<5г)",
            "Много углеводов (>30г)", "Мало углеводов (<10г)",
            "Много жиров (>15г)", "Мало жиров (<3г)",
            "Много ккал (>300)", "Мало ккал (<100)"
        };
        boolean[] checked = {fHighProt, fLowProt, fHighCarb, fLowCarb, fHighFat, fLowFat, fHighCal, fLowCal};

        new AlertDialog.Builder(getContext())
            .setTitle("Фильтры рекомендаций")
            .setMultiChoiceItems(options, checked, (dialog, which, isChecked) -> {
                switch (which) {
                    case 0: fHighProt = isChecked; if (isChecked) fLowProt = false; break;
                    case 1: fLowProt = isChecked; if (isChecked) fHighProt = false; break;
                    case 2: fHighCarb = isChecked; if (isChecked) fLowCarb = false; break;
                    case 3: fLowCarb = isChecked; if (isChecked) fHighCarb = false; break;
                    case 4: fHighFat = isChecked; if (isChecked) fLowFat = false; break;
                    case 5: fLowFat = isChecked; if (isChecked) fHighFat = false; break;
                    case 6: fHighCal = isChecked; if (isChecked) fLowCal = false; break;
                    case 7: fLowCal = isChecked; if (isChecked) fHighCal = false; break;
                }
                ((AlertDialog)dialog).getListView().setItemChecked(which, isChecked);
                if (which == 0 && isChecked) { checked[1] = false; ((AlertDialog)dialog).getListView().setItemChecked(1, false); }
                if (which == 1 && isChecked) { checked[0] = false; ((AlertDialog)dialog).getListView().setItemChecked(0, false); }
            })
            .setPositiveButton("Применить", (dialog, which) -> {
                updateActiveFilterChips();
                applyFilters();
            })
            .setNegativeButton("Сбросить", (dialog, which) -> {
                fHighProt = fLowProt = fHighCarb = fLowCarb = fHighFat = fLowFat = fHighCal = fLowCal = false;
                updateActiveFilterChips();
                applyFilters();
            })
            .show();
    }

    private void updateActiveFilterChips() {
        chipGroupActiveFilters.removeAllViews();
        addFilterChip("Белки+", fHighProt);
        addFilterChip("Белки-", fLowProt);
        addFilterChip("Угл+", fHighCarb);
        addFilterChip("Угл-", fLowCarb);
        addFilterChip("Жиры+", fHighFat);
        addFilterChip("Жиры-", fLowFat);
        addFilterChip("Ккал+", fHighCal);
        addFilterChip("Ккал-", fLowCal);
    }

    private void addFilterChip(String text, boolean isActive) {
        if (!isActive) return;
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chipGroupActiveFilters.addView(chip);
    }

    private void loadData() {
        RecSysDatabase db = RecSysDatabase.getInstance(getContext());
        db.categoryDao().getAll().observe(getViewLifecycleOwner(), categories -> {
            allCategories = categories;
            checkAndSetup();
        });

        new Thread(() -> {
            Profile profile = db.profileDao().getProfile();
            if (profile != null && getActivity() != null) {
                LiveData<List<Product>> productsLiveData;
                switch (profile.goal) {
                    case "gain": productsLiveData = db.productDao().getProductsForGain(); break;
                    case "lose": productsLiveData = db.productDao().getProductsForLoss(); break;
                    default: productsLiveData = db.productDao().getProductsForMaintain(); break;
                }
                getActivity().runOnUiThread(() -> {
                    productsLiveData.observe(getViewLifecycleOwner(), products -> {
                        allProducts = products;
                        checkAndSetup();
                    });
                });
            }
        }).start();
    }

    private void checkAndSetup() {
        if (allCategories != null && allProducts != null) {
            applyFilters();
        }
    }

    private void applyFilters() {
        List<Product> filtered = new ArrayList<>(allProducts);
        
        Profile profile = RecSysDatabase.getInstance(getContext()).profileDao().getProfile();
        if (profile != null) {
            filtered.sort((p1, p2) -> {
                if (profile.goal.equals("gain")) return Double.compare(p2.caloricity, p1.caloricity);
                else return Double.compare(p1.caloricity, p2.caloricity);
            });
        }

        if (fHighProt) filtered = filtered.stream().filter(p -> p.proteins > 15).collect(Collectors.toList());
        if (fLowProt) filtered = filtered.stream().filter(p -> p.proteins < 5).collect(Collectors.toList());
        if (fHighCarb) filtered = filtered.stream().filter(p -> p.carbohydrates > 30).collect(Collectors.toList());
        if (fLowCarb) filtered = filtered.stream().filter(p -> p.carbohydrates < 10).collect(Collectors.toList());
        if (fHighFat) filtered = filtered.stream().filter(p -> p.fats > 15).collect(Collectors.toList());
        if (fLowFat) filtered = filtered.stream().filter(p -> p.fats < 3).collect(Collectors.toList());
        if (fHighCal) filtered = filtered.stream().filter(p -> p.caloricity > 300).collect(Collectors.toList());
        if (fLowCal) filtered = filtered.stream().filter(p -> p.caloricity < 100).collect(Collectors.toList());

        if (filtered.size() > 15) filtered = filtered.subList(0, 15);

        Map<Integer, List<Product>> grouped = new HashMap<>();
        for (Product p : filtered) {
            grouped.computeIfAbsent(p.categoryId, k -> new ArrayList<>()).add(p);
        }

        recyclerView.setAdapter(new CategoryAdapter(allCategories, grouped, getContext()));
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<Category> categories;
        private Map<Integer, List<Product>> productsMap;
        private Context context;
        private Map<Integer, Boolean> expandedState = new HashMap<>();

        CategoryAdapter(List<Category> categories, Map<Integer, List<Product>> productsMap, Context context) {
            this.categories = categories.stream()
                .filter(c -> productsMap.containsKey(c.id))
                .collect(Collectors.toList());
            this.productsMap = productsMap;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category cat = categories.get(position);
            holder.name.setText(cat.ruName);
            List<Product> catProducts = productsMap.get(cat.id);
            holder.recycler.setLayoutManager(new LinearLayoutManager(context));
            holder.recycler.setAdapter(new ProductAdapter(catProducts, context));
            boolean isExpanded = expandedState.getOrDefault(cat.id, true);
            holder.recycler.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.name.setOnClickListener(v -> {
                boolean newState = !expandedState.getOrDefault(cat.id, true);
                expandedState.put(cat.id, newState);
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() { return categories.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            RecyclerView recycler;
            ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.categoryName);
                recycler = v.findViewById(R.id.recyclerProducts);
            }
        }
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private List<Product> products;
        private Context context;
        ProductAdapter(List<Product> products, Context context) {
            this.products = products;
            this.context = context;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int pos) {
            Product p = products.get(pos);
            holder.textName.setText(p.name);
            holder.textCalories.setText(String.format("%.0f ккал", p.caloricity));
            holder.textProteins.setText(String.format("Б: %.1fг", p.proteins));
            holder.textFats.setText(String.format("Ж: %.1fг", p.fats));
            holder.textCarbs.setText(String.format("У: %.1fг", p.carbohydrates));

            holder.buttonExclude.setVisibility(View.VISIBLE);
            holder.buttonExclude.setOnClickListener(v -> {
                new Thread(() -> {
                    if (!RecSysDatabase.getInstance(context).excludedProductDao().isExcluded(p.id)) {
                        RecSysDatabase.getInstance(context).excludedProductDao().insert(new ExcludedProductCrossRef(p.id));
                    } else {
                        RecSysDatabase.getInstance(context).excludedProductDao().delete(p.id);
                    }
                }).start();
            });
            
            if (RecSysDatabase.getInstance(context).excludedProductDao().isExcluded(p.id)) {
                holder.buttonExclude.setText("Включить");
            } else {
                holder.buttonExclude.setText("Исключить");
            }

            holder.btnAddDiary.setOnClickListener(v -> showAddDiaryDialog(p));
        }
        private void showAddDiaryDialog(Product p) {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_diary, null);
            TextView textName = view.findViewById(R.id.textDialogProductName);
            TextView textInfo = view.findViewById(R.id.textDialogProductInfo);
            EditText editGrams = view.findViewById(R.id.editGrams);

            textName.setText(p.name);
            textInfo.setText(String.format("%.0f ккал | Б:%.1f Ж:%.1f У:%.1f (на 100г)", 
                p.caloricity, p.proteins, p.fats, p.carbohydrates));

            new AlertDialog.Builder(context)
                .setTitle("Добавить в дневник")
                .setView(view)
                .setPositiveButton("Добавить", (d, w) -> {
                    String val = editGrams.getText().toString();
                    if (!val.isEmpty()) {
                        new Thread(() -> RecSysDatabase.getInstance(context).diaryDao().insert(
                            new DiaryEntry(p.id, Double.parseDouble(val), System.currentTimeMillis())
                        )).start();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
        }
        @Override
        public int getItemCount() { return products.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textCalories, textProteins, textFats, textCarbs;
            Button buttonExclude, btnAddDiary;
            RatingBar ratingBar;
            ViewHolder(View v) {
                super(v);
                textName = v.findViewById(R.id.productName);
                textCalories = v.findViewById(R.id.productCalories);
                textProteins = v.findViewById(R.id.productProteins);
                textFats = v.findViewById(R.id.productFats);
                textCarbs = v.findViewById(R.id.productCarbs);
                buttonExclude = v.findViewById(R.id.buttonExclude);
                btnAddDiary = v.findViewById(R.id.btnAddDiary);
                ratingBar = v.findViewById(R.id.ratingBar);
            }
        }
    }
}
