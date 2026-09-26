package com.example.cocktaillab.model

import com.google.gson.annotations.SerializedName


// What filter.php returns (by ingredient or category): a simplified version
// with only the data needed to display the RecyclerView on the main screen.
data class DrinkSummary(
    @SerializedName("idDrink")
    val id: String,
    @SerializedName("strDrink")
    val name: String,
    @SerializedName("strDrinkThumb")
    val thumbnail: String?
)

// What lookup.php?i={id} returns: the complete drink details.
data class DrinkDetail(
    @SerializedName("idDrink")
    val id: String,
    @SerializedName("strDrink")
    val name: String,
    @SerializedName("strDrinkThumb")
    val thumbnail: String?,
    @SerializedName("strCategory")
    val category: String?,
    @SerializedName("strGlass")
    val glass: String?,
    @SerializedName("strInstructions") //strInstructionsES in spanish
    val instructions: String?,

    // The API does not return a list of ingredients: it returns up to 15 separate
    // pairs (strIngredient1..15 / strMeasure1..15). We map them as they are
    // and then combine them ourselves using getIngredients().

    @SerializedName("strIngredient1") val ingredient1: String?,
    @SerializedName("strIngredient2") val ingredient2: String?,
    @SerializedName("strIngredient3") val ingredient3: String?,
    @SerializedName("strIngredient4") val ingredient4: String?,
    @SerializedName("strIngredient5") val ingredient5: String?,
    @SerializedName("strIngredient6") val ingredient6: String?,
    @SerializedName("strIngredient7") val ingredient7: String?,
    @SerializedName("strIngredient8") val ingredient8: String?,
    @SerializedName("strIngredient9") val ingredient9: String?,
    @SerializedName("strIngredient10") val ingredient10: String?,
    @SerializedName("strIngredient11") val ingredient11: String?,
    @SerializedName("strIngredient12") val ingredient12: String?,
    @SerializedName("strIngredient13") val ingredient13: String?,
    @SerializedName("strIngredient14") val ingredient14: String?,
    @SerializedName("strIngredient15") val ingredient15: String?,

    @SerializedName("strMeasure1") val measure1: String?,
    @SerializedName("strMeasure2") val measure2: String?,
    @SerializedName("strMeasure3") val measure3: String?,
    @SerializedName("strMeasure4") val measure4: String?,
    @SerializedName("strMeasure5") val measure5: String?,
    @SerializedName("strMeasure6") val measure6: String?,
    @SerializedName("strMeasure7") val measure7: String?,
    @SerializedName("strMeasure8") val measure8: String?,
    @SerializedName("strMeasure9") val measure9: String?,
    @SerializedName("strMeasure10") val measure10: String?,
    @SerializedName("strMeasure11") val measure11: String?,
    @SerializedName("strMeasure12") val measure12: String?,
    @SerializedName("strMeasure13") val measure13: String?,
    @SerializedName("strMeasure14") val measure14: String?,
    @SerializedName("strMeasure15") val measure15: String?
) {

    // Builds the actual list of "ingredient + amount", ignoring empty fields.
    // We use it in the detail Fragment to avoid repeating this if statement
    // 15 times in the view.
    fun getIngredients(): List<Pair<String, String?>> {
        val ingredients = listOf(
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
            ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
            ingredient11, ingredient12, ingredient13, ingredient14, ingredient15
        )
        val measures = listOf(
            measure1, measure2, measure3, measure4, measure5,
            measure6, measure7, measure8, measure9, measure10,
            measure11, measure12, measure13, measure14, measure15
        )

        return ingredients.zip(measures)
            .filter { (ingredient, _) -> !ingredient.isNullOrBlank() }
            .map { (ingredient, measure) -> ingredient!! to measure }
    }
}

// Wrappers: the API always wraps the result in a "drinks" object.
// It can be null if nothing is found (it does not throw an error,
// it returns drinks: null).
data class DrinkListResponse(
    val drinks: List<DrinkSummary>?
)

data class DrinkDetailResponse(
    val drinks: List<DrinkDetail>?
)

// What list.php?c=list returns: the official list of valid categories.
data class CategoryItem(
    @SerializedName("strCategory")
    val name: String
)

// What list.php?i=list returns. Note: the API also uses the
// "strIngredient1" field here (instead of a cleaner name such as
// "strIngredient"), so we map it the same way for Gson to understand it.
data class IngredientItem(
    @SerializedName("strIngredient1")
    val name: String
)

data class CategoryListResponse(
    val drinks: List<CategoryItem>?
)

data class IngredientListResponse(
    val drinks: List<IngredientItem>?
)