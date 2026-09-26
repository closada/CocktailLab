package com.example.cocktaillab

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        viewModel.categories.observe(this) { categories -> setupCategoryDropdown(categories) }
        viewModel.ingredients.observe(this) { ingredients -> setupIngredientDropdown(ingredients) }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CocktailUIState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvMessage.text = getString(R.string.empty_state_message)
                    binding.tvMessage.visibility = View.VISIBLE
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
                    // This state is not used on this screen, only in DetailActivity.
                }
            }
        }
    }

    // Unlike the classic Spinner, MaterialAutoCompleteTextView does not
    // trigger any "ghost" selection when setting the adapter: the field
    // starts empty and displays the hint ("Category"/"Ingredient") until
    // the user selects something. Therefore, no flag is needed to ignore
    // the first selection, as we did with the Spinner.
    private fun setupCategoryDropdown(categories: List<CategoryItem>) {
        val names = categories.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        binding.actCategory.setAdapter(adapter)

        binding.actCategory.setOnItemClickListener { _, _, position, _ ->
            viewModel.searchByCategory(names[position])
        }
    }

    private fun setupIngredientDropdown(ingredients: List<IngredientItem>) {
        val names = ingredients.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        binding.actIngredient.setAdapter(adapter)

        binding.actIngredient.setOnItemClickListener { _, _, position, _ ->
            viewModel.searchByIngredient(names[position])
        }
    }

    private fun showMessage(message: String) {
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        adapter.updateDrinks(emptyList())
    }
}
