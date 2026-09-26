package com.example.cocktaillab.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cocktaillab.repository.CocktailRepository

// Since CocktailViewModel receives the Repository through the constructor
// (Dependency Injection), the default ViewModelProvider does not know how
// to create it. This factory tells it how to do so.
class CocktailViewModelFactory(
    private val repository: CocktailRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailViewModel::class.java)) {
            return CocktailViewModel(repository, application) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}