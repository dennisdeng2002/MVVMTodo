package com.codinginflow.mvvmtodo.core.ext

import androidx.appcompat.widget.SearchView

fun SearchView.onQueryTextChanged(listener: (newText: String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener.invoke(newText.orEmpty())
            return true
        }
    })
}
