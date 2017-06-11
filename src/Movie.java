/**
 * Created by suraj on 7/6/17.
 */
public class Movie {
    private int id;
    private String name;
    private int year;
    private int languageId;
    private String language;

    public Movie() {
    }

    public int getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(int recordNo) {
        this.recordNo = recordNo;
    }

    private int recordNo;

    public Movie( String name, int year, int languageId, int recordNo) {
        this.name = name;
        this.year = year;
        this.languageId = languageId;
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

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setId(int id) {
        this.id = id;
    }

}
