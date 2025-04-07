package com.project.cataxi

import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.project.cataxi.ui.theme.CaTaxiTheme
import com.yandex.mapkit.MapKitFactory

class ApplicationSettings: Application(){
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("fc283d50-7a89-4871-ba06-68f2e2a431a9")
    }
}