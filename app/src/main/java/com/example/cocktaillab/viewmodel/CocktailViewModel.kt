package com.example.cocktaillab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cocktaillab.model.CategoryItem
import com.example.cocktaillab.model.DrinkSummary
import com.example.cocktaillab.model.IngredientItem
import com.example.cocktaillab.repository.CocktailRepository
import com.example.cocktaillab.util.NetworkUtils
import kotlinx.coroutines.launch

// Usamos AndroidViewModel (en vez de ViewModel a secas) porque necesitamos
// el Context de la app para chequear la conexion con NetworkUtils, y esta
// es la forma segura de tenerlo sin riesgo de memory leak.
class CocktailViewModel(
    private val repository: CocktailRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<CocktailUIState>(CocktailUIState.Idle)
    val uiState: MutableLiveData<CocktailUIState> get() = _uiState

    // Estas dos son independientes del uiState: son los datos para poblar
    // los Spinners, no el resultado de una busqueda.
    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> get() = _categories

    private val _ingredients = MutableLiveData<List<IngredientItem>>()
    val ingredients: LiveData<List<IngredientItem>> get() = _ingredients

    init {
        loadFilters()
    }

    private fun loadFilters() {
        if (!NetworkUtils.isConnected(getApplication())) {
            _uiState.postValue(CocktailUIState.NoConnection)
            return
        }

        viewModelScope.launch {
            try {
                _categories.postValue(repository.getCategories())
                _ingredients.postValue(repository.getIngredients())
            } catch (e: Exception) {
                _uiState.postValue(CocktailUIState.Error("No se pudieron cargar los filtros: ${e.message}"))
            }
        }
    }

    fun searchByCategory(category: String) {
        runIfConnected {
            val results = repository.searchByCategory(category)
            handleSearchResults(results, category)
        }
    }

    fun searchByIngredient(ingredient: String) {
        runIfConnected {
            val results = repository.searchByIngredient(ingredient)
            handleSearchResults(results, ingredient)
        }
    }

    fun fetchDrinkDetail(id: String) {
        runIfConnected {
            val drink = repository.getDrinkDetail(id)
            _uiState.postValue(CocktailUIState.DetailSuccess(drink))
        }
    }

    private fun handleSearchResults(results: List<DrinkSummary>, query: String) {
        if (results.isNotEmpty()) {
            _uiState.postValue(CocktailUIState.SearchSuccess(results))
        } else {
            _uiState.postValue(CocktailUIState.Error("No se encontraron tragos para \"$query\""))
        }
    }

    // Envuelve el patron repetido: chequear conexion, mostrar Loading,
    // ejecutar la llamada suspend y capturar cualquier excepcion como Error.
    private fun runIfConnected(block: suspend () -> Unit) {
        if (!NetworkUtils.isConnected(getApplication())) {
            _uiState.postValue(CocktailUIState.NoConnection)
            return
        }

        _uiState.value = CocktailUIState.Loading

        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                _uiState.postValue(CocktailUIState.Error("Error: ${e.message}"))
            }
        }
    }
}