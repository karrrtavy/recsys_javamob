package model;

import data.database.entities.Profile;

public class NutritionCalculator {
    public static int calculateDailyCalories(Profile profile) {
        double bmr;
        if (profile.sex.equals("male")) {
            bmr = 10 * profile.weight + 6.25 * profile.height - 5 * profile.age + 5;
        } else {
            bmr = 10 * profile.weight + 6.25 * profile.height - 5 * profile.age - 161;
        }
        int adjustment;
        switch (profile.goal) {
            case "gain": adjustment = 500; break;
            case "lose": adjustment = -500; break;
            default: adjustment = 0;
        }
        return (int) (bmr + adjustment + profile.calorieAdjustment);
    }

    public static Macros calculateDailyMacros(Profile profile) {
        double proteins, fats, carbohydrates;
        switch (profile.goal) {
            case "gain":
                proteins = profile.weight * 2.0;
                fats = profile.weight * 1.0;
                carbohydrates = profile.weight * 4.0;
                break;
            case "lose":
                proteins = profile.weight * 2.5;
                fats = profile.weight * 0.8;
                carbohydrates = profile.weight * 2.0;
                break;
            default:
                proteins = profile.weight * 1.8;
                fats = profile.weight * 0.9;
                carbohydrates = profile.weight * 3.0;
        }
        return new Macros(proteins, fats, carbohydrates, calculateDailyCalories(profile));
    }

    public static class Macros {
        public double proteins;
        public double fats;
        public double carbohydrates;
        public int calories;
        public Macros(double p, double f, double c, int cal) {
            proteins = p; fats = f; carbohydrates = c; calories = cal;
        }
    }
}