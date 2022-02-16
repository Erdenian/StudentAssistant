package ru.erdenian.studentassistant.uikit.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.erdenian.studentassistant.style.AppIcons

@Composable
fun StartEndRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    contentStart: @Composable () -> Unit,
    contentEnd: @Composable () -> Unit
) {
    fun Arrangement.Horizontal.inverted() = when (this) {
        Arrangement.Start -> Arrangement.End
        Arrangement.End -> Arrangement.Start
        else -> this
    }

    fun LayoutDirection.inverted() = when (this) {
        LayoutDirection.Ltr -> LayoutDirection.Rtl
        LayoutDirection.Rtl -> LayoutDirection.Ltr
    }

    @Composable
    fun provideInvertedLayoutDirection() = LocalLayoutDirection provides LocalLayoutDirection.current.inverted()

    // Invert Row's layout direction to make contentEnd always visible
    CompositionLocalProvider(provideInvertedLayoutDirection()) {
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement.inverted(),
            verticalAlignment = verticalAlignment
        ) {
            // Invert layout direction back
            CompositionLocalProvider(provideInvertedLayoutDirection()) {
                contentEnd()
                contentStart()
            }
        }
    }
}

@Preview
@Composable
private fun StartEndRowPreview() {
    Column(
        modifier = Modifier.width(100.dp)
    ) {
        StartEndRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            contentStart = {
                Text(
                    text = "Start",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            contentEnd = {
                Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
            }
        )
        StartEndRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
            contentStart = {
                Text(
                    text = "Center",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            contentEnd = {
                Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
            }
        )
        StartEndRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
            contentStart = {
                Text(
                    text = "End",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            contentEnd = {
                Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
            }
        )
        StartEndRow(
            verticalAlignment = Alignment.Bottom,
            contentStart = {
                Text(
                    text = "Very very long text",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            contentEnd = {
                Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
            }
        )
    }
}

@Preview
@Composable
private fun DefaultRowPreview() {
    Column(
        modifier = Modifier.width(100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Start",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Center",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "End",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
        }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Very very long text",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(imageVector = AppIcons.LocationOn, contentDescription = null)
        }
    }
}
