package com.suraj.musicmanagement.data;

import java.util.ArrayList;

/**
 * Created by suraj on 7/6/17.
 */
public class Song {
    private int id;
    private String name;
    private int movieId;
    private ArrayList<Artist> artists;
    private Movie movie;

    public Song(int id, String name, int movieId) {
        this.id = id;
        this.name = name;
        this.movieId = movieId;
    }

    public Song(String songName, int movieId) {
        this.id = id;
        this.name = songName;
        this.movieId = movieId;
    }

    public Song(String name, int movieId, ArrayList<Artist> artists) {
        this.name = name;
        this.movieId = movieId;
        this.artists = artists;
    }

    public Song(int id, String name, int movieId, ArrayList<Artist> artists) {
        this.id=id;
        this.name=name;
        this.movieId=movieId;
        this.artists=artists;
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

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
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
