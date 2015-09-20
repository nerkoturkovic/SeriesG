package es.usal.seriesg.service.tmdb.interfaces;

import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.model.PageResult;
import es.usal.seriesg.service.tmdb.model.TMDBSeason;
import es.usal.seriesg.service.tmdb.model.TMDBSerie;
import es.usal.seriesg.service.tmdb.model.TVPageItem;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Created by nerko on 7/6/15.
 */
public interface TmdbSeriesService {

    @GET("/search/tv")
    PageResult<TVPageItem> search(@Query(TMDBUtils.QUERY_PARAM) String query);

    @GET("/search/tv")
    PageResult<TVPageItem> search(@Query(TMDBUtils.QUERY_PARAM) String query, @Query(TMDBUtils.PAGE_PARAM) int page);

    @GET("/tv/{id}")
    TMDBSerie get(@Path("id") long id);

    @GET("/tv/{id}/season/{seasonNumber}")
    TMDBSeason getSeason(@Path("id") long id, @Path("seasonNumber") int seasonNumber);


}
