package com.avs.supersapiens.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.avs.supersapiens.R
import com.avs.supersapiens.databinding.FragmentUserBinding
import com.avs.supersapiens.ui.activities.MainActivity
import com.avs.supersapiens.utils.ProgressManager
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.InputStream
import java.util.*

class UserFragment : Fragment(), AvatarSelectionDialog.AvatarSelectionListener {

    private lateinit var binding: FragmentUserBinding
    private val PICK_IMAGE_REQUEST = 1
    private val UCROP_REQUEST_CODE = UCrop.REQUEST_CROP
    private lateinit var progressManager: ProgressManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressManager = ProgressManager(requireContext())

        loadProfileImage()

        binding.changeAvatarButton.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.clearScoresButton.setOnClickListener {
            showClearScoresConfirmationDialog()
        }
    }

    private fun showAvatarSelectionDialog() {
        val avatarSelectionDialog = AvatarSelectionDialog(requireContext(), this)
        avatarSelectionDialog.show()
    }

    private fun showClearScoresConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_confirm_clear_scores, null)
        builder.setView(dialogView)

        val dialog = builder.create()

        val btnYes: Button = dialogView.findViewById(R.id.btnYes)
        val btnNo: Button = dialogView.findViewById(R.id.btnNo)

        btnYes.setOnClickListener {
            progressManager.clearAllScores()
            dialog.dismiss()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { startCrop(it) }
                }
                UCROP_REQUEST_CODE -> {
                    handleCropResult(data)
                }
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(context?.cacheDir, "cropped_image_${UUID.randomUUID()}"))
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(150, 150)
            .withOptions(options)
            .start(requireContext(), this, UCROP_REQUEST_CODE)
    }

    private fun handleCropResult(data: Intent?) {
        val resultUri = UCrop.getOutput(data!!)
        resultUri?.let {
            setProfileImageUri(it)
            (activity as? MainActivity)?.updateProfileImage(it.toString())
        }
    }

    private fun setProfileImageResource(resourceId: Int) {
        binding.userAvatar.setImageResource(resourceId)
        saveProfileImage(resourceId.toString())
        (activity as? MainActivity)?.updateProfileImage(resourceId.toString())
    }

    private fun setProfileImageUri(uri: Uri) {
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
        val selectedImage = BitmapFactory.decodeStream(inputStream)
        binding.userAvatar.setImageBitmap(selectedImage)
        saveProfileImage(uri.toString())
        (activity as? MainActivity)?.updateProfileImage(uri.toString())
    }

    private fun saveProfileImage(imageString: String) {
        val sharedPreferences = activity?.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("profile_image", imageString)?.apply()
    }

    private fun loadProfileImage() {
        val sharedPreferences = activity?.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val imageString = sharedPreferences?.getString("profile_image", null)
        imageString?.let {
            if (it.startsWith("content://") || it.startsWith("file://")) {
                val uri = Uri.parse(it)
                setProfileImageUri(uri)
            } else {
                val resourceId = it.toInt()
                setProfileImageResource(resourceId)
            }
        }
    }

    override fun onAvatarSelected(resourceId: Int) {
        setProfileImageResource(resourceId)
    }

    override fun onGallerySelected() {
        pickImageFromGallery()
    }
}
