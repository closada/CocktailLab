package com.example.cocktaillab.repository

import android.util.Log
import com.example.cocktaillab.model.CategoryItem
import com.example.cocktaillab.model.DrinkDetail
import com.example.cocktaillab.model.DrinkSummary
import com.example.cocktaillab.model.IngredientItem
import com.example.cocktaillab.service.CocktailService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Repository Pattern: this is the only class that knows that Retrofit exists.
// The ViewModel does not know about the API; it only requests data from the Repository.
// Receives the CocktailService through the constructor (manual Dependency Injection)
// instead of creating it itself. This decouples the Repository from how Retrofit is configured
class CocktailRepository(private val service: CocktailService) {

    companion object {
        private const val TAG = "CocktailRepository"
        private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

        // Creates the "real" Repository by setting up Retrofit once here.
        fun create(): CocktailRepository {
            val service = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CocktailService::class.java)

            return CocktailRepository(service)
        }
    }

    suspend fun getCategories(): List<CategoryItem> {
        Log.d(TAG, "Pidiendo listado de categorias")
        val response = service.listCategories()

        if (!response.isSuccessful) {
            Log.e(TAG, "Error trayendo categorias: ${response.code()}")
            throw CocktailException.ApiError(response.code())
        }

        return response.body()?.drinks ?: emptyList()
    }

    suspend fun getIngredients(): List<IngredientItem> {
        Log.d(TAG, "Pidiendo listado de ingredientes")
        val response = service.listIngredients()

        if (!response.isSuccessful) {
            Log.e(TAG, "Error trayendo ingredientes: ${response.code()}")
            throw CocktailException.ApiError(response.code())
        }

        return response.body()?.drinks ?: emptyList()
    }

    suspend fun searchByCategory(category: String): List<DrinkSummary> {
        Log.d(TAG, "Buscando tragos con categoria: $category")
        val response = service.filterByCategory(category)

        if (!response.isSuccessful) {
            Log.e(TAG, "Error buscando por categoria: ${response.code()}")
            throw CocktailException.ApiError(response.code())
        }

        return response.body()?.drinks ?: emptyList()
    }

    suspend fun searchByIngredient(ingredient: String): List<DrinkSummary> {
        Log.d(TAG, "Buscando tragos con ingrediente: $ingredient")
        val response = service.filterByIngredient(ingredient)

        if (!response.isSuccessful) {
            Log.e(TAG, "Error buscando por ingrediente: ${response.code()}")
            throw CocktailException.ApiError(response.code())
        }

        return response.body()?.drinks ?: emptyList()
    }

    suspend fun getDrinkDetail(id: String): DrinkDetail {
        Log.d(TAG, "Buscando detalle del trago id=$id")
        val response = service.lookupById(id)

        if (!response.isSuccessful) {
            Log.e(TAG, "Error buscando detalle: ${response.code()}")
            throw CocktailException.ApiError(response.code())
        }


        // lookup.php always returns a single drink inside the "drinks" list.
        return response.body()?.drinks?.firstOrNull()
            ?: throw CocktailException.NotFound(id)
    }
}