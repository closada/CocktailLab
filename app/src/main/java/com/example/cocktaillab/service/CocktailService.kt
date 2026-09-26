package com.example.cocktaillab.service

import com.example.cocktaillab.model.CategoryListResponse
import com.example.cocktaillab.model.DrinkDetailResponse
import com.example.cocktaillab.model.DrinkListResponse
import com.example.cocktaillab.model.IngredientListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {

    // Categories availables
    @GET("list.php")
    suspend fun listCategories(
        @Query("c") list: String = "list"
    ): Response<CategoryListResponse>

    // Ingredients available
    @GET("list.php")
    suspend fun listIngredients(
        @Query("i") list: String = "list"
    ): Response<IngredientListResponse>

    // Filter by category (ej: "Cocktail", "Ordinary Drink").
    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): Response<DrinkListResponse>

    // Filtrer by ingredient (ej: "Gin", "Vodka").
    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): Response<DrinkListResponse>

    // Complete detail of a Drink by its ID.
    @GET("lookup.php")
    suspend fun lookupById(
        @Query("i") id: String
    ): Response<DrinkDetailResponse>
}