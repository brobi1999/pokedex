package hu.bme.aut.pokedex.ui.list
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
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

    private val viewModel: ListViewModel by activityViewModels()

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
        MyUtil.configureStatLayout(binding.statLayout, poke?.hp, poke?.def, poke?.atk, poke?.sp, requireContext())
        binding.rbBack.setOnClickListener { onCheckBoxCheckChanged(poke) }
        binding.rbFront.setOnClickListener { onCheckBoxCheckChanged(poke) }
        binding.rbMale.setOnClickListener { onCheckBoxCheckChanged(poke) }
        binding.rbFemale.setOnClickListener { onCheckBoxCheckChanged(poke) }




    }

    private fun onCheckBoxCheckChanged(poke: Poke?) {
        when{
            binding.rbFront.isChecked && binding.rbBack.isChecked && binding.rbMale.isChecked && binding.rbFemale.isChecked ->{
                //Glide.with(this).load(poke?.front_default).into(holder.binding.ivPoke)
            }

        }
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
        listener = requireParentFragment().childFragmentManager.fragments[0] as DetailDialogListener

    }

}