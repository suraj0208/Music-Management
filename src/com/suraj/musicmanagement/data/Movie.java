package com.suraj.musicmanagement.data;

/**
 * Created by suraj on 7/6/17.
 */
public class Movie {
    private int id;
    private String name;
    private int year;
    private Language language;

    public Movie() {
    }

    public Movie(String movieName, int movieYear, Language language, int movieRecordNo) {
        this.name = movieName;
        this.year = movieYear;
        this.language=language;
        this.recordNo = movieRecordNo;
    }

    public Movie(int id, String movieName, int movieYear, Language language, int movieRecordNo) {
        this.id = id;
        this.name = movieName;
        this.year = movieYear;
        this.language=language;
        this.recordNo = movieRecordNo;
    }

    public int getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(int recordNo) {
        this.recordNo = recordNo;
    }

    private int recordNo;

    public Movie(String name, int year, int recordNo) {
        this.name = name;
        this.year = year;
        this.recordNo = recordNo;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setId(int id) {
        this.id = id;
    }

}
