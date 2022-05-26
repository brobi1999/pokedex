package hu.bme.aut.pokedex.ui.list
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.databinding.FragmentListBinding
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.util.MyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ListFragment : Fragment(), PokeAdapter.Listener, DetailDialogFragment.DetailDialogListener{

    companion object {
        fun newInstance() = ListFragment()
    }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

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
        val items = viewModel.items
        val pokeAdapter = PokeAdapter(this, this)
        binding.rvPoke.adapter = pokeAdapter
        binding.rvPoke.layoutManager = GridLayoutManager(requireContext(), 2)
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.rvPoke.addItemDecoration(decoration)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokeAdapter.loadStateFlow.collect {
                    binding.prependProgress.isVisible = it.source.prepend is LoadState.Loading || it.source.refresh is LoadState.Loading
                    binding.appendProgress.isVisible = it.source.append is LoadState.Loading || it.source.refresh is LoadState.Loading

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                items.collectLatest {
                    pokeAdapter.submitData(it)
                }
            }
        }
    }

    //create and show blur then show dialog
    override fun onPokeClicked(poke: Poke) {
        val dialogFragment = DetailDialogFragment.newInstance(poke)
        lifecycleScope.launch {
            withContext(Dispatchers.Default){
                MyUtil.screenShot(requireActivity()){ bitmap ->
                    val blurBitmap : Bitmap? = BlurView(requireActivity().application).blurBackground(bitmap, 18)
                    lifecycleScope.launch{
                        withContext(Dispatchers.Main){
                            binding.ivBlur.setImageBitmap(blurBitmap)
                            binding.ivBlur.visibility = View.VISIBLE
                            dialogFragment.show(parentFragmentManager, DetailDialogFragment.TAG)
                        }
                    }
                }
            }





        }
    }

    //hide blur on dialog dismiss
    override fun onDismissCalled() {
        binding.ivBlur.visibility = View.GONE
    }


}