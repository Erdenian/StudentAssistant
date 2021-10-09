package ru.erdenian.studentassistant.uikit.utils

import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

private val EmptyKeyboardActionScope = object : KeyboardActionScope {
    override fun defaultKeyboardAction(imeAction: ImeAction) = Unit
}

internal fun createOnEditorActionListener(
    keyboardActions: State<KeyboardActions>
) = TextView.OnEditorActionListener { _, actionId, _ ->
    val event = when (actionId) {
        EditorInfo.IME_ACTION_DONE -> keyboardActions.value.onDone
        EditorInfo.IME_ACTION_GO -> keyboardActions.value.onGo
        EditorInfo.IME_ACTION_NEXT -> keyboardActions.value.onNext
        EditorInfo.IME_ACTION_PREVIOUS -> keyboardActions.value.onPrevious
        EditorInfo.IME_ACTION_SEARCH -> keyboardActions.value.onSearch
        EditorInfo.IME_ACTION_SEND -> keyboardActions.value.onSend
        else -> null
    }

    if (event != null) {
        event(EmptyKeyboardActionScope)
        true
    } else false
}

internal fun EditText.update(singleLine: Boolean, keyboardOptions: KeyboardOptions) {
    fun hasFlag(bits: Int, flag: Int) = (bits and flag) == flag

    this.imeOptions = when (keyboardOptions.imeAction) {
        ImeAction.Default -> if (singleLine) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_UNSPECIFIED
        ImeAction.None -> EditorInfo.IME_ACTION_NONE
        ImeAction.Go -> EditorInfo.IME_ACTION_GO
        ImeAction.Next -> EditorInfo.IME_ACTION_NEXT
        ImeAction.Previous -> EditorInfo.IME_ACTION_PREVIOUS
        ImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
        ImeAction.Send -> EditorInfo.IME_ACTION_SEND
        ImeAction.Done -> EditorInfo.IME_ACTION_DONE
        else -> error("invalid ImeAction")
    }

    when (keyboardOptions.keyboardType) {
        KeyboardType.Text -> this.inputType = InputType.TYPE_CLASS_TEXT
        KeyboardType.Ascii -> {
            this.inputType = InputType.TYPE_CLASS_TEXT
            this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_FORCE_ASCII
        }
        KeyboardType.Number -> this.inputType = InputType.TYPE_CLASS_NUMBER
        KeyboardType.Phone -> this.inputType = InputType.TYPE_CLASS_PHONE
        KeyboardType.Uri -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_URI
        KeyboardType.Email -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        KeyboardType.Password -> this.inputType = InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        KeyboardType.NumberPassword -> this.inputType = InputType.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        else -> error("Invalid Keyboard Type")
    }

    if (!singleLine && hasFlag(this.inputType, InputType.TYPE_CLASS_TEXT)) {
        this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE

        if (keyboardOptions.imeAction == ImeAction.Default) {
            this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
    }

    if (hasFlag(this.inputType, InputType.TYPE_CLASS_TEXT)) {
        when (keyboardOptions.capitalization) {
            KeyboardCapitalization.Characters -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            KeyboardCapitalization.Words -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            KeyboardCapitalization.Sentences -> this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            else -> Unit
        }

        if (keyboardOptions.autoCorrect) this.inputType = this.inputType or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
    }

    this.imeOptions = this.imeOptions or EditorInfo.IME_FLAG_NO_FULLSCREEN
}
