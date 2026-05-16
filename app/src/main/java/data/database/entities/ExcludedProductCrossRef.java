package data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "excluded_products")
public class ExcludedProductCrossRef {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int productId;

    public ExcludedProductCrossRef(int productId) {
        this.productId = productId;
    }
}