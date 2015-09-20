package es.usal.seriesg.async;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.MovieDao;
import es.usal.seriesg.generated.entities.Movie;
import es.usal.seriesg.service.tmdb.interfaces.TmdbFilmService;
import es.usal.seriesg.service.tmdb.model.ProductionCompany;
import es.usal.seriesg.service.tmdb.model.TMDBMovie;

/**
 * Created by nerko on 8/7/15.
 */
public class FindMovie extends AsyncTask<Long, Void, TMDBMovie> {

    private TmdbFilmService tmdbFilmService = SeriesGApplication.getTmdbFilmService();
    private MovieDao movieDao = SeriesGApplication.getMovieDao();
    private Movie movie;
    private RecyclerView.Adapter adapter;
    private int position;

    public FindMovie(Movie movie) {
        this.movie = movie;
    }

    public FindMovie(Movie movie, int position, RecyclerView.Adapter adapter) {
        this.movie = movie;
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected TMDBMovie doInBackground(Long... params) {
        return tmdbFilmService.get(params[0]);
    }

    @Override
    protected void onPostExecute(TMDBMovie tmdbMovie) {
        movie.setTitle(tmdbMovie.getTitle());
        movie.setStatus(tmdbMovie.getStatus());
        movie.setPosterPath(tmdbMovie.getPosterPath());
        movie.setReleaseDate(tmdbMovie.getReleaseDate());
        movie.setRuntime(tmdbMovie.getRuntime());
        List<String> productionCompanies = new ArrayList<String>();
        for(ProductionCompany pc : tmdbMovie.getProductionCompanies()) {
            productionCompanies.add(pc.getName());
        }
        movie.setProductionCompanies(Joiner.on(", ").join(productionCompanies));
        movieDao.insertOrReplace(movie);
        if(adapter != null) {
            adapter.notifyItemChanged(position);
        }
        super.onPostExecute(tmdbMovie);
    }
}
