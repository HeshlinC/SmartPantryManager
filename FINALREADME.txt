# Smart Pantry Manager

Smart Pantry Manager is an Android app, whicxh is also written in Java, it helps people
waste less food. The user records what is in their kitchen, and the app shows
only the recipes which they can cook currently using what they already have. A
recipe will only be suggested when every one of its ingredients is in the pantry
in a large enough quantity, so there is never a trip to the shop.

# Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Input validation, including a check that expiry dates are real dates
- 24 pre-loaded recipes, seeded into the database on first run
- Suggested Recipes screen using strict matching (every ingredient, enough quantity)
- Matching copes with plurals ("tomatoes" vs "tomato") and unit differences (kg vs g, l vs ml)
- Friendly message when no recipes match the pantry
- Recipe detail screen with the full ingredient list and method
- Settings screen with an expiring-soon highlight toggle (saved with SharedPreferences)
- Bottom navigation bar

# Database
SQLite through SQLiteOpenHelper. It is stored on the device, so no internet
connection, account or server is needed. Data stays after the app is closed.
There are three tables: `pantry`, `recipes` and `recipe_ingredients`
(linked to `recipes` by `recipe_id`).

# Project structure
- `DatabaseHelper`: creates the tables, seeds the recipes and handles all CRUD
- `RecipeMatcher`: the strict-matching logic (unit tested in `RecipeMatcherTest`)
- `MainActivity`, `AddEditItemActivity`, `SuggestedRecipesActivity`,
  `RecipeDetailActivity`, `SettingsActivity`: the five screens
- `PantryAdapter`, `RecipeAdapter`: RecyclerView adapters

# How to run the project
1. Clone the repository: `git clone https://github.com/HeshlinC/SmartPantryManager.git`
2. Open the project folder in Android Studio and wait for Gradle to sync.
3. Run the app on an emulator or a physical device (minimum SDK 23).
4. To run the unit tests, right-click `RecipeMatcherTest` and choose Run.

## Author
Heshlin Chetty
Student number: 402206133