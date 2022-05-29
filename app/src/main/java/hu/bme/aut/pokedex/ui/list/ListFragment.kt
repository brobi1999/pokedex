package hu.bme.aut.pokedex.ui.list
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.databinding.FragmentListBinding
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.util.MyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException

@AndroidEntryPoint
class ListFragment : Fragment(), PokeAdapter.Listener, DetailDialogFragment.DetailDialogListener{

    companion object {
        fun newInstance() = ListFragment()
    }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()

    private lateinit var pokeAdapter: PokeAdapter

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

        binding.etName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                viewModel.refreshView()

                return@OnKeyListener true
            }
            false
        })

        setupFilter()

        binding.btnSearch.setOnClickListener {
            viewModel.refreshView()
        }

        viewModel.favAdded.observe(viewLifecycleOwner) { newAddedFav ->
            if(newAddedFav != null){
                pokeAdapter.favAdded(newAddedFav)
            }
        }
        viewModel.favRemoved.observe(viewLifecycleOwner) { newRemovedFav ->
            if(newRemovedFav != null){
                pokeAdapter.favRemoved(newRemovedFav)
            }
        }

        binding.cbFavIcon.setOnCheckedChangeListener { _, _ ->
            viewModel.refreshView()
        }

        viewModel.refresh.observe(viewLifecycleOwner) { newRefresh ->
            if(newRefresh){
                refreshListWithNewFilters()
                viewModel.refreshReceived()
            }
        }

        viewModel.isError.observe(viewLifecycleOwner) { newException ->
            if(newException != null){
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                viewModel.errorReceived()
            }
        }

    }

    //called by liveData observer, use viewModel.refreshView()
    private fun refreshListWithNewFilters(){
        if(viewModel.isCacheReady.value == true){
            viewModel.shouldDisplayFavouritesOnly = binding.cbFavIcon.isChecked
            viewModel.nameQuery = binding.etName.text.toString()
            updateCheckBoxStatusInViewModel()
            pokeAdapter.refresh()
        }
    }

    private fun updateCheckBoxStatusInViewModel(){
        viewModel.filterFire = binding.cbFire.isChecked
        viewModel.filterGrass = binding.cbGrass.isChecked
        viewModel.filterElectric = binding.cbElectric.isChecked
    }

    private fun setupFilter() {
        binding.btnFilter.setOnClickListener {
            if(binding.linLayoutFilter.isVisible)
                binding.linLayoutFilter.visibility = View.GONE
            else
                binding.linLayoutFilter.visibility = View.VISIBLE
        }
    }

    private fun initPokePagerAdapter() {
        viewModel.isCacheReady.observe(viewLifecycleOwner) { newIsCacheReady ->
            if(newIsCacheReady){
                pokeAdapter = PokeAdapter(this, this)
                binding.rvPoke.adapter = pokeAdapter
                binding.rvPoke.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.swipeRefreshLayout.setOnRefreshListener {
                    viewModel.refreshView()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                startPagingFlow()
            }

        }
    }

    private fun startPagingFlow(){
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pokeAdapter.loadStateFlow.collect {
                    //handle progressBar
                    binding.prependProgress.isVisible = it.source.prepend is LoadState.Loading || it.source.refresh is LoadState.Loading
                    binding.appendProgress.isVisible = it.source.append is LoadState.Loading || it.source.refresh is LoadState.Loading

                    //handleErrorMessage
                    val errorState = when {
                        it.source.append is LoadState.Error -> it.source.append as LoadState.Error
                        it.source.prepend is LoadState.Error ->  it.source.prepend as LoadState.Error
                        it.source.refresh is LoadState.Error -> it.source.refresh as LoadState.Error
                        else -> null
                    }
                    errorState?.let {
                        if(errorState.error is ConnectException){
                            Toast.makeText(requireContext(), "Failed to connect to API", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collectLatest {
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
                            dialogFragment.show(childFragmentManager, DetailDialogFragment.TAG)
                        }
                    }
                }
            }
        }
    }

    override fun addPokeToFavourites(name: String) {
        viewModel.addPokeToFavourites(name)
    }

    override fun removePokeFromFavourites(name: String) {
        viewModel.removePokeFromFavourites(name)
    }

    //hide blur on dialog dismiss
    //and update adapter favNameList for consistent ui
    override fun onDismissCalled() {
        binding.ivBlur.visibility = View.GONE
    }


}