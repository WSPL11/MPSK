package com.example.onlinechannelsounder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.onlinechannelsounder.ui.theme.OnlineChannelSounderTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.os.Build
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement


class MainActivity : ComponentActivity() {

    private var recorder: AudioRecord? = null
    private var player: AudioTrack? = null
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT)

    private lateinit var recordedData: ShortArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions() // パーミッション要求

        setContent {
            CIRSounderApp(
                onRecord = { startRecording() },
                onPlay = { playRecordedAudio() }
            )
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private fun startRecording() {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        recordedData = ShortArray(bufferSize)

        recorder?.startRecording()
        recorder?.read(recordedData, 0, bufferSize)
        recorder?.stop()
        recorder?.release()
    }

    private fun playRecordedAudio() {
        player = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 2,
            AudioTrack.MODE_STREAM
        )

        player?.play()
        player?.write(recordedData, 0, recordedData.size)
        player?.stop()
        player?.release()
    }
}

@Composable
fun CIRSounderApp(onRecord: () -> Unit, onPlay: () -> Unit) {
    Scaffold(
        TopAppBar(
            title = { Text("Online Channel Sounder") }
        )
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onRecord) {
                Text("録音")
            }
            Button(onClick = onPlay) {
                Text("再生")
            }
        }
    }
}
