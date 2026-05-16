package data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.*;
import data.database.dao.*;
import data.database.entities.*;

@Database(entities = {Category.class, Product.class, Profile.class,
        Rating.class, ExcludedProductCrossRef.class, DiaryEntry.class}, version = 2)
public abstract class RecSysDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract ProfileDao profileDao();
    public abstract RatingDao ratingDao();
    public abstract ExcludedProductDao excludedProductDao();
    public abstract DiaryDao diaryDao();

    private static volatile RecSysDatabase INSTANCE;

    public static RecSysDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RecSysDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RecSysDatabase.class, "recsys.db")
                            .allowMainThreadQueries() // для простоты, в реальном проекте используйте фон
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}