package com.suraj.musicmanagement;

import com.suraj.musicmanagement.data.*;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT song_id FROM " + DATABASE_SONGS_TABLE_NAME + " WHERE song_name = ? AND movie_id = ?;");
            preparedStatement.setString(1, song.getName());
            preparedStatement.setInt(2, song.getMovieId());

            ResultSet resultSet = preparedStatement.executeQuery();
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

    public HashSet<String> getAllSongs() {
        try {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT song_name FROM Songs;");

            HashSet<String> songs = new HashSet<>();

            while (resultSet.next()) {
                songs.add(resultSet.getString(1));
            }

            return songs;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashSet<String> getAllArtists() {
        try {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT artist_name FROM Artists;");

            HashSet<String> artists = new HashSet<>();

            while (resultSet.next()) {
                artists.add(resultSet.getString(1));
            }

            return artists;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashSet<String> getAllMovies() {
        try {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT movie_name FROM Movies;");

            HashSet<String> movies = new HashSet<>();

            while (resultSet.next()) {
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

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT movie_id, movie_name, movie_year, Movies.language_id, language_name, movie_record_no FROM "
                    + DATABASE_MOVIES_TABLE_NAME + " INNER JOIN " + DATABASE_LANGUAGES_TABLE_NAME + " ON Movies.language_id = Languages.language_id where movie_name=?;");
            preparedStatement.setString(1, movieName);

            ResultSet resultSet = preparedStatement.executeQuery();

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

    public ArrayList<Song> getSongsForArtist(String name) {
        HashMap<Integer, Song> currentHashMap = new HashMap<>();

        try {

            int id = getArtistId(name);

            if (id == -1)
                return null;

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Songs.song_id,Songs.song_name, Movies.movie_id,Movies.movie_name,Movies.movie_year,Movies.movie_record_no,Artists.artist_id, Artists.artist_name FROM Songs INNER JOIN Movies ON Songs.movie_id=Movies.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.Song_id INNER JOIN Artists ON Songs_Artists.Artist_id = Artists.artist_id WHERE Songs.song_id in ( SELECT Songs.song_id FROM Songs INNER JOIN Movies ON Songs.movie_id=Movies.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.Song_id INNER JOIN Artists ON Songs_Artists.Artist_id = Artists.artist_id where Artists.artist_id = ?);");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

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

    public ArrayList<Song> getSongsForMovie(String movieName) {
        HashMap<Integer, Song> songs = new HashMap<>();

        Movie movie = findMovie(movieName);

        if (movie == null) {
            System.out.println("movie not found");
            return null;
        }

        String query = "SELECT Songs.song_id, song_name, Artists.artist_id, artist_name FROM Movies INNER JOIN Songs ON Movies.movie_id=Songs.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.song_id INNER JOIN Artists ON Songs_Artists.artist_id=Artists.artist_id WHERE Movies.movie_id=?;";


        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, movie.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

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

        ArrayList<Song> songArrayList = new ArrayList<>();

        for (Integer k : songs.keySet())
            songArrayList.add(songs.get(k));

        return songArrayList;
    }

    public ArrayList<Song> findSong(String songName) {
        HashMap<Integer, Song> currentHashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Songs.song_id,Songs.song_name, Movies.movie_id,Movies.movie_name,Movies.movie_year,Movies.movie_record_no,Artists.artist_id, Artists.artist_name FROM Songs INNER JOIN Movies ON Songs.movie_id=Movies.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.Song_id INNER JOIN Artists ON Songs_Artists.Artist_id = Artists.artist_id WHERE Songs.song_name = ?;");
            preparedStatement.setString(1, songName);

            ResultSet resultSet = preparedStatement.executeQuery();

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

    public int getArtistId(String name) {


        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT artist_id FROM Artists where artist_name = ?;");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                return resultSet.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean deleteMovie(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("delete FROM Movies where movie_id=?;");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateMovie(Movie movie) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Movies SET movie_name=?,movie_year=?,language_id=?,movie_record_no=? where movie_id=?;");
            statement.setString(1, movie.getName());
            statement.setInt(2, movie.getYear());
            statement.setInt(3, movie.getLanguageId());
            statement.setInt(4, movie.getRecordNo());
            statement.setInt(5, movie.getId());

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateSong(Song song) {
        try {

            PreparedStatement statement = connection.prepareStatement("DELETE FROM Songs_Artists WHERE song_id=?;");
            statement.setInt(1, song.getId());
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE Songs SET song_name=?,movie_id=? where song_id=?;");

            statement.setString(1, song.getName());
            statement.setInt(2, song.getMovieId());
            statement.setInt(3, song.getId());
            statement.executeUpdate();

            for (Artist artist : song.getArtists())
                addSongArtistRelation(song, artist);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteSong(Song song) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Songs WHERE song_id=?;");
            statement.setInt(1, song.getId());
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateArtist(Artist artist) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Artists SET artist_name=? WHERE artist_id=?;");
            statement.setString(1, artist.getName());
            statement.setInt(2, artist.getId());

            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Artist getArtistFromName(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Artists WHERE artist_name=?;");
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Artist artist = new Artist(resultSet.getInt(1), name);
                return artist;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public boolean deleteArtist(Artist artist) {
        try {
            ArrayList<Integer> songIdsForArtist = getSongIdsForArtist(artist);

            if (songIdsForArtist == null)
                return false;

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Artists WHERE artist_id=?;");
            preparedStatement.setInt(1, artist.getId());

            preparedStatement.executeUpdate();

            for (Integer id : songIdsForArtist)
                deleteSong(id);

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private void deleteSong(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Songs WHERE song_id=?;");
            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> getSongIdsForArtist(Artist artist) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT song_id FROM Songs_Artists WHERE artist_id=?;");
            preparedStatement.setInt(1, artist.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<Integer> songIds = new ArrayList<>();

            while (resultSet.next())
                songIds.add(resultSet.getInt(1));

            return songIds;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean exportDatabase(String location) {
        try {
            FileWriter fileWriter = new FileWriter(new File(location));

            String query = "SELECT song_name,GROUP_CONCAT(artist_name SEPARATOR ', '),movie_name, movie_year,language_name,movie_record_no FROM Movies INNER JOIN Languages ON Movies.language_id = Languages.language_id INNER JOIN Songs ON Movies.movie_id=Songs.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.song_id INNER JOIN Artists ON Artists.artist_id=Songs_Artists.artist_id GROUP BY Songs.song_id;";

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            fileWriter.write("Song");
            fileWriter.write(",");
            fileWriter.write("Artists");
            fileWriter.write(",");
            fileWriter.write("Movie");
            fileWriter.write(",");
            fileWriter.write("Year");
            fileWriter.write(",");
            fileWriter.write("Language");
            fileWriter.write(",");
            fileWriter.write("Record No");
            fileWriter.write("\n");
            fileWriter.write("\n");

            while (resultSet.next()) {
                fileWriter.write(resultSet.getString(1));
                fileWriter.write(",");
                fileWriter.write('"' + resultSet.getString(2) + '"');
                fileWriter.write(",");
                fileWriter.write(resultSet.getString(3));
                fileWriter.write(",");
                fileWriter.write(resultSet.getString(4));
                fileWriter.write(",");
                fileWriter.write(resultSet.getString(5));
                fileWriter.write(",");
                fileWriter.write(resultSet.getString(6));
                fileWriter.write("\n");
            }
            fileWriter.close();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public ArrayList<Record> getAllRecords() {
        try {
            String query = "SELECT song_name,GROUP_CONCAT(artist_name SEPARATOR ', '),movie_name, movie_year,language_name,movie_record_no FROM Movies INNER JOIN Languages ON Movies.language_id = Languages.language_id INNER JOIN Songs ON Movies.movie_id=Songs.movie_id INNER JOIN Songs_Artists ON Songs.song_id=Songs_Artists.song_id INNER JOIN Artists ON Artists.artist_id=Songs_Artists.artist_id GROUP BY Songs.song_id;";

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            ArrayList<Record> records = new ArrayList<>();

            while (resultSet.next()) {
                Record record = new Record();
                record.setSongName(resultSet.getString(1));
                record.setArtists(resultSet.getString(2));
                record.setMovieName(resultSet.getString(3));
                record.setYear(resultSet.getInt(4));
                record.setLanguage(resultSet.getString(5));
                record.setRecordNo(resultSet.getInt(6));
                records.add(record);
            }

            return records;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public ArrayList<Song> getSongsForMovie(int recordNo) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT movie_name FROM Movies WHERE movie_record_no=?;");
            preparedStatement.setInt(1, recordNo);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return getSongsForMovie(resultSet.getString(1));
            } else {
                return null;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public HashSet<String> getAllRecordNumbers() {
        try {
            HashSet<String> allRecordNumbers = new HashSet<>();

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DISTINCT(movie_record_no) FROM Movies;");

            while (resultSet.next())
                allRecordNumbers.add(resultSet.getString(1));


            return allRecordNumbers;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
