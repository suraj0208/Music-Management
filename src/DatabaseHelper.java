import java.security.Key;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by suraj on 7/6/17.
 */
public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/MusicManagement";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "root";

    private static final String DATABASE_LANGUAGES_TABLE_NAME = "Languages";
    private static final String DATABASE_MOVIES_TABLE_NAME = "Movies";
    private static final String DATABASE_SONGS_TABLE_NAME = "Songs";
    private static final String DATABASE_ARTISTS_TABLE_NAME = "Artists";
    private static final String DATABASE_LYRICISTS_TABLE_NAME = "Lyricists";
    private static final String DATABASE_SONGS_ARTISTS_TABLE_NAME = "Songs_Artists";
    private static final String DATABASE_SONGS_LYRICISTS_TABLE_NAME = "Songs_Lyricists";


    private Connection connection;

    public DatabaseHelper() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addLanguage(Language language) {
        try {
            Statement statement = connection.createStatement();

            String query = "INSERT INTO " + DATABASE_LANGUAGES_TABLE_NAME + " (language_name) VALUES('" + language.getName() + "');";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMovie(Movie movie) {
        try {
            Statement statement = connection.createStatement();

            String query =
                    "INSERT INTO " + DATABASE_MOVIES_TABLE_NAME + " (movie_name,movie_year,language_id,movie_record_no) " +
                            "VALUES('" + movie.getName() + "'," + movie.getYear() + "," + movie.getLanguageId() + "," + movie.getRecordNo() + ");";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addArtist(Artist artist) {
        try {
            Statement statement = connection.createStatement();

            String query = "INSERT INTO " + DATABASE_ARTISTS_TABLE_NAME + " (artist_name) VALUES('" + artist.getName() + "');";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLyricist(Lyricist lyricist) {
        try {
            Statement statement = connection.createStatement();

            String query = "INSERT INTO " + DATABASE_LYRICISTS_TABLE_NAME + " (lyricist_name) VALUES('" + lyricist.getName() + "');";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSong(Song song) {

        try {
            Statement statement = connection.createStatement();

            String query = "INSERT INTO " + DATABASE_SONGS_TABLE_NAME + " (song_name, movie_id) " +
                    "VALUES('" + song.getName() + "'," + song.getMovieId() + ");";

            statement.executeUpdate(query);

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT song_id FROM " + DATABASE_SONGS_TABLE_NAME + " WHERE song_name ='" + song.getName() + "' AND movie_id = " + song.getMovieId() + ";");

            int songId = -1;

            if (resultSet.next()) {
                songId = resultSet.getInt(1);
            }

            song.setId(songId);

            for (Artist artist : song.getArtists())
                addSongArtistRelation(song, artist);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Language> getAvailableLanguages() {
        ArrayList<Language> languageArrayList = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + DATABASE_LANGUAGES_TABLE_NAME + ";");

            while (resultSet.next()) {
                Language language = new Language();
                language.setId(resultSet.getInt(1));
                language.setName(resultSet.getString(2));
                languageArrayList.add(language);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return languageArrayList;
    }

    public ArrayList<Movie> getAvailableMovies() {
        ArrayList<Movie> languageArrayList = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT movie_id, movie_name, movie_year, Movies.language_id, language_name, movie_record_no FROM "
                            + DATABASE_MOVIES_TABLE_NAME + " INNER JOIN " + DATABASE_LANGUAGES_TABLE_NAME + " ON Movies.language_id = Languages.language_id;");

            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setId(resultSet.getInt(1));
                movie.setName(resultSet.getString(2));
                movie.setYear(resultSet.getInt(3));
                movie.setLanguageId(resultSet.getInt(4));
                movie.setLanguage(resultSet.getString(5));
                movie.setRecordNo(resultSet.getInt(6));
                languageArrayList.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return languageArrayList;
    }

    public ArrayList<Artist> getAvailableArtists() {
        ArrayList<Artist> artistArrayList = new ArrayList<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + DATABASE_ARTISTS_TABLE_NAME + ";");

            while (resultSet.next()) {
                Artist artist = new Artist();
                artist.setId(resultSet.getInt(1));
                artist.setName(resultSet.getString(2));
                artistArrayList.add(artist);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return artistArrayList;
    }

    public void addSongArtistRelation(Song song, Artist artist) {
        try {
            Statement statement = connection.createStatement();

            String query = "INSERT INTO " + DATABASE_SONGS_ARTISTS_TABLE_NAME + " (song_id, artist_id) " +
                    "VALUES(" + song.getId() + "," + artist.getId() + ");";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllSongs(){
        try {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT song_name FROM Songs;");

            ArrayList<String> songs = new ArrayList<>();

            while (resultSet.next()){
                songs.add(resultSet.getString(1));
            }

            return songs;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getAllMovies(){
        try {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT movie_name FROM Movies;");

            ArrayList<String> movies = new ArrayList<>();

            while (resultSet.next()){
                movies.add(resultSet.getString(1));
            }

            return movies;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Movie findMovie(String movieName) {
        try {

            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT movie_id, movie_name, movie_year, Movies.language_id, language_name, movie_record_no FROM "
                            + DATABASE_MOVIES_TABLE_NAME + " INNER JOIN " + DATABASE_LANGUAGES_TABLE_NAME + " ON Movies.language_id = Languages.language_id where movie_name like '%" + movieName + "%';");

            if (resultSet.next()) {
                Movie movie = new Movie();
                movie.setId(resultSet.getInt(1));
                movie.setName(resultSet.getString(2));
                movie.setYear(resultSet.getInt(3));
                movie.setLanguageId(resultSet.getInt(4));
                movie.setLanguage(resultSet.getString(5));
                movie.setRecordNo(resultSet.getInt(6));
                return movie;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public ArrayList<Song> getSongsForMovie(String movieName) {
        HashMap<Integer, Song> songs = new HashMap<>();

        Movie movie = findMovie(movieName);

        if (movie == null) {
            System.out.println("movie not found");
            return null;
        }

        String query = "SELECT Songs.song_id, song_name, Artists.artist_id, artist_name FROM Movies INNER JOIN Songs ON Movies.movie_id=Songs.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.song_id INNER JOIN Artists ON Songs_Artists.artist_id=Artists.artist_id WHERE Movies.movie_id=" + movie.getId() + ";";


        try {
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                int songId = resultSet.getInt(1);

                Song currentSong = songs.get(songId);

                if (currentSong == null) {
                    currentSong = new Song(songId, resultSet.getString(2), movie.getId());
                    currentSong.setMovie(movie);
                    Artist artist = new Artist();
                    artist.setId(resultSet.getInt(3));
                    artist.setName(resultSet.getString(4));
                    currentSong.addArtist(artist);
                    songs.put(songId, currentSong);

                } else {
                    Artist artist = new Artist();
                    artist.setId(resultSet.getInt(3));
                    artist.setName(resultSet.getString(4));
                    currentSong.addArtist(artist);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Integer i : songs.keySet()) {
            Song song = songs.get(i);

            System.out.print(song.getId());
            System.out.print(" ");
            System.out.print(song.getName());
            System.out.print(" ");

            for (Artist artist : song.getArtists())
                System.out.print(artist.getName() + " ");


            System.out.println();
        }

        ArrayList<Song> songArrayList = new ArrayList<>();

        for (Integer k : songs.keySet())
            songArrayList.add(songs.get(k));

        return songArrayList;
    }

    public ArrayList<Song> findSong(String songName) {
        HashMap<Integer, Song> currentHashMap = new HashMap<>();

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(
                    "SELECT Songs.song_id,Songs.song_name, Movies.movie_id,Movies.movie_name,Movies.movie_year,Movies.movie_record_no,Artists.artist_id, Artists.artist_name FROM Songs INNER JOIN Movies ON Songs.movie_id=Movies.movie_id INNER JOIN Songs_Artists on Songs.song_id=Songs_Artists.Song_id INNER JOIN Artists ON Songs_Artists.Artist_id = Artists.artist_id WHERE Songs.song_name LIKE '%" + songName + "%';");

            Movie previousMovie = null;

            while (resultSet.next()) {

                Song currentSong = currentHashMap.get(resultSet.getInt(1));

                if (currentSong == null) {
                    currentSong = new Song(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));

                    if (previousMovie != null) {
                        if (currentSong.getMovieId() == previousMovie.getId()) {
                            currentSong.setMovie(previousMovie);
                        } else {
                            previousMovie = new Movie();
                            previousMovie.setId(resultSet.getInt(3));
                            previousMovie.setName(resultSet.getString(4));
                            previousMovie.setYear(resultSet.getInt(5));
                            previousMovie.setRecordNo(resultSet.getInt(6));
                            currentSong.setMovie(previousMovie);
                        }

                    } else if (previousMovie == null) {
                        previousMovie = new Movie();
                        previousMovie.setId(resultSet.getInt(3));
                        previousMovie.setName(resultSet.getString(4));
                        previousMovie.setYear(resultSet.getInt(5));
                        previousMovie.setRecordNo(resultSet.getInt(6));
                        currentSong.setMovie(previousMovie);
                    }

                    Artist artist = new Artist();
                    artist.setId(resultSet.getInt(7));
                    artist.setName(resultSet.getString(8));

                    currentSong.addArtist(artist);
                    currentHashMap.put(currentSong.getId(), currentSong);
                } else {
                    Artist artist = new Artist();
                    artist.setId(resultSet.getInt(7));
                    artist.setName(resultSet.getString(8));

                    currentSong.addArtist(artist);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Song> arrayList = new ArrayList<>();

        for (Integer k : currentHashMap.keySet())
            arrayList.add(currentHashMap.get(k));

        return arrayList;
    }
}
