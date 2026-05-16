package data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import data.database.entities.Profile;

@Dao
public interface ProfileDao {
    @Insert
    void insert(Profile profile);

    @Update
    void update(Profile profile);

    @Query("SELECT * FROM profile LIMIT 1")
    Profile getProfile();

    @Query("DELETE FROM profile")
    void deleteAll();
}