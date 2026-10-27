package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcherTest {

    private List<Recipe> toastieList() {
        Recipe recipe = new Recipe(1, "Cheese Toastie", "Toast it.");
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("bread", 2, "pcs"));
        ingredients.add(new RecipeIngredient("cheese", 50, "g"));
        ingredients.add(new RecipeIngredient("butter", 10, "g"));
        recipe.setIngredients(ingredients);
        List<Recipe> list = new ArrayList<>();
        list.add(recipe);
        return list;
    }

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Bread", 4, "pcs", null));
        pantry.add(new PantryItem(2, "Cheese", 100, "g", null));
        pantry.add(new PantryItem(3, "Butter", 250, "g", null));
        assertEquals(1, RecipeMatcher.getSuggestedRecipes(toastieList(), pantry).size());
    }

    @Test
    public void oneIngredientMissing_recipeIsNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Bread", 4, "pcs", null));
        pantry.add(new PantryItem(2, "Cheese", 100, "g", null));
        // no butter
        assertTrue(RecipeMatcher.getSuggestedRecipes(toastieList(), pantry).isEmpty());
    }

    @Test
    public void notEnoughQuantity_recipeIsNotSuggested() {
        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Bread", 1, "pcs", null)); // needs 2
        pantry.add(new PantryItem(2, "Cheese", 100, "g", null));
        pantry.add(new PantryItem(3, "Butter", 250, "g", null));
        assertTrue(RecipeMatcher.getSuggestedRecipes(toastieList(), pantry).isEmpty());
    }

    @Test
    public void pluralAndCaseDifferences_stillMatch() {
        Recipe recipe = new Recipe(2, "Tomato Omelette", "Cook it.");
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("tomato", 1, "pcs"));
        ingredients.add(new RecipeIngredient("egg", 2, "pcs"));
        recipe.setIngredients(ingredients);
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "TOMATOES", 3, "pcs", null));
        pantry.add(new PantryItem(2, "Eggs", 6, "pcs", null));
        assertEquals(1, RecipeMatcher.getSuggestedRecipes(recipes, pantry).size());
    }

    @Test
    public void unitDifferences_kgCoversGrams() {
        Recipe recipe = new Recipe(3, "Pancakes", "Cook it.");
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient("flour", 200, "g"));
        recipe.setIngredients(ingredients);
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem(1, "Flour", 1, "kg", null)); // 1000 g
        assertEquals(1, RecipeMatcher.getSuggestedRecipes(recipes, pantry).size());
    }
}