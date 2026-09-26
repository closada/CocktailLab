# CocktailLab

## Demo video
[Watch the demo video on Google Drive](https://drive.google.com/file/d/1tR3kzEp_BmNWL_qXa3RTQGzpbxugpfNq/view?usp=sharing)

## Overview
CocktailLab lets the user browse cocktails by category or by ingredient, using the official option lists provided by TheCocktailDB, and view full preparation details for any drink.

## Architecture
The app follows the MVVM pattern with a clear separation of responsibilities:

- **model** — data classes matching the API responses (`DrinkSummary`, `DrinkDetail`, `CategoryItem`, `IngredientItem`).
- **service** — `CocktailService`, a Retrofit interface describing the API endpoints.
- **repository** — `CocktailRepository`, the single class that knows Retrofit exists. Receives the service through the constructor (manual Dependency Injection).
- **viewmodel** — `CocktailViewModel` (an `AndroidViewModel`) exposes UI state through `LiveData` and talks only to the repository.
- **view** — `MainActivity` (list + filters), `DetailActivity` + `DetailFragment` (drink detail).
- **util** — `NetworkUtils`, checks connectivity before hitting the API.

## Screens
- **Main screen**: two dropdown menus (Category / Ingredient), populated from `list.php`. Selecting a value triggers a search with `filter.php` and lists results in a `RecyclerView` (thumbnail + name).
- **Detail screen** (Fragment, hosted in a separate Activity): high‑resolution photo, category, glass, full ingredient list with quantities, and numbered preparation steps. Includes a back button.

## Requirements covered
- MVVM pattern.
- 2 Activities (`MainActivity`, `DetailActivity`) + 1 Fragment (`DetailFragment`), with correct fragment lifecycle handling (`onDestroyView` clears the binding).
- `RecyclerView` with a custom `Adapter`/`ViewHolder`.
- Network requests through Retrofit.
- **Extra — error handling**: connectivity check (`NoConnection` state), HTTP error handling, loading indicator (`ProgressBar`).
- **Extra — additional patterns**: Repository Pattern and manual Dependency Injection (constructor injection + `ViewModelProvider.Factory`).


## Tech stack
Kotlin, Retrofit + Gson, ViewModel/LiveData, ViewBinding, Material Components (Exposed Dropdown Menu, `ShapeableImageView`).

## API & Endpoints

The app uses **TheCocktailDB API** through Retrofit. The following endpoints are used:

* **`list.php?c=list`** — returns the official list of available cocktail categories. Used to populate the **Category** dropdown on the main screen.
* **`list.php?i=list`** — returns the official list of available ingredients. Used to populate the **Ingredient** dropdown on the main screen.
* **`filter.php?c={category}`** — searches for cocktails by category. Returns a simplified version of each drink, containing the data needed to display the results in the `RecyclerView`.
* **`filter.php?i={ingredient}`** — searches for cocktails by ingredient. It uses the same simplified response structure as the category filter.
* **`lookup.php?i={id}`** — retrieves the complete details of a specific cocktail, including its name, category, glass, instructions, thumbnail URL, and ingredient/measure pairs.

The API response is mapped to Kotlin data classes using **Gson**. The `CocktailService` Retrofit interface defines these endpoints, while `CocktailRepository` handles the API calls and provides the data to the ViewModel.
