@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.dech53.dao_yu

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.dao.FavoriteDataBase
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import com.dech53.dao_yu.views.ImageView
import com.dech53.dao_yu.views.TRView
import okhttp3.OkHttpClient

class ThreadAndReplyView : ComponentActivity() {
    val favDbDao by lazy {
        FavoriteDataBase.getDatabase(applicationContext)
    }
    private val viewModel by viewModels<ThreadInfoView_ViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ThreadInfoView_ViewModel(db.cookieDao, favDbDao.favoriteDao) as T
                }
            }
        }
    )
    private val db by lazy {
        CookieDatabase.getDatabase(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.imgList.clear()
        viewModel.tipsCount.value = 0
        Log.d("TRA", "进程销毁")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val threadId = intent.getStringExtra("threadId")
        val hash = intent.getStringExtra("hash")
        viewModel.isFaved.value = intent.getBooleanExtra("hasId", false)
        viewModel.hash.value = hash ?: ""
        setContent {
            Dao_yuTheme {
                val context = LocalContext.current
                val imageLoader = remember {
                    ImageLoader.Builder(context)
                        .memoryCache {
                            MemoryCache.Builder()
                                .maxSizePercent(context, 0.25)
                                .build()
                        }
                        .diskCache {
                            DiskCache.Builder()
                                .directory(context.cacheDir.resolve("image_cache"))
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
                        .build()
                }
                val navController = rememberNavController()
                SharedTransitionLayout {
                    NavHost(
                        navController = navController,
                        startDestination = "list",
                    ) {
                        composable("list") {
                            TRView(
                                viewModel = viewModel,
                                threadId = threadId ?: "",
                                changeState = { bool ->

                                },
                                onImageClick = { name, id ->
                                    navController.navigate("image/$name/$id")
                                },
                                backAction = {
                                    onBackPressedDispatcher.onBackPressed()
                                },
                                imageLoader,
                                animatedVisibilityScope = this,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        }
                        composable(
                            "image/{date}/{name}/{id}", arguments = listOf(
                                navArgument("date") {
                                    type = NavType.StringType
                                },
                                navArgument("name") {
                                    type = NavType.StringType
                                },
                                navArgument("id") {
                                    type = NavType.IntType
                                },
                            )
                        ) {
                            val date = it.arguments?.getString("date") ?: ""
                            val name = it.arguments?.getString("name") ?: ""
                            val id = it.arguments?.getInt("id") ?: 0
                            Log.d("name", date + name)
                            ImageView(
                                imgName = date + "/" + name,
                                replyId = id,
                                quitClick = {
                                    navController.popBackStack()
                                },
                                imageLoader = imageLoader,
                                animatedVisibilityScope = this,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        }
                    }
                }
            }
        }
    }
}