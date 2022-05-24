package hu.bme.aut.pokedex.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.pokedex.databinding.PokeItemBinding
import hu.bme.aut.pokedex.model.ui.Poke

class PokeAdapter : PagingDataAdapter<Poke, PokeAdapter.PokeViewHolder>(
    PokeComparator
) {

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeViewHolder {
        val binding = PokeItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PokeViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PokeViewHolder, position: Int) {
        val poke = getItem(position)


        holder.binding.root.setOnClickListener {

        }

    }

    inner class PokeViewHolder(val binding: PokeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    interface Listener {

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