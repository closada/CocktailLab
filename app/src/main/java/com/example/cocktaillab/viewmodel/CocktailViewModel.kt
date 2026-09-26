package com.example.cocktaillab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cocktaillab.model.CategoryItem
import com.example.cocktaillab.model.DrinkSummary
import com.example.cocktaillab.model.IngredientItem
import com.example.cocktaillab.repository.CocktailException
import com.example.cocktaillab.repository.CocktailRepository
import com.example.cocktaillab.util.NetworkUtils
import kotlinx.coroutines.launch


// We use AndroidViewModel (instead of a regular ViewModel) because we need
// the app Context to check the connection with NetworkUtils, and this is
// the safe way to access it without risking a memory leak.
class CocktailViewModel(
    private val repository: CocktailRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableLiveData<CocktailUIState>(CocktailUIState.Idle)
    val uiState: LiveData<CocktailUIState> get() = _uiState

    // These two are independent of uiState: they contain the data used to
    // populate the Spinners, not the result of a search.
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
            } catch (e: CocktailException.ApiError) {
                _uiState.postValue(
                    CocktailUIState.Error(
                        "Server error: HTTP ${e.code}"
                    )
                )
            } catch (e: Exception) {
                _uiState.postValue(
                    CocktailUIState.Error(
                        "Unexpected error: ${e.message}"
                    )
                )
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


    private fun runIfConnected(block: suspend () -> Unit) {
        if (!NetworkUtils.isConnected(getApplication())) {
            _uiState.postValue(CocktailUIState.NoConnection)
            return
        }

        _uiState.value = CocktailUIState.Loading

        viewModelScope.launch {
            try {
                block()
            } catch (e: CocktailException.ApiError) {
                _uiState.postValue(
                    CocktailUIState.Error(
                        "Server error: HTTP ${e.code}"
                    )
                )
            } catch (e: CocktailException.NotFound) {
                _uiState.postValue(
                    CocktailUIState.Error(
                        "Drink not found."
                    )
                )
            } catch (e: Exception) {
                _uiState.postValue(
                    CocktailUIState.Error(
                        "Unexpected error: ${e.message}"
                    )
                )
            }
        }
    }
}