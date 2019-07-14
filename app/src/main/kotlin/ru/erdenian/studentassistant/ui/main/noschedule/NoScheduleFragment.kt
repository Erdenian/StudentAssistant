package ru.erdenian.studentassistant.ui.main.noschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class NoScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_no_schedule, container, false).apply {
        requireViewByIdCompat<Button>(R.id.fns_download_schedule).setOnClickListener {
            requireContext().toast(R.string.sa_download)
        }
        requireViewByIdCompat<Button>(R.id.fns_create_schedule).setOnClickListener {
            requireContext().startActivity<SemesterEditorActivity>()
        }
    }
}
