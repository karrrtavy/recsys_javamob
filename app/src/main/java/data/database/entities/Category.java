package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String ruName;

    public Category(String name, String ruName) {
        this.name = name;
        this.ruName = ruName;
    }
}