package hu.bme.aut.pokedex.ui.list
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.DialogDetailBinding
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.util.MyUtil


@AndroidEntryPoint
class DetailDialogFragment : DialogFragment() {
    companion object {

        const val TAG = "DetailDialog"

        private const val KEY_POKE = "KEY_POKE"

        fun newInstance(poke: Poke): DetailDialogFragment {
            val args = Bundle()
            args.putParcelable(KEY_POKE, poke)
            val fragment = DetailDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    interface DetailDialogListener{
        fun onDismissCalled()
    }

    lateinit var listener: DetailDialogListener

    private var _binding: DialogDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val poke = arguments?.getParcelable<Poke>(KEY_POKE)
        binding.pokeName.text = poke?.name.toString().replaceFirstChar { it.uppercase() }
        MyUtil.configureLabelTypeLayout(binding.labelType, poke?.typeSlotOne.toString(), requireContext())
        if(poke?.typeSlotTwo != null)
            MyUtil.configureLabelTypeLayout(binding.labelTypeSlot2, poke.typeSlotTwo.toString(), requireContext())
        else
            binding.labelTypeSlot2.labelBackgroundCardView.visibility = View.GONE
        MyUtil.configureStatLayout(binding.statLayout, poke?.hp, poke?.def, poke?.atk, poke?.sp, requireContext())
        binding.rbBack.setOnClickListener { onRadioButtonCheckChanged(poke) }
        binding.rbFront.setOnClickListener { onRadioButtonCheckChanged(poke) }
        binding.rbMale.setOnClickListener { onRadioButtonCheckChanged(poke) }
        binding.rbFemale.setOnClickListener { onRadioButtonCheckChanged(poke) }
        onRadioButtonCheckChanged(poke)

        binding.btnDismiss.setOnClickListener {
            dismiss()
        }

        binding.cbIsFav.isChecked = poke?.isFavourite == true

        binding.cbIsFav.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.addPokeToFavourites(poke?.name.toString())
            }
            else{
                viewModel.removePokeFromFavourites(poke?.name.toString())
            }
        }
    }

    private fun onRadioButtonCheckChanged(poke: Poke?) {
        var url: String? = null
        url = when{
            binding.rbFront.isChecked && binding.rbMale.isChecked ->{
                poke?.front_default
            }
            binding.rbFront.isChecked && binding.rbFemale.isChecked ->{
                poke?.front_female
            }
            binding.rbBack.isChecked && binding.rbMale.isChecked ->{
                poke?.back_default
            }
            binding.rbBack.isChecked && binding.rbFemale.isChecked ->{
                poke?.back_female
            }
            else -> poke?.front_default
        }
        Glide.with(this).load(url).into(binding.ivPoke)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismissCalled()
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
        //parentFragment is last fragment on stack
        listener = requireParentFragment() as DetailDialogListener

    }

}