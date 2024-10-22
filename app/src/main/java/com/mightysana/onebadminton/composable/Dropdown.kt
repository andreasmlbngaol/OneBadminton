package com.mightysana.onebadminton.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
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
        modifier = modifier.clip(MaterialTheme.shapes.large).fillMaxWidth().fillMaxHeight(0.5f),
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        val oddAlpha = 1f
        val evenAlpha = 0.5f

        val oddBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = oddAlpha)
        val evenBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = evenAlpha)

        val unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant

        val selectedBackground = MaterialTheme.colorScheme.primary
        val selectedTextColor = MaterialTheme.colorScheme.onPrimary

        options.forEachIndexed { index, it ->
            val background = if(selected == it) selectedBackground else (if(index.isOdd()) oddBackground else evenBackground)
            val textColor = if(selected == it) selectedTextColor else unselectedTextColor
            DropdownMenuItem(
                modifier = Modifier.background(background),
                text = {
                    Text(
                        text = it,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onOptionSelected(it)
                    onDismiss()
                }
            )
        }
    }
}