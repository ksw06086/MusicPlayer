package com.example.musicplayer2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        // 가져오고 싶은 컬럼 명을 나열합니다.
        // id, albumId, title, artist 정보를 가져옵니다.
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
        };

        // MediaStore의 음악 데이터 가져오기
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , projection, null, null, null);

        // 가져온 리스트 하나씩 list 변수에 추가해주기
        while(cursor.moveToNext()){ // 가져온 리스트 중에서 다음 레코드가 있는지?
            MusicDTO musicDTO = new MusicDTO();
            musicDTO.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDTO.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDTO.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDTO.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            list.add(musicDTO); // 값을 세팅해주고 list에 추가해줌
        }
        cursor.close();
    }


}