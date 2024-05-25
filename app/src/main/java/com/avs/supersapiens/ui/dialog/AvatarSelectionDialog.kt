package com.avs.supersapiens.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.avs.supersapiens.R

class AvatarSelectionDialog(context: Context, private val listener: AvatarSelectionListener) {

    interface AvatarSelectionListener {
        fun onAvatarSelected(resourceId: Int)
        fun onGallerySelected()
    }

    private val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_avatar_selection, null)
    private val avatarExample1: ImageView = dialogView.findViewById(R.id.avatarExample1)
    private val avatarExample2: ImageView = dialogView.findViewById(R.id.avatarExample2)
    private val avatarExample3: ImageView = dialogView.findViewById(R.id.avatarExample3)
    private val galleryIcon: ImageView = dialogView.findViewById(R.id.galleryIcon)

    private val dialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

    init {
        avatarExample1.setOnClickListener {
            listener.onAvatarSelected(R.drawable.ic_avatar_1)
            dialog.dismiss()
        }

        avatarExample2.setOnClickListener {
            listener.onAvatarSelected(R.drawable.ic_avatar_2)
            dialog.dismiss()
        }

        avatarExample3.setOnClickListener {
            listener.onAvatarSelected(R.drawable.ic_avatar_3)
            dialog.dismiss()
        }

        galleryIcon.setOnClickListener {
            listener.onGallerySelected()
            dialog.dismiss()
        }
    }

    fun show() {
        dialog.show()
    }
}
