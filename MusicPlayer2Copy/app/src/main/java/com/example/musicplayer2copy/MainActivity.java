package com.example.musicplayer2copy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.musicplayer2copy.MusicDTO;
import com.example.musicplayer2copy.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 음악 리스트를 뿌려줄 ListView
    private ListView listView;
    // 음악 리스트
    public static ArrayList<MusicDTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 List를 만듦
        listView = (ListView) findViewById(R.id.listview);
        MyAdapter adapter = new MyAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("playlist", list);
                startActivity(intent);
            }
        });

    }

    // 디바이스 안에 있는 mp3 파일리스트를 조회하여 List를 만듦
    @SuppressLint("Range")
    public void getMusicList() {
        list = new ArrayList<>(); // 음악 리스트 초기화 및 생성

        list.add(new MusicDTO("1", "song1", "kim", R.raw.song1));
        list.add(new MusicDTO("2", "song2", "park", R.raw.song2));
        list.add(new MusicDTO("3", "song3", "lee", R.raw.song3));
        list.add(new MusicDTO("4", "song4", "hong", R.raw.song4));
    }


}