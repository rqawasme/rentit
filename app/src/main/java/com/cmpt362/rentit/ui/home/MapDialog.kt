package com.cmpt362.rentit.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils.getDescriptionSnippet
import com.cmpt362.rentit.Utils.getImage
import com.cmpt362.rentit.details.DetailActivity
import kotlin.properties.Delegates

class MapDialog: DialogFragment() {
    private lateinit var name: String
    private var key by Delegates.notNull<Long>()
    private lateinit var description: String
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    companion object {
        const val MAP_DIALOG_NAME_KEY = "MAP_DIALOG_NAME_KEY"
        const val MAP_DIALOG_DESCRIPTION_KEY = "MAP_DIALOG_DESCRIPTION_KEY"
        const val MAP_DIALOG_ID_KEY = "MAP_DIALOG_ID_KEY"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val bundle = arguments
        if (bundle != null) {
            name = bundle.getString(MAP_DIALOG_NAME_KEY, "")
            key = bundle.getString(MAP_DIALOG_ID_KEY, "-1").toLong()
            description = bundle.getString(MAP_DIALOG_DESCRIPTION_KEY, "")
        }
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_map, null)
        imageView = view.findViewById(R.id.mapDialogImage)
        getImage(key, imageView)
        textView = view.findViewById(R.id.mapDialogText)
        getDescriptionSnippet(key, textView)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        builder.setTitle(name)
        builder.setPositiveButton(getString(R.string.view_more)) { dialog, id ->
            println("DEBUG: We will open the detail view now for $name")
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            intent.putExtra("id", key)
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.go_back)) {dialog, id ->
            println("DEBUG: BACK TO MAP")
        }
        dialog = builder.create()
        return dialog
    }
}