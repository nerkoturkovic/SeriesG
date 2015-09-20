package es.usal.seriesg.service.tmdb.interfaces;

import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.model.MoviePageItem;
import es.usal.seriesg.service.tmdb.model.PageResult;
import es.usal.seriesg.service.tmdb.model.TMDBMovie;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by nerko on 1/7/15.
 */
public interface TmdbFilmService {

    @GET("/search/movie")
    PageResult<MoviePageItem> search(@Query(TMDBUtils.QUERY_PARAM) String query, @Query(TMDBUtils.PAGE_PARAM) int page);

    @GET("/movie/{id}")
    TMDBMovie get(@Path("id")Long id);
}
