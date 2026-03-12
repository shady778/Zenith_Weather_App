package com.example.zenith.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.Locale

object StringHelper {
    fun getString(context: Context, @StringRes resId: Int, isArabic: Boolean): String {
        val locale = if (isArabic) Locale("ar") else Locale("en")
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.getString(resId)
    }
}
