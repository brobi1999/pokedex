package hu.bme.aut.pokedex.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.pokedex.R
import hu.bme.aut.pokedex.databinding.PokeItemBinding
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.util.MyUtil

class PokeAdapter(private val fragment: ListFragment, private val listener: Listener) : PagingDataAdapter<Poke, PokeAdapter.PokeViewHolder>(
    PokeComparator
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeViewHolder {
        val binding = PokeItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PokeViewHolder, position: Int) {
        val poke = getItem(position)
        holder.binding.tvPokeName.text = poke?.name.toString().replaceFirstChar { it.uppercase() }

        MyUtil.configureStatLayout(holder.binding.statLayout, poke?.hp, poke?.def, poke?.atk, poke?.sp, fragment.requireContext())

        Glide.with(fragment).load(poke?.front_default).into(holder.binding.ivPoke)

        MyUtil.configureLabelTypeLayout(holder.binding.labelType, poke?.typeSlotOne.toString(), fragment.requireContext())

        holder.binding.root.setOnClickListener {
            poke?.let { it1 -> listener.onPokeClicked(it1) }
        }

    }

    inner class PokeViewHolder(val binding: PokeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    interface Listener {
        fun onPokeClicked(poke: Poke)
    }

}

object PokeComparator : DiffUtil.ItemCallback<Poke>() {

    override fun areItemsTheSame(oldItem: Poke, newItem: Poke): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Poke, newItem: Poke): Boolean {
        return oldItem == newItem
    }
}