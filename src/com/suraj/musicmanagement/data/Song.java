package com.suraj.musicmanagement.data;

import java.util.ArrayList;

/**
 * Created by suraj on 7/6/17.
 */
public class Song {
    private int id;
    private String name;
    private ArrayList<Artist> artists;
    private Movie movie;

    public Lyricist getLyricist() {
        return lyricist;
    }

    public void setLyricist(Lyricist lyricist) {
        this.lyricist = lyricist;
    }

    public Musician getMusician() {
        return musician;
    }

    public void setMusician(Musician musician) {
        this.musician = musician;
    }

    private Lyricist lyricist;
    private Musician musician;

    public Song(int id, String name, Movie movie) {
        this.id = id;
        this.name = name;
        this.movie=movie;
    }

    public Song(String songName, Movie movie) {
        this.id = id;
        this.name = songName;
        this.movie=movie;
    }

    public Song(String name, Movie movie, ArrayList<Artist> artists, Lyricist lyricist, Musician musician) {
        this.name = name;
        this.movie=movie;
        this.artists = artists;
        this.lyricist=lyricist;
        this.musician=musician;
    }

    public Song(int id, String name, Movie movie,ArrayList<Artist> artists,Lyricist lyricist,Musician musician) {
        this.id=id;
        this.name=name;
        this.movie=movie;
        this.artists=artists;
        this.lyricist=lyricist;
        this.musician=musician;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public void addArtist(Artist artist){
        if(artists==null){
            artists = new ArrayList<>();
        }

        artists.add(artist);
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
