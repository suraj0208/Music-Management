import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by suraj on 7/6/17.
 */
public class Main {

    public static void main(String[] args) {
        DatabaseHelper databaseHelper = new DatabaseHelper();

        Scanner scanner = new Scanner(System.in);

        int choice;

        while (true) {

            /*
            * 0 - add language
            * 1 - add artist
            * 2 - add lyricist
            * 3 - add movie
            * 4 - add song
            * 5 - search for songs - by movie
            * 6 - search for songs - by name
            * 99 - exit
            * */

            int i;
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();

            if (choice == 99)
                break;

            switch (choice) {

                case 0:
                    System.out.print("Enter language name: ");
                    Language language = new Language(scanner.next());
                    databaseHelper.addLanguage(language);
                    break;

                case 1:
                    System.out.print("Enter artist name: ");
                    Artist artist = new Artist(scanner.next());
                    databaseHelper.addArtist(artist);
                    break;

                case 2:
                    System.out.print("Enter lyricist name: ");
                    Lyricist lyricist = new Lyricist(scanner.next());
                    databaseHelper.addLyricist(lyricist);
                    break;

                case 3:
                    ArrayList<Language> languageArrayList = databaseHelper.getAvailableLanguages();

                    if (languageArrayList == null) {

                        break;
                    }

                    for (Language language1 : languageArrayList)
                        System.out.println(language1.getName());

                    System.out.print("Enter language: ");

                    String selectedLanguage = scanner.next();

                    for (i = 0; i < languageArrayList.size(); i++) {
                        if (languageArrayList.get(i).getName().equals(selectedLanguage)) {
                            break;
                        }
                    }

                    if (i == languageArrayList.size()) {
                        System.out.println("Language not available");
                        break;
                    }

                    System.out.print("Enter movie name: ");
                    String movieName = scanner.next();

                    System.out.print("Enter movie year: ");
                    int movieYear = scanner.nextInt();

                    System.out.print("Enter movie record no: ");
                    int movieRecordNo = scanner.nextInt();

                    Movie movie = new Movie(movieName, movieYear, languageArrayList.get(i).getId(), movieRecordNo);
                    databaseHelper.addMovie(movie);
                    break;

                case 4:
                    ArrayList<Movie> movieArrayList = databaseHelper.getAvailableMovies();

                    if (movieArrayList == null)
                        break;

                    for (Movie movie1 : movieArrayList)
                        System.out.println(movie1.getName() + " " + movie1.getYear() + " " + movie1.getLanguage());

                    System.out.print("Enter movie name: ");

                    String selectedMovie = scanner.next();

                    for (i = 0; i < movieArrayList.size(); i++) {
                        if (movieArrayList.get(i).getName().equals(selectedMovie)) {
                            break;
                        }
                    }

                    if (i == movieArrayList.size()) {
                        System.out.println("Movie not available");
                        break;
                    }

                    int movieIndex=i;

                    ArrayList<Artist> artistArrayList = databaseHelper.getAvailableArtists();

                    if (artistArrayList == null)
                        break;

                    for (Artist artist1 : artistArrayList)
                        System.out.println(artist1.getName());

                    ArrayList<Artist> artistsForSong = new ArrayList<>();

                    for(int j=0;j<2;j++){
                        System.out.print("Enter artist name: ");

                        String selectedArtist = scanner.next();

                        for (i = 0; i < artistArrayList.size(); i++) {
                            if (artistArrayList.get(i).getName().equals(selectedArtist)) {
                                break;
                            }
                        }

                        if (i == artistArrayList.size()) {
                            System.out.println("Artist not available");
                            break;
                        }

                        Artist artist1 = artistArrayList.get(i);

                        artistsForSong.add(artist1);
                    }

                    System.out.print("Enter song name: ");
                    String songName = scanner.next();

                    Song song = new Song(songName, movieArrayList.get(movieIndex).getId(),artistsForSong);

                    databaseHelper.addSong(song);

                    break;

                case 5:

                    break;

                case 6:
                    String searchSongName = scanner.next();
                    databaseHelper.findSong(searchSongName);
                    break;

            }

        }

        ArrayList<Movie> movieArrayList = databaseHelper.getAvailableMovies();

        int movieIndex=0;

        ArrayList<Artist> artistArrayList = databaseHelper.getAvailableArtists();

        ArrayList<Artist> artistsForSong = new ArrayList<>();


        artistsForSong.add(artistArrayList.get(0));




        for(int i=0;i<20000;i++){
            Song song = new Song("s" + i, movieArrayList.get(movieIndex).getId(),artistsForSong);
            databaseHelper.addSong(song);
        }



    }
}
