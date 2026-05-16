package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_entries")
public class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int productId;
    public double grams;
    public long timestamp;

    public DiaryEntry(int productId, double grams, long timestamp) {
        this.productId = productId;
        this.grams = grams;
        this.timestamp = timestamp;
    }
}
