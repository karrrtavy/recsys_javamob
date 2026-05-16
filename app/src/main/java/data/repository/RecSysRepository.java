package data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import data.database.RecSysDatabase;
import data.database.dao.*;
import data.database.entities.*;

public class RecSysRepository {
    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ProfileDao profileDao;
    private final RatingDao ratingDao;
    private final ExcludedProductDao excludedProductDao;

    public RecSysRepository(Context context) {
        RecSysDatabase database = RecSysDatabase.getInstance(context);
        this.productDao = database.productDao();
        this.categoryDao = database.categoryDao();
        this.profileDao = database.profileDao();
        this.ratingDao = database.ratingDao();
        this.excludedProductDao = database.excludedProductDao();
    }

    // Product methods
    public LiveData<List<Product>> getProductsForGain() {
        return productDao.getProductsForGain();
    }

    public LiveData<List<Product>> getProductsForLoss() {
        return productDao.getProductsForLoss();
    }

    public LiveData<List<Product>> getProductsForMaintain() {
        return productDao.getProductsForMaintain();
    }

    public LiveData<Product> getProductById(int id) {
        return productDao.getProductById(id);
    }

    public void insertProduct(Product product) {
        new Thread(() -> productDao.insert(product)).start();
    }

    // Category methods
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAll();
    }

    public void insertCategory(Category category) {
        new Thread(() -> categoryDao.insert(category)).start();
    }

    // Profile methods
    public Profile getProfile() {
        return profileDao.getProfile();
    }

    public void insertProfile(Profile profile) {
        new Thread(() -> profileDao.insert(profile)).start();
    }

    public void updateProfile(Profile profile) {
        new Thread(() -> profileDao.update(profile)).start();
    }

    public void deleteAllProfiles() {
        new Thread(() -> profileDao.deleteAll()).start();
    }

    // Rating methods
    public LiveData<Integer> getRatingByProduct(int productId) {
        return ratingDao.getRatingByProduct(productId);
    }

    public void insertRating(Rating rating) {
        new Thread(() -> ratingDao.insert(rating)).start();
    }

    public void updateRating(int productId, int value) {
        new Thread(() -> ratingDao.updateRating(productId, value)).start();
    }

    public Double getAverageRating(int productId) {
        return ratingDao.getAverageRating(productId);
    }

    // Excluded product methods
    public void excludeProduct(int productId) {
        new Thread(() -> {
            ExcludedProductCrossRef crossRef = new ExcludedProductCrossRef(productId);
            excludedProductDao.insert(crossRef);
        }).start();
    }

    public void includeProduct(int productId) {
        new Thread(() -> excludedProductDao.delete(productId)).start();
    }

    public boolean isProductExcluded(int productId) {
        return excludedProductDao.isExcluded(productId);
    }
}