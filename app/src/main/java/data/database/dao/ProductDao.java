package data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import data.database.entities.Product;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Query("SELECT * FROM products WHERE isForWeightGain = 1 AND id NOT IN " +
            "(SELECT productId FROM excluded_products)")
    LiveData<List<Product>> getProductsForGain();

    @Query("SELECT * FROM products WHERE isForWeightLoss = 1 AND id NOT IN " +
            "(SELECT productId FROM excluded_products)")
    LiveData<List<Product>> getProductsForLoss();

    @Query("SELECT * FROM products WHERE isForWeightMain = 1 AND id NOT IN " +
            "(SELECT productId FROM excluded_products)")
    LiveData<List<Product>> getProductsForMaintain();

    @Query("SELECT * FROM products WHERE id = :id")
    LiveData<Product> getProductById(int id);
}