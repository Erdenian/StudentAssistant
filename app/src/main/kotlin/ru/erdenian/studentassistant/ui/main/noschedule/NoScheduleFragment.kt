package ru.erdenian.studentassistant.ui.main.noschedule

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.requireViewByIdCompat

class NoScheduleFragment : Fragment(R.layout.fragment_no_schedule) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.requireViewByIdCompat<Button>(R.id.fns_download_schedule).setOnClickListener {
            requireContext().toast(R.string.nsf_download)
        }
        view.requireViewByIdCompat<Button>(R.id.fns_create_schedule).setOnClickListener {
            requireContext().startActivity<SemesterEditorActivity>()
        }
    }
}
