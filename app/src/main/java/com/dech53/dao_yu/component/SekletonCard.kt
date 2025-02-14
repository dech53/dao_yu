package com.dech53.dao_yu.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.ui.theme.capsuleShape
import com.dech53.dao_yu.ui.theme.shimmerEffect

@Composable
fun SkeletonCard(){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .padding(horizontal = 13.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 12.dp)
                            .clip(capsuleShape)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 12.dp)
                            .clip(capsuleShape)
                            .shimmerEffect()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 20.dp, height = 12.dp)
                        .clip(capsuleShape)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 18.dp)
                    .clip(capsuleShape)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(capsuleShape)
                    .fillMaxWidth()
                    .height(18.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(capsuleShape)
                    .fillMaxWidth()
                    .height(18.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(capsuleShape)
                    .fillMaxWidth()
                    .height(18.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
    }
}