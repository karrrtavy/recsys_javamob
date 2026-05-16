package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ratings")
public class Rating {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int productId;
    public int value; // 1..5

    public Rating(int productId, int value) {
        this.productId = productId;
        this.value = value;
    }
}