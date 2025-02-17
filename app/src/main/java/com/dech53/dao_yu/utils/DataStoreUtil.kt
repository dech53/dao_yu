package com.dech53.dao_yu.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class DataStoreUtil private constructor(private val context: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {
        private var instance: DataStoreUtil? = null

        fun getInstance(context: Context): DataStoreUtil {
            return instance ?: synchronized(this) {
                instance ?: DataStoreUtil(context.applicationContext).also { instance = it }
            }
        }
    }
}
