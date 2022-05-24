package hu.bme.aut.pokedex.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.databinding.DialogAuthBinding

@AndroidEntryPoint
class LoginDialogFragment: DialogFragment() {
    companion object {

        const val TAG = "LoginDialog"

        private const val KEY_EMAIL = "KEY_EMAIL"
        private const val KEY_PASS = "KEY_PASS"

        fun newInstance(email: String, pass: String): LoginDialogFragment {
            val args = Bundle()
            args.putString(KEY_EMAIL, email)
            args.putString(KEY_PASS, pass)
            val fragment = LoginDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: DialogAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAuthBinding.inflate(inflater, container, false)
        return binding.root
    }
}