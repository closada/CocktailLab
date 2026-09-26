package com.example.cocktaillab.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktaillab.databinding.ItemDrinkBinding
import com.example.cocktaillab.model.DrinkSummary
import java.net.URL

// ViewBinding for each item + a click lambda instead of a listener interface.

class CocktailAdapter(
    private var drinks: List<DrinkSummary>,
    private val onItemClick: (DrinkSummary) -> Unit
) : RecyclerView.Adapter<CocktailAdapter.DrinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val binding = ItemDrinkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DrinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        holder.bind(drinks[position])
    }

    override fun getItemCount(): Int = drinks.size

    fun updateDrinks(newDrinks: List<DrinkSummary>) {
        drinks = newDrinks
        notifyDataSetChanged()
    }

    inner class DrinkViewHolder(private val binding: ItemDrinkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(drink: DrinkSummary) {
            binding.tvDrinkName.text = drink.name
            loadThumbnail(binding.ivThumbnail, drink.thumbnail)
            binding.root.setOnClickListener { onItemClick(drink) }
        }

    // Manually downloads the image without external libraries
    //  The image is downloaded on a separate thread to avoid
    // blocking the UI, then post() is used to return to the main thread and set it.

    // The "tag" prevents a common RecyclerView issue: since views are recycled,
    // if the user scrolls quickly, an old and slow download could finish and be
    // displayed on the wrong item. We store the URL associated with this view and,
    // when the download finishes, check that it is still the same before displaying it.

        private fun loadThumbnail(imageView: ImageView, url: String?) {
            imageView.tag = url
            imageView.setImageDrawable(null)
            if (url.isNullOrBlank()) return

            Thread {
                try {
                    val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                    imageView.post {
                        if (imageView.tag == url) {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    // If the download fails (no connection, broken URL, etc.),
                    // the ImageView is simply left empty.
                }
            }.start()
        }
    }
}