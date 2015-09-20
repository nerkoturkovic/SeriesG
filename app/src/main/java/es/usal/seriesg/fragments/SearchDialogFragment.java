package es.usal.seriesg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.TVGenres;

/**
 * Created by nerko on 5/7/15.
 */
public class SearchDialogFragment extends DialogFragment {

    public static final String TAG_NAME = "search_dialog_fragment";

    public static final int TV_SERIE_TYPE = 0;
    public static final int FILM_TYPE = 1;
    public static final String IMAGE_URL_PARAM = "IMAGE_URL_PARAM";
    public static final String RATING_PARAM = "RATING_PARAM";
    public static final String TITLE_PARAM = "TITLE_PARAM";
    public static final String OVERVIEW_PARAM = "OVERVIEW_PARAM";
    public static final String FIRST_AIR_DATE_PARAM = "FIRST_AIR_DATE_PARAM";
    public static final String GENRES_PARAM = "GENRES_PARAM";
    public static final String TYPE_PARAM = "TYPE_PARAM";


    private int type;
    private String imageURL;
    private Double rating;
    private String title;
    private String overview;
    private String airDate;
    private String genres;

    @InjectView(R.id.background_image)
    ImageView backgroundImage;
    @InjectView(R.id.overview)
    TextView overviewText;
    @InjectView(R.id.air_date_label)
    TextView airDateLabel;
    @InjectView(R.id.first_air_date)
    TextView firstAirDateText;
    @InjectView(R.id.rating)
    TextView ratingText;
    @InjectView(R.id.genres)
    TextView genresText;

    public SearchDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.type = args.getInt(TYPE_PARAM);
        this.imageURL = args.getString(IMAGE_URL_PARAM);
        this.rating = args.getDouble(RATING_PARAM, 0D);
        this.title = args.getString(TITLE_PARAM);
        this.overview = args.getString(OVERVIEW_PARAM);
        this.airDate = args.getString(FIRST_AIR_DATE_PARAM);
        long[] genreIds = args.getLongArray(GENRES_PARAM);
        List<String> genresString = new ArrayList<String>();
        if(this.type == TV_SERIE_TYPE) {
            for(Long genreId : genreIds) {
                genresString.add(TVGenres.getInstance().getGenre(genreId));
            }
        } else {
            for(Long genreId : genreIds) {
                genresString.add(TVGenres.getInstance().getGenre(genreId));
            }
        }
        genres = Joiner.on(", ").skipNulls().join(genresString.iterator());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_dialog, container);
        ButterKnife.inject(this, v);

        Picasso.with(SeriesGApplication.getContext())
                .load(TMDBUtils.BASE_IMAGES_URL_342 + this.imageURL)
                .error(R.drawable.ic_image_white_24dp)
                .fit()
                .into(this.backgroundImage);

        getDialog().setTitle(this.title);
        if(type == SearchDialogFragment.FILM_TYPE) {
            this.airDateLabel.setText(R.string.release_date);
        }
        this.overviewText.setText(this.overview);
        this.firstAirDateText.setText(this.airDate);
        this.ratingText.setText(this.rating.toString());
        this.genresText.setText(this.genres);

        return v;
    }
}
