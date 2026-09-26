package com.example.cocktaillab.viewmodel

import com.example.cocktaillab.model.DrinkDetail
import com.example.cocktaillab.model.DrinkSummary

// The Activity/Fragment observes this states and decides what to draw
sealed class CocktailUIState {

    object Idle : CocktailUIState()
    object Loading : CocktailUIState()

    object NoConnection : CocktailUIState()

    data class SearchSuccess(val drinks: List<DrinkSummary>) : CocktailUIState()
    data class DetailSuccess(val drink: DrinkDetail) : CocktailUIState()
    data class Error(val message: String) : CocktailUIState()
}