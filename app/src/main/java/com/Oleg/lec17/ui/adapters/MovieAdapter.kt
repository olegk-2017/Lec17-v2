package com.Oleg.lec17.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.squareup.picasso.Picasso
import com.Oleg.lec17.databinding.MovieItemBinding
import com.Oleg.lec17.models.Movie

class MovieAdapter(val movies: List<Movie>) : Adapter<MovieAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieItemBinding.inflate(inflater, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder:  VH, position: Int) {
        val movie = movies[position]
        //bind the text:
        holder.binding.tvTitle.text = movie.title
        Picasso.get().load(movie.posterUrl).into(holder.binding.imagePoster)
    }

    override fun getItemCount() = movies.size

    class VH(val binding: MovieItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
//https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg