package ru.erdenian.studentassistant.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

class ContextMenuRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
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
