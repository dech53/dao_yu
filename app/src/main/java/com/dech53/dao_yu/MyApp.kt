package com.dech53.dao_yu

import android.app.Application
import uy.kohesive.injekt.Injekt

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Injekt.importModule(AppModule(this))
    }
}