package ru.erdenian.studentassistant.ui.main.noschedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.erdenian.studentassistant.R
import ru.erdenian.studentassistant.databinding.FragmentNoScheduleBinding
import ru.erdenian.studentassistant.ui.semestereditor.SemesterEditorActivity
import ru.erdenian.studentassistant.utils.toast

class NoScheduleFragment : Fragment(R.layout.fragment_no_schedule) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentNoScheduleBinding.bind(view)

        binding.downloadSchedule.setOnClickListener {
            requireContext().toast(R.string.nsf_download)
        }
        binding.createSchedule.setOnClickListener {
            SemesterEditorActivity.start(requireContext())
        }
    }
}
