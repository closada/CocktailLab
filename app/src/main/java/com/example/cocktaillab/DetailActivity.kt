package com.example.cocktaillab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktaillab.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DRINK_ID = "drink_id"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {

            val drinkId = intent.getStringExtra(EXTRA_DRINK_ID)

            supportFragmentManager.beginTransaction()
                .replace(
                    binding.fragmentContainer.id,
                    DetailFragment.newInstance(drinkId)
                )
                .commit()
        }
    }
}