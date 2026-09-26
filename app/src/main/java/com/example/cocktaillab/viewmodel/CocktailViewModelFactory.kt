package com.example.cocktaillab.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cocktaillab.repository.CocktailRepository

// Como CocktailViewModel recibe el Repository por constructor (Dependency
// Injection), el ViewModelProvider por defecto no sabe como construirlo.
// Esta factory le enseña como hacerlo.
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