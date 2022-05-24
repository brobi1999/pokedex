package hu.bme.aut.pokedex.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.DialogAuthBinding


@AndroidEntryPoint
class RegisterDialogFragment: DialogFragment() {

    companion object {

        const val TAG = "RegisterDialog"

        private const val KEY_NAME = "KEY_NAME"
        private const val KEY_FAV_TYPES = "KEY_FAV_TYPES"

        fun newInstance(name: String, favTypes: ArrayList<Int>): RegisterDialogFragment {
            val args = Bundle()
            args.putString(KEY_NAME, name)
            args.putIntegerArrayList(KEY_FAV_TYPES, favTypes)
            val fragment = RegisterDialogFragment()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isRegLoading.observe(this) { newIsLoading ->
            if (newIsLoading)
                binding.progressBarCyclic.visibility = View.VISIBLE
            else
                binding.progressBarCyclic.visibility = View.GONE
        }

        viewModel.regError.observe(this) { newError ->
            if(newError != null)
                viewModel.errorReceived()
            when (newError) {
                null -> {
                    clearErrors()
                }
                is FirebaseAuthWeakPasswordException -> {
                    binding.tilEmail.error = null
                    binding.tilPass.error = getString(R.string.password_min_length)
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    binding.tilEmail.error = getString(R.string.malformed_email)
                    binding.tilPass.error = null
                }
                else -> {
                    clearErrors()
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { newSuccess ->
            if(newSuccess )
                dismiss()
        }

        binding.btnAuth.setOnClickListener {
            viewModel.register(
                arguments?.getString(KEY_NAME) ?: "",
                arguments?.getIntegerArrayList(KEY_FAV_TYPES) ?: arrayListOf(),
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