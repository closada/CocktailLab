# CocktailLab

Android app built with Kotlin for the "Desarrollo de Aplicaciones Android con Kotlin" midterm exam — Tema 1 (Cocktail catalog using TheCocktailDB API).

## Demo video
[Add Loom/YouTube/Drive link here]

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

## Known API limitation
TheCocktailDB's free/test API key (`1`) returns the full, correct list of categories and ingredients through `list.php`, but its `filter.php` endpoint appears to be restricted for that key: it currently returns a single fixed drink regardless of the filter value used. This was verified manually (e.g. `filter.php?i=Gin` and `filter.php?c=Cocktail` both return the same single result). The filtering UI is implemented exactly as required; this is a limitation of the public free API tier, not of the app's logic.

## Tech stack
Kotlin, Retrofit + Gson, ViewModel/LiveData, ViewBinding, Material Components (Exposed Dropdown Menu, `ShapeableImageView`).
