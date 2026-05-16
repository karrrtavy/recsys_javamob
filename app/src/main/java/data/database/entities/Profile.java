package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile")
public class Profile {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String sex; // "male" или "female"
    public int age;
    public int height; // см
    public double weight; // кг
    public String goal; // "gain", "lose", "main"
    public int calorieAdjustment; // ручная коррекция

    public Profile(String sex, int age, int height, double weight, String goal) {
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.calorieAdjustment = 0;
    }
}