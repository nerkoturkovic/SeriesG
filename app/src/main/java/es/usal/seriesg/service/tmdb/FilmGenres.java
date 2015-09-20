package es.usal.seriesg.service.tmdb;

import android.os.AsyncTask;
import android.util.LongSparseArray;

import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.service.tmdb.model.Genres;

/**
 * Created by nerko on 5/7/15.
 */
public class FilmGenres {
    private static FilmGenres ourInstance = new FilmGenres();

    private LongSparseArray<String> allGenres = new LongSparseArray<String>();

    public static FilmGenres getInstance() {
        return ourInstance;
    }

    private FilmGenres() {
    }

    public String getGenre(Long genreId) {
        return this.allGenres.get(genreId);
    }

    public void loadGenres() {
        new FindGenres().execute();
    }

    private class FindGenres extends AsyncTask<Void, Void, Genres> {
        @Override
        protected Genres doInBackground(Void... params) {
            return SeriesGApplication.getGenreService().getMovieGenres();
        }

        @Override
        protected void onPostExecute(Genres genres) {
            for(Genres.Genre genre : genres.getGenres()) {
                allGenres.put(genre.getId(), genre.getName());
            }
            super.onPostExecute(genres);
        }
    }
}
