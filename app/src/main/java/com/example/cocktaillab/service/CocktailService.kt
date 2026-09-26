package com.example.cocktaillab.service

import com.example.cocktaillab.model.CategoryListResponse
import com.example.cocktaillab.model.DrinkDetailResponse
import com.example.cocktaillab.model.DrinkListResponse
import com.example.cocktaillab.model.IngredientListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {

    // Listado oficial de categorias validas, para poblar el Spinner.
    @GET("list.php")
    suspend fun listCategories(
        @Query("c") list: String = "list"
    ): Response<CategoryListResponse>

    // Listado oficial de ingredientes validos, para poblar el otro Spinner.
    @GET("list.php")
    suspend fun listIngredients(
        @Query("i") list: String = "list"
    ): Response<IngredientListResponse>

    // Filtra tragos por categoria (ej: "Cocktail", "Ordinary Drink").
    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): Response<DrinkListResponse>

    // Filtra tragos por ingrediente (ej: "Gin", "Vodka").
    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): Response<DrinkListResponse>

    // Trae el detalle completo de un trago por su id (para la pantalla de detalle).
    @GET("lookup.php")
    suspend fun lookupById(
        @Query("i") id: String
    ): Response<DrinkDetailResponse>
}