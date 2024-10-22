package com.mightysana.onebadminton.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.isEven

@Composable
fun Table(
    columnName: List<TableColumn>,
    data: List<List<TableData>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                columnName.forEach { column ->
                    Text(
                        text = column.name,
                        modifier = Modifier
                            .weight(column.weight) // Menggunakan weight untuk kolom header
                            .padding(8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = column.textAlign,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        var it = 0
        items(data) { rowData ->
            Row(
                modifier = Modifier.fillMaxWidth().background(if(it.isEven()) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowData.forEach { cellData ->
                    Text(
                        fontWeight = cellData.fontWeight,
                        textAlign = cellData.textAlign,
                        text = cellData.content,
                        modifier = Modifier
                            .weight(cellData.weight) // Menggunakan weight untuk sel data
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            it += 1
        }
    }
}

open class TableCell(
    open val name: String = "",
    open val weight: Float = 1f
)

class TableColumn(
    override val name: String,
    override val weight: Float = 1f,
    val textAlign: TextAlign = TextAlign.Center
): TableCell()

class TableData(
    val content: String,
    override val weight: Float = 1f,
    val textAlign: TextAlign = TextAlign.Start,
    val fontWeight: FontWeight = FontWeight.Normal
): TableCell()