/**
 * Created by suraj on 7/6/17.
 */
public class Artist {
    private int id;
    private String name;

    public Artist() {
    }

    public Artist(String artistName) {
        this.name = artistName;
    }

    public Artist(int id, String name) {
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
