package utils;

import android.content.Context;
import data.database.entities.Category;
import data.database.entities.Product;
import data.database.RecSysDatabase;
import data.database.entities.Profile;

import java.util.Arrays;
import java.util.List;

public class DataInitializer {
    public static void initData(Context context) {
        RecSysDatabase db = RecSysDatabase.getInstance(context);
        List<Category> existingCategories = db.categoryDao().getAllSync();
        if (existingCategories.isEmpty()) {
            // Добавляем категории
            List<Category> categories = Arrays.asList(
                    new Category("meat", "Мясо и птица"),
                    new Category("fish", "Рыба"),
                    new Category("eggs", "Яичная продукция"),
                    new Category("milk", "Молочная продукция"),
                    new Category("vegetables", "Овощи"),
                    new Category("fruits", "Фрукты и ягоды"),
                    new Category("fats", "Жиры и масла"),
                    new Category("cereals", "Крупы и злаки"),
                    new Category("baking", "Выпечка")
            );
            for (Category c : categories) db.categoryDao().insert(c);

            // Получаем ID категорий из базы данных
            List<Category> dbCategories = db.categoryDao().getAllSync();
            int meatId = 0, fishId = 0, eggsId = 0, milkId = 0, vegetablesId = 0,
                fruitsId = 0, fatsId = 0, cerealsId = 0, bakingId = 0;
            
            for (Category c : dbCategories) {
                switch (c.name) {
                    case "meat": meatId = c.id; break;
                    case "fish": fishId = c.id; break;
                    case "eggs": eggsId = c.id; break;
                    case "milk": milkId = c.id; break;
                    case "vegetables": vegetablesId = c.id; break;
                    case "fruits": fruitsId = c.id; break;
                    case "fats": fatsId = c.id; break;
                    case "cereals": cerealsId = c.id; break;
                    case "baking": bakingId = c.id; break;
                }
            }

            // Добавляем продукты с правильными ID категорий
            db.productDao().insert(new Product("Куриная грудка запеченная", meatId,
                    30.54, 3.17, 0, 151, true, true, true));
            db.productDao().insert(new Product("Говядина приготовленная", meatId,
                    29.9, 8.37, 0, 203, true, true, true));
            db.productDao().insert(new Product("Семга запеченная", fishId,
                    25.44, 8.13, 0, 182, true, true, true));
            // Продолжаем добавлять продукты с учетом их характеристик
            // Мясо и птица
            db.productDao().insert(new Product("Свинина отварная", meatId,
                    21.4, 23.1, 0, 294, true, false, true));
            db.productDao().insert(new Product("Баранина отварная", meatId,
                    21.4, 17.7, 0, 252, true, false, true));
            db.productDao().insert(new Product("Утка жареная", meatId,
                    17.3, 36.5, 0, 388, true, false, true));
            db.productDao().insert(new Product("Гусь жареный", meatId,
                    16.0, 33.2, 0, 357, true, false, true));
            
            // Рыба
            db.productDao().insert(new Product("Лосось сыровяленый", fishId,
                    19.5, 12.0, 0, 217, true, true, true));
            db.productDao().insert(new Product("Треска отварная", fishId,
                    17.6, 0.3, 0, 78, true, true, true));
            db.productDao().insert(new Product("Окунь жареный", fishId,
                    17.2, 9.7, 0, 160, true, true, true));
            db.productDao().insert(new Product("Кальмары отварные", fishId,
                    18.0, 0.8, 0, 86, true, true, true));
            
            // Яичная продукция
            db.productDao().insert(new Product("Яйцо куриное вареное", eggsId,
                    12.7, 11.5, 0.7, 157, true, true, true));
            db.productDao().insert(new Product("Яйцо перепелиное вареное", eggsId,
                    11.9, 13.0, 0.9, 168, true, true, true));
            
            // Молочная продукция
            db.productDao().insert(new Product("Молоко 2.5%", milkId,
                    3.0, 2.5, 4.7, 54, true, true, true));
            db.productDao().insert(new Product("Кефир 1%", milkId,
                    3.0, 1.0, 4.0, 39, true, true, true));
            db.productDao().insert(new Product("Творог 5%", milkId,
                    18.0, 5.0, 2.0, 158, true, true, true));
            db.productDao().insert(new Product("Сметана 15%", milkId,
                    2.8, 15.0, 2.9, 158, false, false, true));
            db.productDao().insert(new Product("Сыр твердый", milkId,
                    24.8, 34.1, 0.8, 400, false, false, true));
            
            // Овощи
            db.productDao().insert(new Product("Картофель отварной", vegetablesId,
                    2.0, 0.1, 19.7, 87, true, true, true));
            db.productDao().insert(new Product("Морковь отварная", vegetablesId,
                    1.3, 0.1, 8.2, 39, true, true, true));
            db.productDao().insert(new Product("Свекла отварная", vegetablesId,
                    1.5, 0.1, 8.8, 42, true, true, true));
            db.productDao().insert(new Product("Капуста белокочанная", vegetablesId,
                    1.8, 0.1, 5.4, 28, true, true, true));
            db.productDao().insert(new Product("Огурец свежий", vegetablesId,
                    0.8, 0.1, 2.3, 15, true, true, true));
            db.productDao().insert(new Product("Помидор свежий", vegetablesId,
                    0.6, 0.2, 3.8, 20, true, true, true));
            
            // Фрукты и ягоды
            db.productDao().insert(new Product("Яблоко свежее", fruitsId,
                    0.4, 0.4, 9.8, 47, true, true, true));
            db.productDao().insert(new Product("Банан свежий", fruitsId,
                    1.1, 0.3, 21.4, 89, true, false, true));
            db.productDao().insert(new Product("Апельсин свежий", fruitsId,
                    0.9, 0.2, 8.4, 43, true, true, true));
            db.productDao().insert(new Product("Виноград", fruitsId,
                    0.6, 0.4, 16.0, 65, true, false, true));
            
            // Жиры и масла
            db.productDao().insert(new Product("Масло сливочное", fatsId,
                    0.5, 72.5, 0.1, 661, false, false, true));
            db.productDao().insert(new Product("Масло подсолнечное", fatsId,
                    0, 99.8, 0, 899, false, false, true));
            
            // Крупы и злаки
            db.productDao().insert(new Product("Рис вареный", cerealsId,
                    2.7, 0.3, 23.7, 111, true, true, true));
            db.productDao().insert(new Product("Гречка вареная", cerealsId,
                    3.5, 1.2, 21.8, 115, true, true, true));
            db.productDao().insert(new Product("Овсянка вареная", cerealsId,
                    3.8, 1.5, 12.0, 88, true, true, true));
            db.productDao().insert(new Product("Манка вареная", cerealsId,
                    3.0, 1.0, 17.0, 85, true, true, true));
            
            // Выпечка
            db.productDao().insert(new Product("Хлеб пшеничный", bakingId,
                    8.1, 1.0, 49.7, 265, false, false, true));
            db.productDao().insert(new Product("Хлеб ржаной", bakingId,
                    5.8, 1.2, 45.8, 225, true, true, true));
            
            // Добавляем профиль по умолчанию больше не нужно, 
            // так как мы будем запрашивать его при первом входе
        }
    }
}
