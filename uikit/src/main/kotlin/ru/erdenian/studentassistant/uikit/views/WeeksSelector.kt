package ru.erdenian.studentassistant.uikit.views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ru.erdenian.studentassistant.uikit.R
import ru.erdenian.studentassistant.uikit.style.AppIcons
import ru.erdenian.studentassistant.uikit.style.AppTheme

/**
 * View для выбора недель для повторения пары.
 */
@Composable
fun WeeksSelector(
    weeks: List<Boolean>,
    onWeeksChange: (weeks: List<Boolean>) -> Unit,
    modifier: Modifier = Modifier
) {
    val repeatVariants = stringArrayResource(R.array.repeat_variants).toList()
    val weeksVariants = listOf(
        listOf(true),
        listOf(true, false),
        listOf(false, true),
        listOf(true, false, false, false),
        listOf(false, true, false, false),
        listOf(false, false, true, false),
        listOf(false, false, false, true)
    )

    var selectedRepeatVariantIndex by rememberSaveable {
        val index = weeksVariants.indexOf(weeks).takeIf { it >= 0 } ?: weeksVariants.size
        mutableStateOf(index)
    }
    var repeatVariantsExpanded by remember { mutableStateOf(false) }

    val isCustomEnabled = (selectedRepeatVariantIndex >= weeksVariants.size)

    WeeksSelectorView(
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
        onWeekClick = { index, checked ->
            val mutableWeeks = weeks.toBooleanArray()
            mutableWeeks[index] = checked
            onWeeksChange(mutableWeeks.toList())
        },
        onMinusClick = { onWeeksChange(weeks.dropLast(1)) },
        onPlusClick = { onWeeksChange(weeks + false) },
        isMinusEnabled = (weeks.size > 1) && isCustomEnabled,
        isPlusEnabled = isCustomEnabled,
        isCustomEnabled = isCustomEnabled,
        modifier = modifier
    )
}

@Composable
private fun WeeksSelectorView(
    repeatVariants: List<String>,
    selectedRepeatVariantIndex: Int,
    repeatVariantsExpanded: Boolean,
    onSelectedRepeatVariantClick: () -> Unit,
    onRepeatVariantsDismissRequest: () -> Unit,
    onRepeatVariantClick: (index: Int) -> Unit,
    weeks: List<Boolean>,
    onWeekClick: (index: Int, checked: Boolean) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    isMinusEnabled: Boolean,
    isPlusEnabled: Boolean,
    isCustomEnabled: Boolean,
    modifier: Modifier = Modifier
) = ConstraintLayout(modifier = modifier) {
    val (
        repeatTitle, selectedRepeatVariant, repeatDropdownMenu,
        minusButton, minusDivider,
        checkboxesScroll,
        plusDivider, plusButton
    ) = createRefs()

    val barrier = createBottomBarrier(repeatTitle)
    createHorizontalChain(
        repeatTitle,
        selectedRepeatVariant,
        chainStyle = ChainStyle.Packed(0.0f)
    )

    Text(
        text = stringResource(R.string.ws_variants_title),
        modifier = Modifier
            .constrainAs(repeatTitle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(selectedRepeatVariant.start)
                bottom.linkTo(barrier)
            }
            .padding(
                start = dimensionResource(R.dimen.activity_horizontal_margin),
                end = 8.dp
            )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .constrainAs(selectedRepeatVariant) {
                top.linkTo(parent.top)
                start.linkTo(repeatTitle.end)
                end.linkTo(parent.end)
                bottom.linkTo(barrier)
            }
            .padding(end = dimensionResource(R.dimen.activity_horizontal_margin))
            .clickable(onClick = onSelectedRepeatVariantClick)
    ) {
        Text(
            text = repeatVariants[selectedRepeatVariantIndex],
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Icon(
            imageVector = AppIcons.ArrowDropDown,
            contentDescription = null
        )
    }

    DropdownMenu(
        expanded = repeatVariantsExpanded,
        onDismissRequest = onRepeatVariantsDismissRequest,
        modifier = Modifier
            .constrainAs(repeatDropdownMenu) {
                top.linkTo(selectedRepeatVariant.top)
                start.linkTo(selectedRepeatVariant.start)
                width = Dimension.wrapContent
            }
    ) {
        repeatVariants.forEachIndexed { index, variant ->
            DropdownMenuItem(
                onClick = { onRepeatVariantClick(index) }
            ) {
                Text(text = variant)
            }
        }
    }

    IconButton(
        onClick = onMinusClick,
        modifier = Modifier
            .constrainAs(minusButton) {
                top.linkTo(checkboxesScroll.top)
                start.linkTo(parent.start)
                end.linkTo(minusDivider.start)
                bottom.linkTo(checkboxesScroll.bottom)
            },
        enabled = isMinusEnabled
    ) {
        Icon(
            imageVector = AppIcons.Remove,
            contentDescription = null
        )
    }

    Divider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(1.dp)
            .constrainAs(minusDivider) {
                start.linkTo(minusButton.end)
                top.linkTo(checkboxesScroll.top)
                end.linkTo(checkboxesScroll.start)
                bottom.linkTo(checkboxesScroll.bottom)
                height = Dimension.fillToConstraints
            }
    )

    LazyRow(
        modifier = Modifier
            .constrainAs(checkboxesScroll) {
                top.linkTo(barrier)
                start.linkTo(minusDivider.end)
                end.linkTo(plusDivider.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            }
    ) {
        itemsIndexed(weeks) { index, checked ->
            CheckBoxWithText(
                checked = checked,
                text = (index + 1).toString(),
                onCheckedChange = { onWeekClick(index, it) },
                enabled = isCustomEnabled
            )
        }
    }

    Divider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(1.dp)
            .constrainAs(plusDivider) {
                top.linkTo(checkboxesScroll.top)
                start.linkTo(checkboxesScroll.end)
                bottom.linkTo(checkboxesScroll.bottom)
                end.linkTo(plusButton.start)
                height = Dimension.fillToConstraints
            }
    )

    IconButton(
        onClick = onPlusClick,
        modifier = Modifier
            .constrainAs(plusButton) {
                top.linkTo(checkboxesScroll.top)
                start.linkTo(plusDivider.end)
                end.linkTo(parent.end)
                bottom.linkTo(checkboxesScroll.bottom)
            },
        enabled = isPlusEnabled
    ) {
        Icon(
            imageVector = AppIcons.Add,
            contentDescription = null
        )
    }
}

@Preview(name = "WeeksSelector preview")
@Preview(name = "WeeksSelector preview (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WeeksSelectorPreview() = AppTheme {
    WeeksSelectorView(
        repeatVariants = listOf("По чётным", "По нечётным", "Своё"),
        selectedRepeatVariantIndex = 2,
        repeatVariantsExpanded = false,
        onSelectedRepeatVariantClick = {},
        onRepeatVariantsDismissRequest = {},
        onRepeatVariantClick = {},
        weeks = listOf(true, false, true),
        onWeekClick = { _, _ -> },
        onMinusClick = {},
        onPlusClick = {},
        isMinusEnabled = true,
        isPlusEnabled = true,
        isCustomEnabled = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun WeeksSelectorPreviewLongRepeatVariant() = AppTheme {
    WeeksSelectorView(
        repeatVariants = listOf("По чётным чётным чётным чётным чётным чётным чётным"),
        selectedRepeatVariantIndex = 0,
        repeatVariantsExpanded = false,
        onSelectedRepeatVariantClick = {},
        onRepeatVariantsDismissRequest = {},
        onRepeatVariantClick = {},
        weeks = listOf(true, false, true, false, true, false, true, false, true, false, true, false),
        onWeekClick = { _, _ -> },
        onMinusClick = {},
        onPlusClick = {},
        isMinusEnabled = true,
        isPlusEnabled = true,
        isCustomEnabled = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CheckBoxWithText(
    checked: Boolean,
    text: String,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
        .run { if (enabled) clickable { onCheckedChange?.invoke(!checked) } else this }
        .padding(4.dp)
) {
    Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
    Text(text = text, maxLines = 1)
}

@Preview(name = "Short text")
@Composable
private fun CheckBoxWithTextPreviewShort() = AppTheme {
    CheckBoxWithText(true, "1", null)
}

@Preview(name = "Medium text")
@Composable
private fun CheckBoxWithTextPreviewMedium() = AppTheme {
    CheckBoxWithText(true, "Text", null)
}

@Preview(name = "Long text")
@Composable
private fun CheckBoxWithTextPreviewLong() = AppTheme {
    CheckBoxWithText(true, "Long text", null)
}

@Preview(name = "Disabled")
@Composable
private fun CheckBoxWithTextPreviewDisabled() = AppTheme {
    CheckBoxWithText(true, "Disabled", null, enabled = false)
}

@Preview(name = "Dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckBoxWithTextPreviewDark() = AppTheme {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
        CheckBoxWithText(
            true,
            "Dark",
            null,
            modifier = Modifier.background(MaterialTheme.colors.background)
        )
    }
}
