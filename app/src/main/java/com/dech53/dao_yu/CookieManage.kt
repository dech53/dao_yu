@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.static.CookieState
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.viewmodels.CookieViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONObject
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.dao.CookieEvent


class CookieManage : ComponentActivity() {
    private val qrResult = mutableStateOf("")
    private val cookie = mutableStateOf(Cookie(0, "", ""))


    private val viewModel by viewModels<CookieViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CookieViewModel(db.cookieDao) as T
                }
            }
        }
    )

    private val db by lazy {
        CookieDatabase.getDatabase(applicationContext)
    }

    //拉起相机
    private val qrCodeLanuncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {

        } else {
            qrResult.value = result.contents
            val jsonObject = JSONObject(qrResult.value)
            cookie.value = Cookie(
                 0,
                jsonObject.getString("cookie"),
                jsonObject.getString("name")
            )
            viewModel.onEvent(CookieEvent.SetCookie(cookie.value.cookie))
            viewModel.onEvent(CookieEvent.SetIsToVerify(cookie.value.isToVerify))
            viewModel.onEvent(CookieEvent.SetName(cookie.value.name))
            viewModel.onEvent(CookieEvent.SaveCookie)
            Log.d(this@CookieManage.toString(), "qrCodeLauncher: ${jsonObject.getString("name")}")
        }
    }

    //相机配置
    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("扫描饼干二维码")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        qrCodeLanuncher.launch(options)
    }

    //请求权限
    private val requesPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showCamera()
            }
        }

    //检查权限
    private fun checkCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "请允许相机权限", Toast.LENGTH_SHORT).show()
        } else {
            requesPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dao_yuTheme {
                LaunchedEffect(Unit) {
                    viewModel.onEvent(CookieEvent.GetCookies)
                }
                val state by viewModel.state.collectAsState()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "饼干管理") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        onBackPressedDispatcher.onBackPressed()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(
                                            id = R.drawable.baseline_arrow_back_24
                                        ),
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    CookiePage(padding = innerPadding, addAction = {
                        checkCameraPermission(this@CookieManage)
                    }, state = state, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

@Composable
fun CookiePage(
    padding: PaddingValues,
    addAction: () -> Unit,
    state: CookieState,
    onEvent: (CookieEvent) -> Unit
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.cookies.forEachIndexed { index, cookie ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                        .clickable {
                            onEvent(CookieEvent.SetVerifyCookie(cookie.cookie))
                        }
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 3.dp,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                        ) {
                            Row {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = if (cookie.isToVerify == 1) R.drawable.baseline_cookie_24 else R.drawable.outline_cookie_24),
                                    contentDescription = "cookie is selected"
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = cookie.name)
                                Spacer(modifier = Modifier.width(5.dp))
                                if(cookie.isToVerify == 1)
                                    Text(text = "鉴权", color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                onEvent(CookieEvent.DeleteCookie(cookie.cookie))
                                Log.d("delete button test", cookie.name)
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_forever_24),
                                    contentDescription = "delete cookie"
                                )
                            }
                        }
                    }
                }
            }
            Button(onClick = { addAction() }) {
                Row() {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_add_24),
                        contentDescription = ""
                    )
                    Text(text = "添加饼干")
                }
            }
        }
    }
}