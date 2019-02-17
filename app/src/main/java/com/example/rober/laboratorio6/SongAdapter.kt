package com.example.rober.laboratorio6

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;


class SongAdapter(c: Context, theSongs: ArrayList<Song>) : BaseAdapter() {

    private var songs: ArrayList<Song>? = theSongs
    private var songInf: LayoutInflater? = null

    init {
        songInf = LayoutInflater.from(c)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        //map to song layout
        val songLay : LinearLayout = songInf!!.inflate(R.layout.song, parent, false) as LinearLayout
        //get title and artist views
        val songView = songLay.findViewById<View>(R.id.song_title) as TextView
        val artistView = songLay.findViewById<View>(R.id.song_artist) as TextView
        //get song using position
        val currSong = songs?.get(position)
        //get title and artist strings
        songView.text = currSong?.getTitle()
        artistView.text = currSong?.getArtist()
        //set position as tag
        songLay.tag = position
        return songLay

    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
        }

    override fun getCount(): Int {
        return songs!!.size
        }
}