package es.usal.seriesg.service.tmdb.interfaces;

import es.usal.seriesg.service.tmdb.model.Genres;
import retrofit.http.GET;

/**
 * Created by nerko on 5/7/15.
 */
public interface GenreService {

    @GET("/genre/tv/list")
    Genres getTvGenres();


    @GET("/genre/movie/list")
    Genres getMovieGenres();
}
