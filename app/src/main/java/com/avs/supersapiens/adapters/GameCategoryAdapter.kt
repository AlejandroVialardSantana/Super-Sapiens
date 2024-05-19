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
    private val categories: List<GameCategory>,
    private val clickListener: (GameCategory) -> Unit
) : RecyclerView.Adapter<GameCategoryAdapter.GameCategoryViewHolder>() {

    class GameCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameIcon: ImageView = itemView.findViewById(R.id.gameIcon)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val cardContent: LinearLayout = itemView.findViewById(R.id.cardContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_card, parent, false)
        return GameCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameCategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.gameIcon.setImageResource(category.iconResId)
        holder.gameTitle.text = category.title

        val context = holder.itemView.context
        val backgroundColor = when (category.category) {
            "math" -> ContextCompat.getColor(context, R.color.colorMath)
            "english" -> ContextCompat.getColor(context, R.color.colorEnglish)
            "science" -> ContextCompat.getColor(context, R.color.colorScience)
            else -> ContextCompat.getColor(context, R.color.white)
        }
        holder.cardContent.setBackgroundColor(backgroundColor)

        holder.itemView.setOnClickListener { clickListener(category) }
    }

    override fun getItemCount() = categories.size
}
