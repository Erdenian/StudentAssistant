package ru.erdenian.studentassistant.uikit.utils

import android.text.InputType
import android.text.TextWatcher
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

internal fun EditText.update(singleLine: Boolean, keyboardOptions: KeyboardOptions, textWatcher: TextWatcher) {
    fun hasFlag(bits: Int, flag: Int) = (bits and flag) == flag

    // Input type setting causes TextWatcher to be called, so we remove watcher for now
    removeTextChangedListener(textWatcher)

    var imeOptions = when (keyboardOptions.imeAction) {
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

    var inputType = when (keyboardOptions.keyboardType) {
        KeyboardType.Text -> InputType.TYPE_CLASS_TEXT
        KeyboardType.Ascii -> {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_FORCE_ASCII
            InputType.TYPE_CLASS_TEXT
        }
        KeyboardType.Number -> InputType.TYPE_CLASS_NUMBER
        KeyboardType.Phone -> InputType.TYPE_CLASS_PHONE
        KeyboardType.Uri -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_URI
        KeyboardType.Email -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        KeyboardType.Password -> InputType.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        KeyboardType.NumberPassword -> InputType.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
        else -> error("Invalid Keyboard Type")
    }

    if (!singleLine && hasFlag(inputType, InputType.TYPE_CLASS_TEXT)) {
        inputType = inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE

        if (keyboardOptions.imeAction == ImeAction.Default) {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
    }

    if (hasFlag(inputType, InputType.TYPE_CLASS_TEXT)) {
        when (keyboardOptions.capitalization) {
            KeyboardCapitalization.Characters -> inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            KeyboardCapitalization.Words -> inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            KeyboardCapitalization.Sentences -> inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            else -> Unit
        }

        if (keyboardOptions.autoCorrect) inputType = inputType or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
    }

    imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_FULLSCREEN

    if (this.imeOptions != imeOptions) this.imeOptions = imeOptions
    if (this.inputType != inputType) this.inputType = inputType // This line causes TextWatcher to be called

    // Here we return TextWatcher back
    addTextChangedListener(textWatcher)
}
