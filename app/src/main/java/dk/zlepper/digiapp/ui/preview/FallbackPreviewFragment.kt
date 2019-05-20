package dk.zlepper.digiapp.ui.preview


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.zlepper.digiapp.R

/**
 * A simple [Fragment] subclass.
 *
 */
class FallbackPreviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fallback_preview, container, false)
    }


}
