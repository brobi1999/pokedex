package hu.bme.aut.pokedex.ui.login
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.FragmentLoginBinding
import hu.bme.aut.pokedex.model.ui.PokeType

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTypeLabels()

        viewModel.success.observe(viewLifecycleOwner) { newSuccess ->
            if(newSuccess){
                viewModel.successReceived()
                navigateToListFragment()
            }

        }

        binding.btnSaveAccount.setOnClickListener {
            RegisterDialogFragment.newInstance(binding.etName.text.toString(), getFavouriteTypes()).show(childFragmentManager, RegisterDialogFragment.TAG)
        }
        binding.btnLogin.setOnClickListener {
            LoginDialogFragment.newInstance().show(childFragmentManager, LoginDialogFragment.TAG)
        }
    }

    private fun setTypeLabels() {
        binding.labelFire.labelBackgroundCardView.setCardBackgroundColor(Color.RED)
        binding.labelFire.tvLabelType.text = getString(R.string.fire)
        binding.labelFire.tvLabelType.setTextColor(Color.WHITE)

        binding.labelGrass.labelBackgroundCardView.setCardBackgroundColor(Color.GREEN)
        binding.labelGrass.tvLabelType.text = getString(R.string.grass)
        binding.labelGrass.tvLabelType.setTextColor(Color.BLACK)

        binding.labelElectric.labelBackgroundCardView.setCardBackgroundColor(Color.YELLOW)
        binding.labelElectric.tvLabelType.text = getString(R.string.electric)
        binding.labelElectric.tvLabelType.setTextColor(Color.BLACK)
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
