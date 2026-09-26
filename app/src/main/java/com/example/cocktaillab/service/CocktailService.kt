package com.example.cocktaillab.service

import com.example.cocktaillab.model.DrinkDetailResponse
import com.example.cocktaillab.model.DrinkListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailService {

    // Filtra tragos que contengan un ingrediente especifico (ej: "Vodka").
    // Uno de los dos filtros del buscador de la pantalla principal.
    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): Response<DrinkListResponse>

    // Busca por nombre exacto o parcial (ej: "Margarita").
    // El otro filtro del buscador de la pantalla principal.
    @GET("search.php")
    suspend fun searchByName(
        @Query("s") name: String
    ): Response<DrinkListResponse>

    // Trae el detalle completo de un trago por su id (para la pantalla de detalle).
    @GET("lookup.php")
    suspend fun lookupById(
        @Query("i") id: String
    ): Response<DrinkDetailResponse>
}