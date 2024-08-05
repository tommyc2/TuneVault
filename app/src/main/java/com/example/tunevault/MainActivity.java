package com.example.tunevault;


import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import android.os.Build;

public class MainActivity extends AppCompatActivity {

    public static List<Uri> listOfSongs = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapterListView;
    private MusicService musicService = new MusicService();
    private boolean isPlaying = false;

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

    }

    public void updateIsPlaying(){

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
                String songTitle = getFileNameOfMP3(audioUri); // mp3 file name used as song title

                if (listOfSongs.add(audioUri)){
                    if (songTitle.toLowerCase().contains(".mp3")) {
                        arrayAdapterListView.add(songTitle.substring(0, songTitle.length() - 4));
                        arrayAdapterListView.notifyDataSetChanged();
                    }
                }

            }
        }
    }

    private String getFileNameOfMP3(Uri uri){
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        return returnCursor.getString(nameIndex);
    }

    public void handlePlayPause(View view) {
        if (isPlaying) {
            sendData("PAUSE");
        }
        else {
            sendData("PLAY");
        }
    }

    public void sendData(String actionMessage) {
        Intent intent = new Intent(MainActivity.this, MusicService.class);

        if (actionMessage.equals("PLAY")){
            isPlaying = true;
            intent.setAction("PLAY");
            startService(intent);
        }
        else if (actionMessage.equals("PAUSE")){
            isPlaying = false;
            intent.setAction("PAUSE");
            startService(intent);
        }


    }

    public void playNextSong(View view) {
        isPlaying = true;
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setAction("NEXT");
        startService(intent);
    }

}
