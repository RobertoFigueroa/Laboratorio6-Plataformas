package com.example.rober.laboratorio6

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import com.example.rober.laboratorio6.MusicService.MusicBinder
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build
import android.support.annotation.RequiresApi
import com.example.rober.laboratorio6.R.drawable.rand







class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private val songTitle = ""
    val NOTIFY_ID = 1
    private var shuffle = false
    private var rand: Random? = null

    //media player
    private var player: MediaPlayer? = null
    //song list
    private var songs: ArrayList<Song>? = null
    //current position
    private var songPosn: Int = 0

    private val musicBind = MusicBinder()


    override fun onCreate() {
        rand = Random()
        //create the service

        //create the service
        super.onCreate()
//initialize position
        songPosn = 0
//create player
        player = MediaPlayer()

        initMusicPlayer()
        player?.setOnPreparedListener(this);
        player?.setOnCompletionListener(this);
        player?.setOnErrorListener(this);

    }

    fun initMusicPlayer() {
        //set player properties
        player!!.setWakeMode(
            applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK)
        player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)



    }


    override fun onCompletion(mp: MediaPlayer?) {
        if(player?.currentPosition!! >0){
            mp?.reset();
            playNext()}
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp!!.reset()


    return false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()

        val notIntent = Intent(this,MainActivity::class.java)
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val  pendInt = PendingIntent.getActivity(this,0,
        notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        val builder = Notification.Builder(this)
        builder.setContentIntent(pendInt)
            .setSmallIcon(R.drawable.play)
            .setTicker(songTitle)
            .setOngoing(true)
            .setContentTitle("Playing")
        .setContentText(songTitle)
        val  not = builder.build()
        startForeground(NOTIFY_ID, not)

    }


    override fun onBind(arg0: Intent): IBinder? {
        return musicBind
    }

    fun setList(theSongs: ArrayList<Song>) {
        songs = theSongs
    }

    inner class MusicBinder : Binder() {
        internal val service: MusicService
            get() = this@MusicService
    }

    override fun onUnbind(intent: Intent): Boolean {
        player?.stop()
        player?.release()
        return false
    }

    fun playSong() {
        //play a song
        player?.reset()
        //get song
        val playSong = songs!![songPosn]

        val songTitle=playSong.getTitle()

        //get id
        val currSong = playSong!!.getID()
        //set uri
        val trackUri = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currSong
        )
        try {
            player?.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }

        player?.prepareAsync()

    }

    fun setSong(songIndex: Int) {
        songPosn = songIndex
    }

    fun getPosn(): Int {
        return player!!.currentPosition
    }

    fun getDur(): Int {
        return player!!.duration
    }

    fun isPng(): Boolean {
        return player!!.isPlaying
    }

    fun pausePlayer() {
        player!!.pause()
    }

    fun seek(posn: Int) {
        player!!.seekTo(posn)
    }

    fun go() {
        player!!.start()
    }

    fun playPrev(){
        songPosn--
        if(this.songPosn<0) songPosn = (songs!!.size)-1
        playSong()
    }
    //skip to next
    fun playNext(){
        songPosn++
        if(songPosn>=songs!!.size) songPosn=0
        playSong()
        if(shuffle){
            var newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand!!.nextInt(songs!!.size)
            }
            songPosn=newSong
        }
        else{
            songPosn++
            if(songPosn>=songs!!.size) songPosn=0
        }
        playSong()

    }

    override fun onDestroy() {
        stopForeground(true)

    }

    fun setShuffle() {
        shuffle = !shuffle

    }




}
