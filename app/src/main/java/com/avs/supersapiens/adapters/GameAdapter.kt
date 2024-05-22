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
import com.avs.supersapiens.models.Game

class GameAdapter(
    private val games: List<Game>,
    private val category: String,
    private val clickListener: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameIcon: ImageView = itemView.findViewById(R.id.gameIcon)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gameScore: TextView = itemView.findViewById(R.id.gameScore)
        val gameCardContent: LinearLayout = itemView.findViewById(R.id.cardContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_card, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.gameIcon.setImageResource(game.iconResId)
        holder.gameTitle.text = game.title
        holder.gameScore.text = "${game.score}/10" // Mostrar el puntaje

        // Set background color based on category
        val context = holder.itemView.context
        val backgroundColor = when (category) {
            "math" -> ContextCompat.getColor(context, R.color.colorMath)
            "english" -> ContextCompat.getColor(context, R.color.colorEnglish)
            "science" -> ContextCompat.getColor(context, R.color.colorScience)
            else -> ContextCompat.getColor(context, R.color.white)
        }
        holder.gameCardContent.setBackgroundColor(backgroundColor)

        holder.itemView.setOnClickListener { clickListener(game) }
    }

    override fun getItemCount() = games.size
}