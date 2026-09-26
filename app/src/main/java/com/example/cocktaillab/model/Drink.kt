package com.example.cocktaillab.model

import com.google.gson.annotations.SerializedName

// Lo que devuelve filter.php (por ingrediente o categoria): version resumida,
// justo lo necesario para pintar el RecyclerView de la pantalla principal.
data class DrinkSummary(
    @SerializedName("idDrink")
    val id: String,
    @SerializedName("strDrink")
    val name: String,
    @SerializedName("strDrinkThumb")
    val thumbnail: String?
)

// Lo que devuelve lookup.php?i={id}: el detalle completo del trago.
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

    // La API no devuelve una lista de ingredientes: devuelve hasta 15 pares
    // sueltos (strIngredient1..15 / strMeasure1..15). Los mapeamos tal cual
    // y despues los combinamos nosotros con getIngredients().
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

    // Arma la lista real de "ingrediente + cantidad" ignorando los campos vacios.
    // La usamos en el Fragment de detalle para no tener que repetir este if
    // 15 veces en la vista.
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

// Wrappers: la API siempre envuelve el resultado en un objeto "drinks".
// Puede venir null si no encuentra nada (no tira error, devuelve drinks: null).
data class DrinkListResponse(
    val drinks: List<DrinkSummary>?
)

data class DrinkDetailResponse(
    val drinks: List<DrinkDetail>?
)

// Lo que devuelve list.php?c=list: el listado oficial de categorias validas.
data class CategoryItem(
    @SerializedName("strCategory")
    val name: String
)

// Lo que devuelve list.php?i=list. Ojo: la API reutiliza el campo
// "strIngredient1" aca tambien (no usa un nombre mas prolijo tipo
// "strIngredient"), asi que lo mapeamos igual para que Gson lo entienda.
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