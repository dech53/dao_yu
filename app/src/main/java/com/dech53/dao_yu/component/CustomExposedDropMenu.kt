package com.dech53.dao_yu.component

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//后续还要改，直接用textfield太几把丑了
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomExposedDropMenu(itemList: List<String>) {
    var dropMenuExpanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(itemList[0]) }
    ExposedDropdownMenuBox(
        expanded = dropMenuExpanded,
        onExpandedChange = { dropMenuExpanded = !dropMenuExpanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropMenuExpanded)
            },
            modifier = Modifier
                .width(140.dp)
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = dropMenuExpanded,
            onDismissRequest = { dropMenuExpanded = false }
        ) {
            itemList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontSize = 7.sp) },
                    onClick = {
                        selected = item
                        dropMenuExpanded = false
                    }
                )
            }
        }
    }
}