package com.dech53.dao_yu

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import androidx.compose.ui.platform.ClipboardManager
import androidx.room.Room
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.dao.DataBaseRepository
import okhttp3.OkHttpClient
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(private val app: Application) : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)
        addSingletonFactory<CookieDatabase> {
            Room.databaseBuilder(
                app,
                CookieDatabase::class.java,
                "cookie.db"
            )
                .build()
        }
        addSingletonFactory<DataBaseRepository> { DataBaseRepository(get()) }
        addSingleton(ImageLoader.Builder(app)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(app, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(app.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.2)
                    .build()
            }
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            OkHttpClient()
                        }
                    )
                )
                if (SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .logger(DebugLogger())
            .build())
    }
}