package com.example.musicplayer2copy;

import java.io.Serializable;

// 컨텐트 리졸버를 통해 조회한 mp3 정보들을 담을 DTO 클래스
public class MusicDTO implements Serializable {
    private String id;          // Music id
    private String title;       // Music Name
    private String artist;      // Music artist
    private int musicResId;     // Music resId

    // 기본생성자
    public MusicDTO(){}

    // 초기화 생성자
    public MusicDTO(String id, String title, String artist, int musicResId){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.musicResId = musicResId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getMusicResId() {
        return musicResId;
    }

    public void setMusicResId(int musicResId) {
        this.musicResId = musicResId;
    }

    // ToString()
    @Override
    public String toString() {
        return "MusicDto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", musicResId='" + musicResId + '\'' +
                '}';
    }
}
