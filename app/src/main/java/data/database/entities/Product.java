package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int categoryId;
    public double proteins;
    public double fats;
    public double carbohydrates;
    public double caloricity;
    public boolean isForWeightGain;
    public boolean isForWeightLoss;
    public boolean isForWeightMain;

    public Product(String name, int categoryId, double proteins, double fats,
                   double carbohydrates, double caloricity,
                   boolean isForWeightGain, boolean isForWeightLoss, boolean isForWeightMain) {
        this.name = name;
        this.categoryId = categoryId;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.caloricity = caloricity;
        this.isForWeightGain = isForWeightGain;
        this.isForWeightLoss = isForWeightLoss;
        this.isForWeightMain = isForWeightMain;
    }
}