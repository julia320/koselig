package com.example.koselig

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.koselig.R

class LyricAdapter constructor(private val lyrics: List<String>) : RecyclerView.Adapter<PathAdapter.ViewHolder>() {

    /**
     * Open & parse our XML file for our row and return the ViewHolder.
     *
     * This is called when the RecyclerView needs to render a new row type for the 1st time or when
     * there's nothing to recycle for a new row (for example, when the initial list is being
     * created for the first time).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Open & parse our XML file
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.pathrow, parent, false)

        // Create a new ViewHolder
        return ViewHolder(view)
    }

    /**
     * Returns the number of rows to render
     */
    override fun getItemCount(): Int = path.size

    /**
     * Given a row, fill it with data.
     *
     * This is called after a row has been created (onCreateViewHolder) or when a row is being
     * recycled. In both cases, the ViewHolder is passed to this function along with the int
     * which you can use to index your data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentpath = path[position]

        holder.directionstepTextView.text = currentpath
        // TODO Download the image by its URL and load it into the ImageView
    }

    /**
     * Holds references to the views that make up an individual row. findViewById can be an
     * expensive operation, so this prevents you from needing to do it again when a row is recycled.
     */
    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        val directionstepTextView: TextView = view.findViewById(R.id.directionstep)

    }
}