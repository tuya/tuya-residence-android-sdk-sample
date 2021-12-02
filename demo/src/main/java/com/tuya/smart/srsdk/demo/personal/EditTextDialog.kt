package com.tuya.smart.srsdk.demo.personal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.tuya.smart.srsdk.R
import androidx.fragment.app.DialogFragment

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/8 10:08
 */
class EditTextDialog(private val title: String?, private val listener: InputListener?) : DialogFragment() {

    private lateinit var mTvTitle: TextView
    private lateinit var mEtContent: EditText

    interface InputListener {
        fun onInputComplete(content: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_edit, null)
        mTvTitle = view.findViewById(R.id.tv_title)
        mEtContent = view.findViewById(R.id.et_content)
        mTvTitle.text = this.title
        builder.setView(view)
            .setPositiveButton("Confirm"
            ) { _, _ ->
                run {
                    listener?.onInputComplete(mEtContent.text.toString())
                }
            }.setNegativeButton("Cancel", null)
        return builder.create()
    }
}