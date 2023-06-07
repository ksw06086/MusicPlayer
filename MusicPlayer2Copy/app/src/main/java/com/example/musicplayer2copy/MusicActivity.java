package com.example.musicplayer2copy;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<MusicDTO> list;                       // 음악 리스트 
    private MediaPlayer mediaPlayer;                        // 음악 플레이를 위한 클래스
    private TextView title;                                 // 음악 제목
    private ImageView previous, play, pause, next;    // 앨범, 이전, 플레이, 정지, 다음 이미지
    private SeekBar seekBar;                                // 음악 process 실시간 출력 바
    boolean isPlaying = true;                               // 플레이 중인지 아닌지 switch 변수
    private ProgressUpdate progressUpdate;                  // 음악 process 실시간 출력하는 Thread
    private int position;                                   // 현재 음악 번호 및 위치
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        
        Intent intent = getIntent(); // 이전 intent값 가져오기 위한 생성
        mediaPlayer = new MediaPlayer(); // 음악 재생을 위한 클래스 생성
        title = (TextView) findViewById(R.id.title);
        seekBar = (SeekBar) findViewById(R.id.seekbar);

        position = intent.getIntExtra("position", 0); // 음악 재생하는 위치 가져오기
        list = (ArrayList<MusicDTO>) intent.getSerializableExtra("playlist"); // 재생할 모든 음악 가져오기

        // 이전곡, 재생, 정지, 다음곡 ImageView 가져오기
        previous = (ImageView) findViewById(R.id.pre);
        play = (ImageView) findViewById(R.id.play);
        pause = (ImageView) findViewById(R.id.pause);
        next = (ImageView) findViewById(R.id.next);

        // 각 이미지의 클릭 리스너 지정해줌
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);

        // 해당 번호의 음악 재생하기
        playMusic(list.get(position));
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        // seekBar가 계속 증가가 될텐데 그때마다 이벤트 발생시켜줌
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            // seekBar에 사용자가 손을 대고 있을 때 => 음악 중지
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            // seekBar에 사용자가 손을 떼었을 때 => 해당 장소에 음악 재생
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress() > 0 && play.getVisibility() == View.GONE){
                    mediaPlayer.start();
                }
            }
        });
    }

    // 해당 번호의 음악 가져와서 재생하기
    public void playMusic(MusicDTO musicDTO) {
        try{
            seekBar.setProgress(0); // 음악 재생 전 seekBar 0으로 초기화
            title.setText(musicDTO.getArtist() + " - " + musicDTO.getTitle()); // 제목 보여줌
            Log.v("musicActivity", "제목 보여줌");
            mediaPlayer.reset(); // 음악 재생하기 전에 초기화
            Log.v("musicActivity", "음악 초기화");
            mediaPlayer = MediaPlayer.create(this, musicDTO.getMusicResId());
            Log.v("musicActivity", "음악 " + musicDTO.getMusicResId() + " 새로 생성해줌");
            // 음악이 모두 재생되어서 끝났을 때
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(position + 1 < list.size()){
                        position++;
                        playMusic(list.get(position));
                    }
                }
            });
            Log.v("musicActivity", "음악 OnCompletionListener 세팅해줌");
            mediaPlayer.start();   // 음악 재생하기
            Log.v("musicActivity", "음악 재생");
            seekBar.setMax(mediaPlayer.getDuration()); // seekBar의 최대값음 음악 재생 마지막 값으로 세팅
            Log.v("musicActivity", "seekBar 최대값 지정");
            if(mediaPlayer.isPlaying()){ // 음악 재생중이면 정지 이미지가 출력되게 하기
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            } else {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

        } catch (Exception e){
            Log.e("SimplePlayer", e.getMessage());
        }
    }

    // 앨범이 저장되어 있는 경로를 리턴함
    private String getCoverArtPath(long albumId, Context context) {
        // id가 albumId인 album_art(이미지 경로) 가져오기
        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID,
                new String[]{Long.toString(albumId)},
                null
        );

        boolean queryResult = albumCursor.moveToFirst(); // 잘 가져왔는지 확인
        String result = null;
        if(queryResult){ // 잘 가져왔으면 경로 전달
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play) {
            // 재생했을 때 재생 이미지를 정지 이미지로 바꾸고
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
            // 음악 제셍 시작 지점을 이전에 멈춘 지점으로 지정해줌
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            // 음악 시작
            mediaPlayer.start();
        } else if(v.getId() == R.id.pause) {
            // 정지했을 때 정지 이미지를 재생 이미지로 바꾸고
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            // seekBar를 현재 음악 재생되어있는 부분으로 맞춰주고
            mediaPlayer.pause();
        } else if(v.getId() == R.id.pre) {
            if(position-1 >= 0){ // 이전 곡이 있을 때
                position--;      // 위치를 이전으로 바꾸어줌
                playMusic(list.get(position));  // 이전 음악을 재생시켜줌
                seekBar.setProgress(0);         // 음악을 처음부터 재생하니까 SeekBar도 처음으로 지정
            }
        } else if(v.getId() == R.id.next) {
            if(position+1 < list.size()){ // 다음 곡이 있을 때
                position++;      // 위치를 다음으로 바꾸어줌
                playMusic(list.get(position));  // 다음 음악을 재생시켜줌
                seekBar.setProgress(0);         // 음악을 처음부터 재생하니까 SeekBar도 처음으로 지정
            }
        }
    }

    // seekBar의 progress 음악 재생과 동시에 증가시켜줌
    // 스레드로 상속해줘서 음악 재생과 함께 동작할 수 있도록 해줌
    class ProgressUpdate extends Thread{
        @Override
        public void run() {
            while(isPlaying){ // 음악이 재생중이라면
                try {
                    Thread.sleep(500); // 0.5초마다
                    if(mediaPlayer!=null){ // 음악이 없지 않다면
                        // 현재 음악 재생 지점으로 progress 지정해줌(곧 계속 증가됨)
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate",e.getMessage());
                }

            }
        }
    }

    // 화면이 나가게 되면, 음악 멈추고, 음악 실행 되었던거 삭제해줌
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}