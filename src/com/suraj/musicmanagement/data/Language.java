package com.suraj.musicmanagement.data;

/**
 * Created by suraj on 7/6/17.
 */
public class Language {
    public Language(int id, String name) {
        this.id=id;
        this.name=name;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;

    public Language() {
    }

    public Language(String languageName) {
        this.name = languageName;
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
