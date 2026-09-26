package com.example.cocktaillab.repository

sealed class CocktailException(message: String) : Exception(message) {

    // API response Error (4xx/5xx).
    class ApiError(val code: Int) : CocktailException("Server error: $code")

    // lookup.php  didnt found any drink for ID.
    class NotFound(id: String) : CocktailException("Drink not found: $id")
}