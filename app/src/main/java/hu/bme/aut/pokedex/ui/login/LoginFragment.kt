package hu.bme.aut.pokedex.ui.login
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.databinding.FragmentLoginBinding
import hu.bme.aut.pokedex.model.ui.PokeType

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.success.observe(viewLifecycleOwner) { newSuccess ->
            if(newSuccess){
                viewModel.successReceived()
                navigateToListFragment()
            }

        }

        binding.btnSaveAccount.setOnClickListener {
            RegisterDialogFragment.newInstance(binding.etName.text.toString(), getFavouriteTypes()).show(parentFragmentManager, RegisterDialogFragment.TAG)
        }
        binding.btnLogin.setOnClickListener {
            LoginDialogFragment.newInstance().show(parentFragmentManager, LoginDialogFragment.TAG)
        }
    }

    private fun getFavouriteTypes(): ArrayList<Int> {
        val array = arrayListOf<Int>()
        if(binding.cbFire.isChecked)
            array.add(PokeType.Fire.ordinal)
        if(binding.cbGrass.isChecked)
            array.add(PokeType.Grass.ordinal)
        if(binding.cbElectric.isChecked)
            array.add(PokeType.Electric.ordinal)
        return array
    }

    private fun navigateToListFragment(){
        val action = LoginFragmentDirections.actionLoginFragmentToListFragment()
        view?.findNavController()?.navigate(action)
    }

}
