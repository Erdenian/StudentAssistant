package ru.erdenian.studentassistant.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import ru.erdenian.studentassistant.uikit.R

/**
 * [RecyclerView] с возможностью отображать контекстное меню для элементов списка.
 *
 * @author Ilya Solovyov
 * @since 0.3.0
 */
class ContextMenuRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @SuppressLint("PrivateResource")
    defStyleAttr: Int = R.attr.recyclerViewStyle
) : RecyclerView(context, attrs, defStyleAttr) {

    private var contextMenuInfo: AdapterView.AdapterContextMenuInfo? = null

    override fun getContextMenuInfo() = checkNotNull(contextMenuInfo)

    override fun showContextMenuForChild(originalView: View): Boolean {
        val position = getChildAdapterPosition(originalView).takeIf { it >= 0 } ?: return false
        val id = adapter?.getItemId(position) ?: return false
        contextMenuInfo = AdapterView.AdapterContextMenuInfo(originalView, position, id)
        return super.showContextMenuForChild(originalView)
    }

    override fun showContextMenuForChild(originalView: View, x: Float, y: Float): Boolean {
        val position = getChildAdapterPosition(originalView).takeIf { it >= 0 } ?: return false
        val id = adapter?.getItemId(position) ?: return false
        contextMenuInfo = AdapterView.AdapterContextMenuInfo(originalView, position, id)
        return super.showContextMenuForChild(originalView, x, y)
    }
}
