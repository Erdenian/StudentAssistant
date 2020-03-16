package ru.erdenian.studentassistant.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.os.bundleOf

fun Context.attr(@AttrRes attribute: Int) = TypedValue().also { outValue ->
    require(theme.resolveAttribute(attribute, outValue, true)) {
        "Failed to resolve attribute: $attribute"
    }
}

@ColorInt
fun Context.colorAttr(@AttrRes attribute: Int) = attr(attribute).apply {
    require(type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT) {
        "Attribute value type is not color: $attribute"
    }
}.data

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
    startActivity(Intent(this, T::class.java).apply { putExtras(bundleOf(*params)) })

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, Any?>
) = startActivityForResult(
    Intent(this, T::class.java).apply { putExtras(bundleOf(*params)) }, requestCode
)

fun Context.toast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, resId, length).show()

fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, text, length).show()
