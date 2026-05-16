package data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import data.database.entities.Rating;
import androidx.lifecycle.LiveData;

@Dao
public interface RatingDao {
    @Insert
    void insert(Rating rating);

    @Query("SELECT value FROM ratings WHERE productId = :productId LIMIT 1")
    LiveData<Integer> getRatingByProduct(int productId);

    @Query("UPDATE ratings SET value = :value WHERE productId = :productId")
    void updateRating(int productId, int value);

    @Query("SELECT AVG(value) FROM ratings WHERE productId = :productId")
    Double getAverageRating(int productId);
}