@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import com.dech53.dao_yu.views.ThreadInfoView

class ThreadAndReplyView : ComponentActivity() {
    private val viewModel: ThreadInfoView_ViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val threadId = intent.getStringExtra("threadId")
        setContent {
            Dao_yuTheme {
                ThreadAndReplyView(threadId = threadId!!, viewModel = viewModel, onFinish = {
                    onBackPressedDispatcher.onBackPressed()
                })
            }
        }
    }
}


@Composable
fun ThreadAndReplyView(
    threadId: String,
    viewModel: ThreadInfoView_ViewModel,
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(elevation = 10.dp),
                title = {
                    Text(text = "No." + threadId)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onFinish()
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
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ThreadInfoView(
                threadId = threadId,
                viewModel = viewModel,
            )
        }
    }
}