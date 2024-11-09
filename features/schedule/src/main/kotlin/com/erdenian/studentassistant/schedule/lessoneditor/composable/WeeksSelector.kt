package com.erdenian.studentassistant.schedule.lessoneditor.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.strings.RA
import com.erdenian.studentassistant.strings.RS
import com.erdenian.studentassistant.style.AppIcons
import com.erdenian.studentassistant.style.AppTheme
import com.erdenian.studentassistant.style.dimensions

/**
 * View для выбора недель для повторения пары.
 */
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
    val simpleRepeatVariants = remember { repeatVariants.take(3) }

    var selectedRepeatVariantIndex by rememberSaveable {
        val index = weeksVariants.indexOf(weeks).takeIf { it >= 0 } ?: weeksVariants.size
        mutableIntStateOf(index)
    }
    var repeatVariantsExpanded by remember { mutableStateOf(false) }

    val isCustomEnabled = (selectedRepeatVariantIndex >= weeksVariants.size)

    if (isAdvancedMode || (selectedRepeatVariantIndex !in simpleRepeatVariants.indices)) {
        WeeksSelectorAdvancedContent(
            repeatVariants = repeatVariants,
            selectedRepeatVariantIndex = selectedRepeatVariantIndex,
            repeatVariantsExpanded = repeatVariantsExpanded,
            onSelectedRepeatVariantClick = { repeatVariantsExpanded = true },
            onRepeatVariantsDismissRequest = { repeatVariantsExpanded = false },
            onRepeatVariantClick = { index ->
                selectedRepeatVariantIndex = index
                onWeeksChange(weeksVariants.getOrElse(index) { weeks })
                repeatVariantsExpanded = false
            },
            weeks = weeks,
            onWeekCheckedChange = { index, checked ->
                val mutableWeeks = weeks.toBooleanArray()
                mutableWeeks[index] = checked
                onWeeksChange(mutableWeeks.toList())
            },
            onMinusClick = { onWeeksChange(weeks.dropLast(1)) },
            onPlusClick = { onWeeksChange(weeks + false) },
            isMinusEnabled = (weeks.size > 1) && isCustomEnabled,
            isCustomEnabled = isCustomEnabled,
            enabled = enabled,
            modifier = modifier,
        )
    } else {
        WeeksSelectorSimpleContent(
            repeatVariants = simpleRepeatVariants,
            selectedRepeatVariantIndex = selectedRepeatVariantIndex,
            repeatVariantsExpanded = repeatVariantsExpanded,
            onSelectedRepeatVariantClick = { repeatVariantsExpanded = true },
            onRepeatVariantsDismissRequest = { repeatVariantsExpanded = false },
            onRepeatVariantClick = { index ->
                selectedRepeatVariantIndex = index
                onWeeksChange(weeksVariants.getOrElse(index) { weeks })
                repeatVariantsExpanded = false
            },
            enabled = enabled,
            modifier = modifier,
        )
    }
}

@Composable
private fun WeeksSelectorSimpleContent(
    repeatVariants: List<String>,
    selectedRepeatVariantIndex: Int,
    repeatVariantsExpanded: Boolean,
    onSelectedRepeatVariantClick: () -> Unit,
    onRepeatVariantsDismissRequest: () -> Unit,
    onRepeatVariantClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = Column(modifier = modifier) {
    Row(
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = stringResource(RS.ws_variants_title))

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal)
                .clickable(onClick = onSelectedRepeatVariantClick, enabled = enabled),
        ) {
            Text(
                text = repeatVariants[selectedRepeatVariantIndex],
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1.0f, false),
            )

            Icon(
                imageVector = AppIcons.ArrowDropDown,
                contentDescription = null,
            )

            DropdownMenu(
                expanded = repeatVariantsExpanded,
                onDismissRequest = onRepeatVariantsDismissRequest,
            ) {
                repeatVariants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = { Text(text = variant) },
                        onClick = { onRepeatVariantClick(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeksSelectorAdvancedContent(
    repeatVariants: List<String>,
    selectedRepeatVariantIndex: Int,
    repeatVariantsExpanded: Boolean,
    onSelectedRepeatVariantClick: () -> Unit,
    onRepeatVariantsDismissRequest: () -> Unit,
    onRepeatVariantClick: (index: Int) -> Unit,
    weeks: List<Boolean>,
    onWeekCheckedChange: (index: Int, checked: Boolean) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    isMinusEnabled: Boolean,
    isCustomEnabled: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = Column(modifier = modifier) {
    Row(
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = stringResource(RS.ws_variants_title))

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(horizontal = MaterialTheme.dimensions.screenPaddingHorizontal)
                .clickable(onClick = onSelectedRepeatVariantClick, enabled = enabled),
        ) {
            Text(
                text = repeatVariants[selectedRepeatVariantIndex],
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1.0f, false),
            )

            Icon(
                imageVector = AppIcons.ArrowDropDown,
                contentDescription = null,
            )

            DropdownMenu(
                expanded = repeatVariantsExpanded,
                onDismissRequest = onRepeatVariantsDismissRequest,
            ) {
                repeatVariants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = { Text(text = variant) },
                        onClick = { onRepeatVariantClick(index) },
                    )
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp),
    ) {
        IconButton(
            onClick = onMinusClick,
            enabled = (isMinusEnabled && enabled),
        ) {
            Icon(
                imageVector = AppIcons.Remove,
                contentDescription = null,
            )
        }

        VerticalDivider(modifier = Modifier.height(40.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.0f),
        ) {
            itemsIndexed(weeks) { index, checked ->
                CheckBoxWithText(
                    checked = checked,
                    text = (index + 1).toString(),
                    onCheckedChange = { onWeekCheckedChange(index, it) },
                    enabled = (isCustomEnabled && enabled),
                )
            }
        }

        VerticalDivider(modifier = Modifier.height(40.dp))

        IconButton(
            onClick = onPlusClick,
            enabled = (isCustomEnabled && enabled),
        ) {
            Icon(
                imageVector = AppIcons.Add,
                contentDescription = null,
            )
        }
    }
}

@Preview(name = "WeeksSelector simple preview", group = "WeeksSelector", showBackground = true)
@Preview(
    name = "WeeksSelector simple preview (dark)",
    group = "WeeksSelector",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun WeeksSelectorSimplePreview() = AppTheme {
    WeeksSelectorSimpleContent(
        repeatVariants = listOf("Каждую неделю", "По чётным", "По нечётным"),
        selectedRepeatVariantIndex = 2,
        repeatVariantsExpanded = false,
        onSelectedRepeatVariantClick = {},
        onRepeatVariantsDismissRequest = {},
        onRepeatVariantClick = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "WeeksSelector preview", group = "WeeksSelector", showBackground = true)
@Preview(
    name = "WeeksSelector preview (dark)",
    group = "WeeksSelector",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun WeeksSelectorPreview() = AppTheme {
    WeeksSelectorAdvancedContent(
        repeatVariants = listOf("По чётным", "По нечётным", "Своё"),
        selectedRepeatVariantIndex = 2,
        repeatVariantsExpanded = false,
        onSelectedRepeatVariantClick = {},
        onRepeatVariantsDismissRequest = {},
        onRepeatVariantClick = {},
        weeks = listOf(true, false, true),
        onWeekCheckedChange = { _, _ -> },
        onMinusClick = {},
        onPlusClick = {},
        isMinusEnabled = true,
        isCustomEnabled = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(group = "WeeksSelector", showBackground = true)
@Composable
private fun WeeksSelectorLongRepeatVariantPreview() = AppTheme {
    WeeksSelectorAdvancedContent(
        repeatVariants = listOf("По чётным чётным чётным чётным чётным чётным чётным"),
        selectedRepeatVariantIndex = 0,
        repeatVariantsExpanded = false,
        onSelectedRepeatVariantClick = {},
        onRepeatVariantsDismissRequest = {},
        onRepeatVariantClick = {},
        weeks = listOf(true, false, true, false, true, false, true, false, true, false, true, false),
        onWeekCheckedChange = { _, _ -> },
        onMinusClick = {},
        onPlusClick = {},
        isMinusEnabled = true,
        isCustomEnabled = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CheckBoxWithText(
    checked: Boolean,
    text: String,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier.run { if (enabled) clickable { onCheckedChange?.invoke(!checked) } else this },
) {
    Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
    Text(text = text, maxLines = 1)
}

@Preview(name = "Short text", group = "CheckBoxWithText", showBackground = true)
@Composable
private fun CheckBoxWithTextShortPreview() = AppTheme {
    CheckBoxWithText(true, "1", null)
}

@Preview(name = "Medium text", group = "CheckBoxWithText", showBackground = true)
@Composable
private fun CheckBoxWithTextMediumPreview() = AppTheme {
    CheckBoxWithText(true, "Text", null)
}

@Preview(name = "Long text", group = "CheckBoxWithText", showBackground = true)
@Composable
private fun CheckBoxWithTextLongPreview() = AppTheme {
    CheckBoxWithText(true, "Long text", null)
}

@Preview(name = "Disabled", group = "CheckBoxWithText", showBackground = true)
@Composable
private fun CheckBoxWithTextDisabledPreview() = AppTheme {
    CheckBoxWithText(checked = true, text = "Disabled", onCheckedChange = null, enabled = false)
}

@Preview(
    name = "Dark theme", group = "CheckBoxWithText", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CheckBoxWithTextDarkPreview() = AppTheme {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
        CheckBoxWithText(
            checked = true,
            text = "Dark",
            onCheckedChange = null,
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}
