package com.example.cocktaillab.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktaillab.databinding.ItemDrinkBinding
import com.example.cocktaillab.model.DrinkSummary
import java.net.URL

// Mismo esquema que vimos en clase: ViewBinding por item + una lambda de
// click en vez de una interfaz de listener.
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

        // Descarga manual de la imagen, sin librerias externas (no vimos
        // Glide/Coil en clase). Se baja en un Thread aparte para no bloquear
        // la UI y se vuelve al hilo principal con post() para setearla.
        //
        // El "tag" evita el problema clasico de RecyclerView: como las vistas
        // se reciclan, si el usuario scrollea rapido, una descarga vieja y
        // lenta podria terminar mostrandose sobre el item equivocado. Guardamos
        // la URL que le corresponde a esta vista y, cuando la descarga termina,
        // chequeamos que siga siendo la misma antes de mostrarla.
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
                    // Si falla la descarga (sin conexion, url rota, etc.)
                    // simplemente dejamos el ImageView vacio.
                }
            }.start()
        }
    }
}