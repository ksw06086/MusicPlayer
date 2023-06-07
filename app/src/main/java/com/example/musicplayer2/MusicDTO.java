package com.example.musicplayer2;

import java.io.Serializable;

// 컨텐트 리졸버를 통해 조회한 mp3 정보들을 담을 DTO 클래스
public class MusicDTO implements Serializable {
    private String id;          // Music id
    private String albumId;     // Album id
    private String title;       // Music Name
    private String artist;      // Music artist

    // 기본생성자
    public MusicDTO(){}

    // 초기화 생성자
    public MusicDTO(String id, String albumId, String title, String artist){
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
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

    // ToString()
    @Override
    public String toString() {
        return "MusicDto{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
