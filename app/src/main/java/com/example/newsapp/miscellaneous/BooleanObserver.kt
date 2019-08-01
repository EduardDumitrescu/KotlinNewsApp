package com.example.newsapp.miscellaneous

import kotlin.properties.Delegates

class BooleanObserver {
    var value: Boolean by Delegates.observable(false){ _, oldValue, newValue ->
        onValueChanged?.invoke(oldValue, newValue)
    }

    var onValueChanged: ((Boolean, Boolean) -> Unit)? = null
}