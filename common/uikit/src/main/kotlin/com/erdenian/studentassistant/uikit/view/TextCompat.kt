package com.erdenian.studentassistant.uikit.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.erdenian.studentassistant.style.AppTheme

/**
 * Text that supports setting [TextAlign.End] combined with [TextOverflow.Clip].
 *
 * @see <a href="https://issuetracker.google.com/issues/183835422">Issue on IssueTracker</a>
 */
@Composable
fun TextCompat(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) = Box(
    contentAlignment = when (textAlign) {
        TextAlign.Left -> AbsoluteAlignment.TopLeft
        TextAlign.Right -> AbsoluteAlignment.TopLeft
        TextAlign.Center -> Alignment.TopCenter
        TextAlign.Start -> Alignment.TopStart
        TextAlign.End -> Alignment.TopEnd
        else -> Alignment.TopStart
    },
    modifier = modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            // Forward all parameters except textAlign and modifier
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
}

@Preview
@Composable
private fun TextCompatPreview() = AppTheme {
    Column(modifier = Modifier.width(100.dp)) {
        TextCompat(
            text = "Very very very very very very very very very very long text",
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            text = "Very very very very very very very very very very long text",
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}
