package com.avs.supersapiens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avs.supersapiens.R
import com.avs.supersapiens.models.GameCategory

class GameCategoryAdapter(
    private var categories: List<GameCategory>,
    private val clickListener: (GameCategory) -> Unit
) : RecyclerView.Adapter<GameCategoryAdapter.GameCategoryViewHolder>() {

    class GameCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        val categoryProgress: TextView = itemView.findViewById(R.id.categoryProgress)
        val cardContent: LinearLayout = itemView.findViewById(R.id.cardContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_category_card, parent, false)
        return GameCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameCategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryIcon.setImageResource(category.iconResId)
        holder.categoryTitle.text = category.title
        holder.categoryProgress.text = "${category.completedGames}/${category.totalGames}"

        val backgroundDrawable = when (category.category) {
            "math" -> R.drawable.gradient_math
            "english" -> R.drawable.gradient_english
            "science" -> R.drawable.gradient_science
            else -> R.color.white
        }
        holder.cardContent.setBackgroundResource(backgroundDrawable)

        holder.itemView.setOnClickListener { clickListener(category) }
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<GameCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}