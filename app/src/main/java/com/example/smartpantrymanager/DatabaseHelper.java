package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 3;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe tables
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_STEPS = "steps";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RECIPE_ID = "recipe_id";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT)");
        createRecipeTables(db);
        seedRecipes(db);
        seedExtraRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 only had the pantry table. Keep the user's pantry
        // data and just add the recipe tables.
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
        // Version 3 added four more recipes
        if (oldVersion < 3) {
            seedExtraRecipes(db);
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_STEPS + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL, "
                + COL_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_ID + "))");
    }

    // Inserts one recipe plus its ingredients. Each ingredient is written
    // as "name|quantity|unit", e.g. "cheese|50|g"
    private void insertRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_NAME, name);
        recipeValues.put(COL_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (String line : ingredients) {
            String[] parts = line.split("\\|");
            ContentValues iv = new ContentValues();
            iv.put(COL_RECIPE_ID, recipeId);
            iv.put(COL_NAME, parts[0]);
            iv.put(COL_QUANTITY, Double.parseDouble(parts[1]));
            iv.put(COL_UNIT, parts[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }

    // Pre-loads the original recipe collection the first time the database is created
    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Cheese Toastie",
                "1. Butter both slices of bread.\n2. Put the cheese between them.\n3. Toast in a pan until golden on both sides.",
                "bread|2|pcs", "cheese|50|g", "butter|10|g");

        insertRecipe(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Melt the butter in a pan and add the beaten eggs.\n3. Stir gently until just set, then serve on the toast.",
                "egg|2|pcs", "bread|2|pcs", "butter|10|g");

        insertRecipe(db, "Tomato Omelette",
                "1. Chop the tomato.\n2. Melt the butter and pour in the beaten eggs.\n3. Add the tomato, fold the omelette and cook for a minute.",
                "egg|3|pcs", "tomato|1|pcs", "butter|10|g");

        insertRecipe(db, "Cheesy Scrambled Eggs",
                "1. Beat the eggs.\n2. Scramble them in melted butter.\n3. Stir in the grated cheese just before they set.",
                "egg|3|pcs", "cheese|40|g", "butter|10|g");

        insertRecipe(db, "French Toast",
                "1. Whisk the eggs and milk together.\n2. Soak each slice of bread.\n3. Fry in butter until golden on both sides.",
                "bread|4|pcs", "egg|2|pcs", "milk|100|ml", "butter|10|g");

        insertRecipe(db, "Pancakes",
                "1. Mix the flour, sugar, eggs and milk into a smooth batter.\n2. Pour a ladle into a hot pan.\n3. Flip when bubbles form and cook until golden.",
                "flour|200|g", "milk|300|ml", "egg|2|pcs", "sugar|20|g");

        insertRecipe(db, "Banana Pancakes",
                "1. Mash the bananas.\n2. Mix with the eggs and flour.\n3. Fry small spoonfuls until golden on both sides.",
                "banana|2|pcs", "egg|2|pcs", "flour|50|g");

        insertRecipe(db, "Banana Oat Porridge",
                "1. Heat the oats and milk in a pot, stirring.\n2. Cook for 5 minutes until thick.\n3. Top with sliced banana.",
                "oat|80|g", "milk|300|ml", "banana|1|pcs");

        insertRecipe(db, "Rice Pudding",
                "1. Put the rice, milk and sugar in a pot.\n2. Simmer on low heat for 30 minutes, stirring often.\n3. Serve warm once creamy.",
                "rice|100|g", "milk|500|ml", "sugar|40|g");

        insertRecipe(db, "Egg Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the chopped onion in oil.\n3. Add the rice, push aside and scramble the eggs in the pan, then mix together.",
                "rice|200|g", "egg|2|pcs", "onion|1|pcs", "oil|15|ml");

        insertRecipe(db, "Chicken and Rice",
                "1. Fry the chopped onion in oil.\n2. Add the chicken and brown it.\n3. Add the rice and enough water, cover and simmer until cooked.",
                "chicken|300|g", "rice|200|g", "onion|1|pcs", "oil|15|ml");

        insertRecipe(db, "Chicken Stir Fry",
                "1. Slice the chicken, carrots and onion.\n2. Fry the chicken in hot oil until cooked.\n3. Add the vegetables and stir fry for 5 minutes.",
                "chicken|300|g", "carrot|2|pcs", "onion|1|pcs", "oil|15|ml");

        insertRecipe(db, "Tomato Pasta",
                "1. Boil the pasta.\n2. Fry the onion and garlic in oil, then add the chopped tomatoes and simmer.\n3. Mix the sauce through the pasta.",
                "pasta|200|g", "tomato|3|pcs", "onion|1|pcs", "garlic|2|pcs", "oil|15|ml");

        insertRecipe(db, "Garlic Butter Pasta",
                "1. Boil the pasta.\n2. Melt the butter and gently fry the crushed garlic.\n3. Toss the pasta in the garlic butter.",
                "pasta|200|g", "garlic|3|pcs", "butter|30|g");

        insertRecipe(db, "Mince Pasta",
                "1. Boil the pasta.\n2. Brown the mince with the chopped onion.\n3. Add the chopped tomatoes, simmer, and serve over the pasta.",
                "pasta|200|g", "mince|250|g", "tomato|2|pcs", "onion|1|pcs");

        insertRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with the butter and milk.\n3. Serve hot.",
                "potato|4|pcs", "butter|30|g", "milk|50|ml");

        insertRecipe(db, "Roast Potatoes",
                "1. Heat the oven to 200 C.\n2. Cut the potatoes and toss in the oil.\n3. Roast for 40 minutes, turning once.",
                "potato|5|pcs", "oil|30|ml");

        insertRecipe(db, "Cheesy Potato Bake",
                "1. Slice the potatoes thinly and layer in a dish.\n2. Pour over the milk and dot with butter.\n3. Top with cheese and bake at 180 C for 45 minutes.",
                "potato|4|pcs", "cheese|100|g", "milk|100|ml", "butter|20|g");

        insertRecipe(db, "Tomato Soup",
                "1. Fry the chopped onion in butter.\n2. Add the chopped tomatoes and simmer for 20 minutes.\n3. Blend until smooth.",
                "tomato|6|pcs", "onion|1|pcs", "butter|20|g");

        insertRecipe(db, "Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Fry the onion in oil, then add the rest with water.\n3. Simmer for 25 minutes until soft.",
                "carrot|2|pcs", "potato|2|pcs", "onion|1|pcs", "oil|15|ml");
    }

    // The four recipes added in database version 3
    private void seedExtraRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Lasagna",
                "1. Fry the chopped onion, add the mince and brown it, then add the chopped tomatoes and simmer for 20 minutes.\n"
                        + "2. Melt the butter, stir in the flour, then slowly whisk in the milk until you have a thick white sauce.\n"
                        + "3. Layer the lasagna sheets, meat sauce and white sauce in a dish, finishing with white sauce.\n"
                        + "4. Top with the grated cheese and bake at 180 C for 40 minutes until golden.",
                "lasagna sheet|9|pcs", "mince|500|g", "tomato|4|pcs", "onion|1|pcs",
                "cheese|150|g", "milk|500|ml", "flour|40|g", "butter|40|g");

        insertRecipe(db, "Pizza",
                "1. Heat the oven to 220 C.\n2. Crush the tomatoes with a little oil and spread over the pizza base.\n3. Cover with the grated cheese.\n4. Bake for 12 to 15 minutes until the cheese is bubbling and golden.",
                "pizza base|1|pcs", "tomato|2|pcs", "cheese|150|g", "oil|10|ml");

        insertRecipe(db, "Mutton Curry",
                "1. Heat the oil and fry the chopped onion until soft.\n2. Add the crushed garlic, ginger and curry powder and fry for 2 minutes.\n3. Add the mutton and brown it on all sides.\n4. Add the chopped tomatoes, potatoes and a cup of water.\n5. Cover and simmer for 1 hour until the meat is tender.",
                "mutton|500|g", "onion|2|pcs", "tomato|2|pcs", "garlic|3|pcs",
                "ginger|20|g", "curry powder|30|g", "oil|30|ml", "potato|2|pcs");

        insertRecipe(db, "Chops and Chutney",
                "1. Rub the lamb chops with crushed garlic.\n2. Heat the oil in a pan on high heat.\n3. Fry the chops for 4 minutes on each side.\n4. Brush with the chutney and cook for another minute until sticky and glazed.",
                "lamb chop|4|pcs", "chutney|100|g", "garlic|2|pcs", "oil|15|ml");
    }

    // ---------- PANTRY CRUD ----------

    // CREATE
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    // READ (all items)
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " ASC");
        while (c.moveToNext()) {
            items.add(new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                    c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                    c.getString(c.getColumnIndexOrThrow(COL_EXPIRY))));
        }
        c.close();
        return items;
    }

    // READ (one item)
    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (c.moveToFirst()) {
            item = new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                    c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                    c.getString(c.getColumnIndexOrThrow(COL_EXPIRY)));
        }
        c.close();
        return item;
    }

    // UPDATE
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
    }
    // DELETE ALL: removes every pantry item (recipes are not affected)
    public int clearPantry() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, null, null);
    }
    // DELETE
    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // ---------- RECIPE READS ----------

    // Returns every recipe together with its list of ingredients
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null, COL_NAME + " ASC");
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
            Recipe recipe = new Recipe(
                    id,
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_STEPS)));
            recipe.setIngredients(getIngredientsForRecipe(db, id));
            recipes.add(recipe);
        }
        c.close();
        return recipes;
    }

    // Returns one recipe (with ingredients), or null if it does not exist
    public Recipe getRecipe(long recipeId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, COL_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        Recipe recipe = null;
        if (c.moveToFirst()) {
            recipe = new Recipe(
                    recipeId,
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_STEPS)));
            recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
        }
        c.close();
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            list.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                    c.getString(c.getColumnIndexOrThrow(COL_UNIT))));
        }
        c.close();
        return list;
    }
}