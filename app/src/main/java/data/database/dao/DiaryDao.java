package data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.lifecycle.LiveData;
import data.database.entities.DiaryEntry;
import data.database.entities.DiaryEntryWithProduct;
import java.util.List;

@Dao
public interface DiaryDao {
    @Insert
    void insert(DiaryEntry entry);

    @Transaction
    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    LiveData<List<DiaryEntryWithProduct>> getAllEntriesWithProduct();

    @Transaction
    @Query("SELECT * FROM diary_entries WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    LiveData<List<DiaryEntryWithProduct>> getEntriesForDayWithProduct(long startOfDay, long endOfDay);

    @Query("DELETE FROM diary_entries WHERE id = :id")
    void deleteById(int id);
}
