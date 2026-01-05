package com.thundenet.admin.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnack(view: View, text: String) {
    Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
}