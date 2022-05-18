package com.example.graphqlandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaunchListAdapter(

    private val launches: List<LaunchListQuery.Launch>

) : RecyclerView.Adapter<LaunchListAdapter.ViewHolder>() {

    var onEndOfListReached: (() -> Unit)? = null

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val missionName: TextView = itemView.findViewById(R.id.mission_name)
        val site: TextView = itemView.findViewById(R.id.site)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.launch_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val launch = launches[position]
        // sets the text to the textview from our itemHolder class
        holder.missionName.text = launch.id
        holder.site.text = launch.site

        if (position == launches.size - 1) {
            onEndOfListReached?.invoke()
        }

    }

    override fun getItemCount(): Int {
        return launches.size
    }
}