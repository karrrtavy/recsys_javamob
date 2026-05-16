package data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import data.database.entities.ExcludedProductCrossRef;

@Dao
public interface ExcludedProductDao {
    @Insert
    void insert(ExcludedProductCrossRef crossRef);

    @Query("DELETE FROM excluded_products WHERE productId = :productId")
    void delete(int productId);

    @Query("SELECT EXISTS(SELECT 1 FROM excluded_products WHERE productId = :productId)")
    boolean isExcluded(int productId);
}