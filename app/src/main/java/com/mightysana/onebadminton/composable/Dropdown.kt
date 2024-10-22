package com.mightysana.onebadminton.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.isOdd

@Composable
fun OneDropdownMenu(
    modifier: Modifier = Modifier,
    selected: String = "",
    options: List<String>,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onExpandRequest: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = { onExpandRequest() }
    ) {
        Text(selected, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    DropdownMenu(
        modifier = modifier.clip(MaterialTheme.shapes.large),
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        val oddBackground = MaterialTheme.colorScheme.surfaceVariant
        val evenBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

        options.forEachIndexed { index, it ->
            DropdownMenuItem(
                modifier = Modifier.background(if(index.isOdd()) oddBackground else evenBackground),
                text = { Text(it) },
                onClick = {
                    onOptionSelected(it)
                    onDismiss()
                }
            )
        }
    }
}