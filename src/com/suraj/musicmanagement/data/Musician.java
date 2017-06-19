package com.suraj.musicmanagement.data;

/**
 * Created by suraj on 18/6/17.
 */
public class Musician {
    private int id;
    private String name;

    public Musician() {
    }

    public Musician(String artistName) {
        this.name = artistName;
    }

    public Musician(int id, String name) {
        this.id=id;
        this.name=name;
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

    public void setId(int id) {
        this.id = id;
    }
}
