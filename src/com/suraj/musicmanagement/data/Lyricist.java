package com.suraj.musicmanagement.data;

/**
 * Created by suraj on 7/6/17.
 */
public class Lyricist {
    private int id;
    private String name;

    public Lyricist( String lyricistName) {
        this.id = id;
        this.name = lyricistName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
