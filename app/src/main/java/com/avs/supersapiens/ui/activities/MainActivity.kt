package com.avs.supersapiens.ui.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.avs.supersapiens.R
import com.avs.supersapiens.utils.ProgressManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MainActivity : AppCompatActivity() {

    private lateinit var progressManager: ProgressManager
    private lateinit var userIcon: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressManager = ProgressManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)

        userIcon = findViewById(R.id.userIcon)
        progressBar = findViewById(R.id.progressBar)
        userIcon.setOnClickListener {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(R.id.action_homeFragment_to_userFragment)
            } else if (navController.currentDestination?.id == R.id.gameListFragment) {
                navController.navigate(R.id.action_gameListFragment_to_userFragment)
            }
        }

        // Cargar la imagen de perfil
        loadProfileImage(userIcon)

        // Botón para borrar las preferencias
        val clearScoresButton: ImageView = findViewById(R.id.clearScoresButton)
        clearScoresButton.setOnClickListener {
            progressManager.clearAllScores()
        }
    }

    private fun loadProfileImage(userIcon: ImageView) {
        val sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val imageString = sharedPreferences.getString("profile_image", null)
        imageString?.let {
            progressBar.visibility = View.VISIBLE
            if (it.startsWith("content://") || it.startsWith("file://")) {
                val uri = Uri.parse(it)
                Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            userIcon.setImageBitmap(resource)
                            progressBar.visibility = View.GONE
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            progressBar.visibility = View.GONE
                        }
                    })
            } else {
                val resourceId = it.toInt()
                Glide.with(this)
                    .asBitmap()
                    .load(resourceId)
                    .apply(RequestOptions.circleCropTransform())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            userIcon.setImageBitmap(resource)
                            progressBar.visibility = View.GONE
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            progressBar.visibility = View.GONE
                        }
                    })
            }
        }
    }

    fun updateProfileImage(imageString: String) {
        val sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("profile_image", imageString).apply()
        loadProfileImage(userIcon)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
