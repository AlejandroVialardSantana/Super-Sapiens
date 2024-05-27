package com.avs.supersapiens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.avs.supersapiens.R
import com.avs.supersapiens.models.Game

/**
 * Adaptador para el RecyclerView de juegos
 *
 * @param games Lista de juegos a mostrar
 * @param clickListener Función que se ejecuta al hacer click en un juego
 */
class GameAdapter(
    private var games: List<Game>,
    private val clickListener: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameIcon: ImageView = itemView.findViewById(R.id.gameIcon)
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gameScore: TextView = itemView.findViewById(R.id.gameScore)
        val gameCardContent: LinearLayout = itemView.findViewById(R.id.cardContent)
        val lockIcon: ImageView = itemView.findViewById(R.id.lockIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game_card, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.gameIcon.setImageResource(game.iconResId)
        holder.gameTitle.text = game.title
        holder.gameScore.text = "${game.score}/10"

        // Cambiar el fondo del cardView según el tipo de juego
        val backgroundDrawable = when (game.type) {
            "sum", "multiply" -> R.drawable.gradient_math
            "word", "vocabulary" -> R.drawable.gradient_english
            "animals", "solar" -> R.drawable.gradient_science
            else -> R.color.white
        }
        holder.gameCardContent.setBackgroundResource(backgroundDrawable)

        holder.lockIcon.visibility = if (game.isUnlocked) View.GONE else View.VISIBLE

        // Si el juego está desbloqueado, se puede hacer click
        holder.itemView.setOnClickListener {
            if (game.isUnlocked) {
                clickListener(game)
            } else {
                Toast.makeText(it.context, "Este juego está bloqueado, consigue una puntuación de 5 en el anterior primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = games.size

    fun updateGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }
}
