package ru.erdenian.studentassistant.schedule.lessoneditor.composable

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.strings.RA
import ru.erdenian.studentassistant.strings.RS
import ru.erdenian.studentassistant.style.AppIcons
import ru.erdenian.studentassistant.style.AppTheme

/**
 * Компонент для выбора недель повторения занятия.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeeksSelector(
    weeks: List<Boolean>,
    onWeeksChange: (weeks: List<Boolean>) -> Unit,
    modifier: Modifier = Modifier,
    isAdvancedMode: Boolean = true,
    enabled: Boolean = true,
) {
    val repeatVariants = stringArrayResource(RA.repeat_variants).toList()
    val weeksVariants = remember {
        listOf(
            listOf(true),
            listOf(true, false),
            listOf(false, true),
            listOf(true, false, false, false),
            listOf(false, true, false, false),
            listOf(false, false, true, false),
            listOf(false, false, false, true),
        )
    }

    val selectedRepeatVariantIndex = remember(weeks) {
        val index = weeksVariants.indexOf(weeks)
        if (index >= 0) index else weeksVariants.size
    }

    val visibleDropdownIndices by remember(isAdvancedMode, selectedRepeatVariantIndex) {
        derivedStateOf {
            val allIndices = repeatVariants.indices
            if (isAdvancedMode) {
                allIndices.toList()
            } else {
                (listOf(0, 1, 2) + selectedRepeatVariantIndex)
                    .filter { it in allIndices }
                    .distinct()
                    .sorted()
            }
        }
    }

    val showCustomSettings = isAdvancedMode || (selectedRepeatVariantIndex > 2)

    Column(
        modifier = modifier,
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded },
        ) {
            OutlinedTextField(
                value = repeatVariants.getOrElse(selectedRepeatVariantIndex) { repeatVariants.last() },
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(RS.ws_variants_title)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                enabled = enabled,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                visibleDropdownIndices.forEach { index ->
                    DropdownMenuItem(
                        text = { Text(text = repeatVariants[index]) },
                        onClick = {
                            if (index < weeksVariants.size) {
                                onWeeksChange(weeksVariants[index])
                            }
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showCustomSettings,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(RS.ws_cycle_length, weeks.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onWeeksChange(weeks.dropLast(1)) },
                            enabled = enabled && weeks.size > 1,
                        ) {
                            Icon(imageVector = AppIcons.Remove, contentDescription = null)
                        }

                        IconButton(
                            onClick = { onWeeksChange(weeks + false) },
                            enabled = enabled,
                        ) {
                            Icon(imageVector = AppIcons.Add, contentDescription = null)
                        }
                    }
                }

                val listState = rememberLazyListState()

                // Анимация прозрачности маски по краям:
                // 0f - край прозрачный (контент скрыт/затухает)
                // 1f - край непрозрачный (контент виден полностью)
                // Если можно скроллить назад, левый край должен быть прозрачным (0f), иначе непрозрачным (1f).
                val startAlpha by animateFloatAsState(
                    targetValue = if (listState.canScrollBackward) 0f else 1f,
                    label = "StartFade",
                )
                val endAlpha by animateFloatAsState(
                    targetValue = if (listState.canScrollForward) 0f else 1f,
                    label = "EndFade",
                )

                val fadeWidthPx = with(LocalDensity.current) { 16.dp.toPx() }

                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        // Используем Offscreen слой для применения BlendMode.DstIn к контенту LazyRow
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()

                            // Формируем градиентную маску.
                            // BlendMode.DstIn сохраняет Destination (контент) там, где Source (кисть) непрозрачен.
                            // Где Source прозрачен, контент исчезает.
                            //
                            // Точки градиента:
                            // 0: startAlpha (0 или 1) - начало списка
                            // fadeWidth: 1 (полностью виден)
                            // width - fadeWidth: 1 (полностью виден)
                            // width: endAlpha (0 или 1) - конец списка

                            if (size.width > 0) {
                                val startFraction = (fadeWidthPx / size.width).coerceAtMost(0.5f)
                                val endFraction = (1f - fadeWidthPx / size.width).coerceAtLeast(0.5f)

                                val fadeBrush = Brush.horizontalGradient(
                                    0.0f to Color.Black.copy(alpha = startAlpha),
                                    startFraction to Color.Black,
                                    endFraction to Color.Black,
                                    1.0f to Color.Black.copy(alpha = endAlpha),
                                )

                                drawRect(brush = fadeBrush, blendMode = BlendMode.DstIn)
                            }
                        },
                ) {
                    itemsIndexed(weeks) { index, checked ->
                        FilterChip(
                            selected = checked,
                            onClick = {
                                val mutableWeeks = weeks.toBooleanArray()
                                mutableWeeks[index] = !checked
                                onWeeksChange(mutableWeeks.toList())
                            },
                            label = { Text((index + 1).toString()) },
                            enabled = enabled,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Simple Mode")
@Preview(name = "Simple Mode (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeeksSelectorSimplePreview() = AppTheme {
    WeeksSelector(
        weeks = listOf(true, false),
        onWeeksChange = {},
        isAdvancedMode = false,
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(name = "Advanced Mode")
@Preview(name = "Advanced Mode (Dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeeksSelectorAdvancedPreview() = AppTheme {
    WeeksSelector(
        weeks = listOf(true, false, true),
        onWeeksChange = {},
        isAdvancedMode = true,
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(name = "Simple Mode with Custom Data")
@Composable
private fun WeeksSelectorSimpleWithCustomDataPreview() = AppTheme {
    WeeksSelector(
        weeks = listOf(true, false, true),
        onWeeksChange = {},
        isAdvancedMode = false,
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(name = "Long Cycle")
@Composable
private fun WeeksSelectorLongCyclePreview() = AppTheme {
    WeeksSelector(
        weeks = List(15) { it % 2 == 0 },
        onWeeksChange = {},
        isAdvancedMode = true,
        modifier = Modifier.padding(16.dp),
    )
}
