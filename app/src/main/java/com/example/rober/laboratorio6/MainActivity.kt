package com.example.rober.laboratorio6

import android.widget.MediaController.MediaPlayerControl;
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.ListView;
import android.widget.MediaController
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu
import android.view.MenuItem;
import kotlinx.android.synthetic.main.activity_main.*
import com.example.rober.laboratorio6.MusicService.MusicBinder




class MainActivity : AppCompatActivity(), MediaPlayerControl {

    private val songList: ArrayList<Song> = ArrayList()
    private val controller: MusicController? = null
    private var musicSrv: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false
    private var paused = false
    private var playbackPaused = false

    override fun isPlaying(): Boolean {
        if(musicSrv!=null && musicBound)
        return musicSrv!!.isPng()
        return false
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getDuration(): Int {
        if(musicSrv!=null && musicBound && musicSrv!!.isPng())
        return musicSrv!!.getDur()
        else return 0
    }

    override fun pause() {
        playbackPaused=true
        musicSrv!!.pausePlayer()

    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        musicSrv!!.seek(pos)
    }

    override fun getCurrentPosition(): Int {
        if(musicSrv!=null && musicBound && musicSrv!!.isPng())
        return musicSrv!!.getPosn()
        else return 0
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        musicSrv!!.go()
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        return true
    }



    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      // val songView: ListView? = null
        val songView : ListView = findViewById(R.id.song_list)

        getSongList()

        songList.sortWith(Comparator { a, b -> a.getTitle().compareTo(b.getTitle()) })

        val songAdt = SongAdapter(this, songList)
        songView.adapter = songAdt

        setController()
    }

    //connect to the service
    private val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicBinder
            //get service
            musicSrv = binder.service
            //pass list
            musicSrv?.setList(songList)
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    public fun getSongList() {
        //devuelve la lista de canciones
        val musicResolver = contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val myCursor = musicResolver.query(musicUri, null, null, null, null, null)

        if(myCursor != null && myCursor.moveToFirst()){


                //get columns
                val titleColumn = myCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn = myCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                val artistColumn = myCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
                //add songs to list
                do {
                    val thisId = myCursor.getLong(idColumn)
                    val thisTitle = myCursor.getString(titleColumn)
                    val thisArtist = myCursor.getString(artistColumn)
                    songList.add(Song(thisId, thisTitle, thisArtist))
                } while (myCursor.moveToNext())
            }


        }

    private fun setController() {
        //set the controller up
        val controller = MusicController(this)
        controller.setPrevNextListeners(View.OnClickListener { playNext() }, View.OnClickListener { playPrev() })
        controller.setMediaPlayer(this)
        controller.setAnchorView(findViewById(R.id.song_list))
        controller.isEnabled = true
    }

    fun songPicked(view: View) {
        musicSrv?.setSong(Integer.parseInt(view.tag.toString()))
        musicSrv?.playSong()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller?.show(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //menu item selected
        when (item.itemId) {
            R.id.action_shuffle -> {
            }
            R.id.action_end -> {
                stopService(playIntent)
                musicSrv = null
                System.exit(0)
            }
            R.id.action_shuffle -> {
                musicSrv?.setShuffle()
            }
        }//shuffle
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        stopService(playIntent)
        musicSrv = null
        super.onDestroy()
    }

    private fun playNext() {
        musicSrv?.playNext()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller?.show(0)
    }

    //play previous
    private fun playPrev() {
        musicSrv?.playPrev()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller?.show(0)
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    override fun onResume() {
        super.onResume()
        if (paused) {
            setController()
            paused = false
        }
    }

    override fun onStop() {
        controller?.hide()
        super.onStop()
    }






    }

