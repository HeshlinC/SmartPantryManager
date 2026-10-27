package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    // Returns only the recipes the user can make right now
    public static List<Recipe> getSuggestedRecipes(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> available = buildAvailableMap(pantry);
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, available)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    // Totals up the pantry per ingredient, keyed by normalised name + base unit.
    // Two "Tomato" entries are added together.
    private static Map<String, Double> buildAvailableMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double quantity = toBaseQuantity(item.getQuantity(), item.getUnit());
            Double existing = map.get(key);
            map.put(key, (existing == null ? 0 : existing) + quantity);
        }
        return map;
    }

    // A recipe qualifies only if every ingredient is present in enough quantity
    private static boolean canMake(Recipe recipe, Map<String, Double> available) {
        if (recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            Double have = available.get(makeKey(ingredient.getName(), ingredient.getUnit()));
            double need = toBaseQuantity(ingredient.getQuantity(), ingredient.getUnit());
            if (have == null || have + 0.000001 < need) {
                return false; // missing or not enough: reject the whole recipe
            }
        }
        return true;
    }

    static String makeKey(String name, String unit) {
        return normalizeName(name) + "|" + baseUnit(unit);
    }

    // Lower-cases, trims and turns simple plurals into singular
    // (tomatoes -> tomato, eggs -> egg, berries -> berry)
    static String normalizeName(String name) {
        String n = name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("ies") && n.length() > 3) {
            return n.substring(0, n.length() - 3) + "y";
        }
        if (n.endsWith("oes")) {
            return n.substring(0, n.length() - 2);
        }
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 2) {
            return n.substring(0, n.length() - 1);
        }
        return n;
    }

    // g/kg are both counted in grams, ml/l in millilitres, everything else as-is
    static String baseUnit(String unit) {
        String u = unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("kg") || u.equals("g")) {
            return "g";
        }
        if (u.equals("l") || u.equals("ml")) {
            return "ml";
        }
        return u;
    }

    static double toBaseQuantity(double quantity, String unit) {
        String u = unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("kg") || u.equals("l")) {
            return quantity * 1000;
        }
        return quantity;
    }
}