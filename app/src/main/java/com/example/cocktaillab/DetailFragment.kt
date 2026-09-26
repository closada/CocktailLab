package com.example.cocktaillab

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cocktaillab.databinding.FragmentDetailBinding
import com.example.cocktaillab.model.DrinkDetail
import com.example.cocktaillab.repository.CocktailRepository
import com.example.cocktaillab.viewmodel.CocktailUIState
import com.example.cocktaillab.viewmodel.CocktailViewModel
import com.example.cocktaillab.viewmodel.CocktailViewModelFactory
import java.net.URL

class DetailFragment : Fragment() {

    companion object {
        private const val ARG_DRINK_ID = "drink_id"

        fun newInstance(drinkId: String?): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DRINK_ID, drinkId)
                }
            }
        }
    }

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CocktailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            CocktailViewModelFactory(
                CocktailRepository.create(),
                requireActivity().application
            )
        )[CocktailViewModel::class.java]

        setupObservers()

        val drinkId = arguments?.getString(ARG_DRINK_ID)

        if (drinkId.isNullOrBlank()) {
            showError("No se pudo identificar el trago.")
            return
        }

        viewModel.fetchDrinkDetail(drinkId)
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is CocktailUIState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }

                is CocktailUIState.DetailSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE

                    showDrinkDetail(state.drink)
                }

                is CocktailUIState.NoConnection -> {
                    binding.progressBar.visibility = View.GONE
                    showError(getString(R.string.no_connection_message))
                }

                is CocktailUIState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showError(state.message)
                }

                else -> {
                    // Other states are not used by this screen.
                }
            }
        }
    }

    private fun showDrinkDetail(drink: DrinkDetail) {

        binding.tvDrinkName.text = drink.name

        binding.tvCategory.text =
            "Category: ${drink.category ?: "Not available"}"

        binding.tvGlass.text =
            "Glass: ${drink.glass ?: "Not available"}"

        binding.tvInstructions.text =
            drink.instructions ?: "Preparation instructions not available."

        val ingredientsText = drink.getIngredients()
            .joinToString("\n") { (ingredient, measure) ->
                if (measure.isNullOrBlank()) {
                    "• $ingredient"
                } else {
                    "• $measure $ingredient"
                }
            }

        binding.tvIngredients.text =
            if (ingredientsText.isBlank()) {
                "Ingredients not available."
            } else {
                ingredientsText
            }

        loadImage(binding.ivDrink, drink.thumbnail)
    }

    private fun loadImage(imageView: ImageView, url: String?) {

        imageView.tag = url

        if (url.isNullOrBlank()) {
            imageView.setImageDrawable(null)
            return
        }

        Thread {
            try {
                val bitmap = BitmapFactory.decodeStream(
                    URL(url).openStream()
                )

                imageView.post {
                    if (imageView.tag == url) {
                        imageView.setImageBitmap(bitmap)
                    }
                }

            } catch (e: Exception) {
                imageView.post {
                    imageView.setImageDrawable(null)
                }
            }
        }.start()
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}