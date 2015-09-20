package es.usal.seriesg.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.async.FindMovie;
import es.usal.seriesg.fragments.SearchDialogFragment;
import es.usal.seriesg.generated.dao.MovieDao;
import es.usal.seriesg.generated.entities.Movie;
import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.model.MoviePageItem;

/**
 * Created by nerko on 1/7/15.
 */
public class SearchFilmsAdapter extends RecyclerView.Adapter<SearchFilmsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.card_view)
        CardView cardView;
        @InjectView(R.id.front_face)
        ImageView frontFace;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.air_date)
        TextView airDate;
        @InjectView(R.id.watched)
        ImageView watched;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private List<MoviePageItem> moviePageItemList;
    private MovieDao movieDao;
    private Context mContext;

    public SearchFilmsAdapter(List<MoviePageItem> moviePageItemList, Context context) {
        this.moviePageItemList = moviePageItemList;
        this.mContext = context;
        this.movieDao = SeriesGApplication.getMovieDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = vh.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MoviePageItem moviePageItem = moviePageItemList.get(position);
                    Bundle args = new Bundle();
                    args.putInt(SearchDialogFragment.TYPE_PARAM, SearchDialogFragment.FILM_TYPE);
                    args.putString(SearchDialogFragment.IMAGE_URL_PARAM, moviePageItem.getPosterPath());
                    args.putString(SearchDialogFragment.TITLE_PARAM, moviePageItem.getTitle());
                    args.putDouble(SearchDialogFragment.RATING_PARAM, moviePageItem.getVoteAverage());
                    args.putString(SearchDialogFragment.OVERVIEW_PARAM, moviePageItem.getOverview());
                    if(moviePageItem.getReleaseDate() != null) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(moviePageItem.getReleaseDate());
                        args.putString(SearchDialogFragment.FIRST_AIR_DATE_PARAM, String.valueOf(c.get(Calendar.YEAR)));
                    }
                    long[] genres = new long[moviePageItem.getGenreIds().size()];
                    for(int i=0;i< moviePageItem.getGenreIds().size();i++) {
                        genres[i] = moviePageItem.getGenreIds().get(i);
                    }
                    args.putLongArray(SearchDialogFragment.GENRES_PARAM, genres);
                    SearchDialogFragment sdf = new SearchDialogFragment();
                    sdf.setArguments(args);
                    sdf.show(((FragmentActivity) mContext).getSupportFragmentManager(), SearchDialogFragment.TAG_NAME);
                }
            }
        });
        v.setClickable(true);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MoviePageItem moviePageItem = this.moviePageItemList.get(position);
        Movie movie = movieDao.load(moviePageItem.getId());
        if(movie == null) {
            movie = new Movie(moviePageItem.getId());
        }
        Picasso.with(SeriesGApplication.getContext())
                .load(TMDBUtils.BASE_IMAGES_URL_342 + moviePageItem.getPosterPath())
                .fit()
                .error(R.drawable.ic_image_white_24dp)
                .into(holder.frontFace);
        holder.title.setText(moviePageItem.getTitle());
        if(moviePageItem.getReleaseDate()!=null) {
            Calendar c = Calendar.getInstance();
            c.setTime(moviePageItem.getReleaseDate());
            holder.airDate.setText(String.valueOf(c.get(Calendar.YEAR)));
        } else {
            holder.airDate.setText("");
        }
        if(movie.getSeen()) {
            holder.watched.setImageResource(R.drawable.ic_visibility_white_24dp);
        } else {
            holder.watched.setImageResource(R.drawable.ic_visibility_off_white_24dp);
        }

        holder.watched.setOnClickListener(new OnWatchedClickListener(movie));
    }

    @Override
    public int getItemCount() {
        return moviePageItemList.size();
    }

    private class OnWatchedClickListener implements View.OnClickListener{

        private Movie movie;

        public OnWatchedClickListener(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View v) {
            if(movie.getSeen()) {
                movie.setSeen(false);
                ((ImageView) v).setImageResource(R.drawable.ic_visibility_off_white_24dp);
                Toast.makeText(mContext, R.string.film_marked_not_seen, Toast.LENGTH_SHORT).show();
                movieDao.insertOrReplace(movie);
            } else {
                movie.setSeen(true);
                ((ImageView) v).setImageResource(R.drawable.ic_visibility_white_24dp);
                Toast.makeText(mContext, R.string.film_marked_seen, Toast.LENGTH_SHORT).show();
                new FindMovie(movie).execute(movie.getTmdbId());
            }
        }
    }
}
