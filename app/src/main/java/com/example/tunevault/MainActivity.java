package com.example.tunevault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.media.MediaPlayer;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Uri> listOfSongs = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int currSongIndex = 0;
    private ArrayAdapter<String> arrayAdapterListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listViewSongs = findViewById(R.id.listViewSongs);
        arrayAdapterListView = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, new ArrayList<String>());
        listViewSongs.setAdapter(arrayAdapterListView);
        mediaPlayer = new MediaPlayer(); // initialize media player in create function
    }


    public void addSong(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (intent != null) {
                Uri audioUri = intent.getData(); // file path

                if (listOfSongs.add(audioUri)){
                    arrayAdapterListView.add(audioUri.toString());
                    arrayAdapterListView.notifyDataSetChanged();
                }

            }
        }
    }

    public void playSongs(View view) {
        if (!listOfSongs.isEmpty()) {
            currSongIndex = 0;
            playNextSong(view);
        }
    }

    public void playNextSong(View view) {
        if (currSongIndex < listOfSongs.size()) {
            Uri songUri = listOfSongs.get(currSongIndex);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, songUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        currSongIndex++;
                        playNextSong(view);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }


}
