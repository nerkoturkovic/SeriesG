package es.usal.seriesg.service.tmdb;

import android.os.AsyncTask;
import android.util.LongSparseArray;

import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.service.tmdb.model.Genres;

/**
 * Created by nerko on 5/7/15.
 */
public class TVGenres {
    private static TVGenres ourInstance = new TVGenres();

    private LongSparseArray<String> allGenres = new LongSparseArray<String>();

    public static TVGenres getInstance() {
        return ourInstance;
    }

    private TVGenres() {
    }

    public String getGenre(long genreId) {
        return this.allGenres.get(genreId);
    }

    public void loadGenres() {
        new FindGenres().execute();
    }

    private class FindGenres extends AsyncTask<Void, Void, Genres> {
        @Override
        protected Genres doInBackground(Void... params) {
            return SeriesGApplication.getGenreService().getTvGenres();
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
