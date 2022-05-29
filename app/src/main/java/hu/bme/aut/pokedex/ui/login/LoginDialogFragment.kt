package hu.bme.aut.pokedex.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.DialogAuthBinding

@AndroidEntryPoint
class LoginDialogFragment: DialogFragment() {
    companion object {

        const val TAG = "LoginDialog"

        fun newInstance(): LoginDialogFragment {
            return LoginDialogFragment()
        }

    }

    private var _binding: DialogAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAuthBinding.inflate(inflater, container, false)
        binding.btnAuth.text = getString(R.string.login)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoginLoading.observe(this) { newIsLoading ->
            if (newIsLoading)
                binding.progressBarCyclic.visibility = View.VISIBLE
            else
                binding.progressBarCyclic.visibility = View.GONE
        }

        viewModel.loginError.observe(this) { newError ->
            if(newError != null)
                viewModel.errorReceived()
            when (newError) {
                null -> {
                    clearErrors()
                }
                else -> {
                    clearErrors()
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { newSuccess ->
            if(newSuccess)
                dismiss()
        }

        binding.btnAuth.setOnClickListener {
            viewModel.login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPass.error = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setBackgroundDrawableResource(R.drawable.dialog_background)
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }
}