package com.example.cocktaillab.viewmodel

import com.example.cocktaillab.model.DrinkDetail
import com.example.cocktaillab.model.DrinkSummary

// la Activity/Fragment observa esto y decide que dibujar en cada momento.
sealed class CocktailUIState {

    object Idle : CocktailUIState()
    object Loading : CocktailUIState()

    // Sin conexion es un estado propio (no un Error mas) para poder mostrar un mensaje especifico
    object NoConnection : CocktailUIState()

    data class SearchSuccess(val drinks: List<DrinkSummary>) : CocktailUIState()
    data class DetailSuccess(val drink: DrinkDetail) : CocktailUIState()
    data class Error(val message: String) : CocktailUIState()
}