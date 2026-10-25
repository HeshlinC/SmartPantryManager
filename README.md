# Smart Pantry Manager

Smart Pantry Manager is an Android app, written in Java, that helps people
waste less food. The user records what is currently in their kitchen, and the
app shows only the recipes they can cook right now, using what they already
have. A recipe is suggested only when every one of its ingredients is in the
pantry in a large enough quantity, so there is never a trip to the shop.

## What the app does
- Lets the user add, edit and delete pantry items (name, quantity, unit and an optional expiry date)
- Comes with a built-in set of recipes loaded on first launch
- Shows a Suggested Recipes screen based on strict ingredient matching
- Shows a recipe detail screen with the full ingredient list and method
- Includes a settings screen

## Database
The app uses SQLite through the SQLiteOpenHelper class. I chose it because the
data is stored on the device, so no internet connection, account or server is
needed. Data is kept after the app is closed, and the structure (pantry items,
recipes and recipe ingredients) suits a small relational database.

## How to run the project
1. Clone the repository: `git clone https://github.com/HeshlinC/SmartPantryManager.git`
2. Open the project folder in Android Studio and wait for Gradle to sync.
3. Run the app on an emulator or a physical device (minimum SDK 23).

## Author
Heshlin Chetty
Student number: 402206133