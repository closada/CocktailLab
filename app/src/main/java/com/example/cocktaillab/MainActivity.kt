package com.example.cocktaillab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cocktaillab.adapter.CocktailAdapter
import com.example.cocktaillab.databinding.ActivityMainBinding
import com.example.cocktaillab.model.CategoryItem
import com.example.cocktaillab.model.DrinkSummary
import com.example.cocktaillab.model.IngredientItem
import com.example.cocktaillab.repository.CocktailRepository
import com.example.cocktaillab.viewmodel.CocktailUIState
import com.example.cocktaillab.viewmodel.CocktailViewModel
import com.example.cocktaillab.viewmodel.CocktailViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CocktailViewModel
    private lateinit var adapter: CocktailAdapter

    // Android dispara onItemSelected una vez "gratis" apenas seteas el
    // adapter del Spinner, antes de que el usuario toque nada. Estos flags
    // evitan que esa selección fantasma dispare una búsqueda al abrir la app.
    private var categorySpinnerReady = false
    private var ingredientSpinnerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            CocktailViewModelFactory(CocktailRepository.create(), application)
        )[CocktailViewModel::class.java]

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = CocktailAdapter(emptyList()) { drink -> openDetail(drink) }
        binding.rvDrinks.adapter = adapter
    }

    private fun openDetail(drink: DrinkSummary) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DRINK_ID, drink.id)
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.categories.observe(this) { categories -> setupCategorySpinner(categories) }
        viewModel.ingredients.observe(this) { ingredients -> setupIngredientSpinner(ingredients) }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CocktailUIState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvMessage.visibility = View.GONE
                }

                is CocktailUIState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvMessage.visibility = View.GONE
                }

                is CocktailUIState.SearchSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvMessage.visibility = View.GONE
                    adapter.updateDrinks(state.drinks)
                }

                is CocktailUIState.NoConnection -> {
                    binding.progressBar.visibility = View.GONE
                    showMessage(getString(R.string.no_connection_message))
                }

                is CocktailUIState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showMessage(state.message)
                }

                is CocktailUIState.DetailSuccess -> {
                    // Este estado no se usa en esta pantalla, solo en DetailActivity.
                }
            }
        }
    }

    private fun setupCategorySpinner(categories: List<CategoryItem>) {
        val names = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinnerCategories.adapter = adapter

        binding.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!categorySpinnerReady) {
                    categorySpinnerReady = true
                    return
                }
                viewModel.searchByCategory(names[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupIngredientSpinner(ingredients: List<IngredientItem>) {
        val names = ingredients.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.spinnerIngredients.adapter = adapter

        binding.spinnerIngredients.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!ingredientSpinnerReady) {
                    ingredientSpinnerReady = true
                    return
                }
                viewModel.searchByIngredient(names[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showMessage(message: String) {
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        adapter.updateDrinks(emptyList())
    }
}