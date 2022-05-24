package hu.bme.aut.pokedex.ui.list
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.databinding.FragmentListBinding

@AndroidEntryPoint
class ListFragment : Fragment(){

    companion object {
        fun newInstance() = ListFragment()
    }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    private var pokeAdapter: PokeAdapter = PokeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPokePagerAdapter()

    }

    private fun initPokePagerAdapter() {

    }


}