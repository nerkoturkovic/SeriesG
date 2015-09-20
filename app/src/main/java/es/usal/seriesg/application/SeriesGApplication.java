package es.usal.seriesg.application;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.usal.seriesg.generated.dao.DaoMaster;
import es.usal.seriesg.generated.dao.DaoSession;
import es.usal.seriesg.generated.dao.EpisodeDao;
import es.usal.seriesg.generated.dao.MovieDao;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.service.tmdb.FilmGenres;
import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.TVGenres;
import es.usal.seriesg.service.tmdb.interfaces.GenreService;
import es.usal.seriesg.service.tmdb.interfaces.TmdbFilmService;
import es.usal.seriesg.service.tmdb.interfaces.TmdbSeriesService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by nerko on 7/6/15.
 */
public class SeriesGApplication extends Application {

    private static final String DATABASE_NAME = "SERIESGDB";

    private static Context mContext;
    private static AssetManager mAssets;
    private static Resources mResources;
    private static Resources.Theme mTheme;
    // Database classes
    private static DaoMaster daoMaster;
    private static SQLiteDatabase database;
    private static DaoMaster.DevOpenHelper devOpenHelper;
    private static DaoSession daoSession;
    // Rest classes
    private static RestAdapter mRestAdapter;
    private static TmdbSeriesService mTmdbSeriesService;
    private static TmdbFilmService mTmdbFilmService;
    private static GenreService mGenreService;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mContext = getApplicationContext();
        mAssets = getAssets();
        mResources = getResources();
        mTheme = getTheme();

        devOpenHelper = new DaoMaster.DevOpenHelper(mContext, DATABASE_NAME, null);
        database = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();

        createRestAdapter();
    }

    private void createRestAdapter() {

        final GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    @Override
                    public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                            throws JsonParseException {
                        try {
                            return df.parse(json.getAsString());
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                })
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.type());
                request.addQueryParam(TMDBUtils.API_KEY_PARAM, TMDBUtils.API_KEY_VALUE);
            }
        };

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(TMDBUtils.BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gsonBuilder.create()))
                .build();

        mTmdbSeriesService = mRestAdapter.create(TmdbSeriesService.class);
        mTmdbFilmService = mRestAdapter.create(TmdbFilmService.class);
        mGenreService = mRestAdapter.create(GenreService.class);

        if(isNetworkAvailable()) {
            TVGenres.getInstance().loadGenres();
            FilmGenres.getInstance().loadGenres();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static AssetManager getMAssets() {
        return mAssets;
    }

    public static Resources getMResources() {
        return mResources;
    }

    public static Resources.Theme getMTheme() {
        return mTheme;
    }

    public static TVSerieDao getTVSerieDao() {
        return daoSession.getTVSerieDao();
    }

    public static EpisodeDao getEpisodeDao() {
        return daoSession.getEpisodeDao();
    }

    public static MovieDao getMovieDao() {
        return daoSession.getMovieDao();
    }

    public static TmdbSeriesService getTmdbSeriesService() {
        return mTmdbSeriesService;
    }

    public static TmdbFilmService getTmdbFilmService() {
        return mTmdbFilmService;
    }

    public static GenreService getGenreService() {
        return mGenreService;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
}
