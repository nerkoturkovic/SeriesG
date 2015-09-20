package es.usal.seriesg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.MovieDao;
import es.usal.seriesg.generated.entities.Movie;
import es.usal.seriesg.service.tmdb.TMDBUtils;

/**
 * Created by nerko on 8/7/15.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.front_face)
        ImageView frontFace;
        @InjectView(R.id.title)
        TextView titleText;
        @InjectView(R.id.release_date)
        TextView releaseDateText;
        @InjectView(R.id.runtime)
        TextView runtimeText;
        @InjectView(R.id.production_companies)
        TextView productionCompaniesText;
        @InjectView(R.id.delete)
        ImageView deleteImage;
        //@InjectView(R.id.seen)
        //ImageView seenImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private Context mContext;
    private List<Movie> movieList;
    private MovieDao movieDao;

    public MovieListAdapter(Context context, List<Movie> movieList) {
        this.mContext = context;
        this.movieList = movieList;
        this.movieDao = SeriesGApplication.getMovieDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = this.movieList.get(position);

        Picasso.with(this.mContext)
                .load(TMDBUtils.BASE_IMAGES_URL_500 + movie.getPosterPath())
                .fit()
                .error(R.drawable.ic_image_white_24dp)
                .into(holder.frontFace);

        holder.titleText.setText(movie.getTitle());
        holder.releaseDateText.setText(new SimpleDateFormat("d/M/yyyy").format(movie.getReleaseDate()));
        holder.runtimeText.setText(movie.getRuntime() + " " + this.mContext.getResources().getString(R.string.minutes));
        holder.productionCompaniesText.setText(movie.getProductionCompanies());

        /*if(movie.getSeen()) {
            holder.seenImage.setImageResource(R.drawable.ic_visibility_white_18dp);
        } else {
            holder.seenImage.setImageResource(R.drawable.ic_visibility_off_white_18dp);
        }
        holder.seenImage.setOnClickListener(new OnWatchedClickListener(movie));*/

        holder.deleteImage.setOnClickListener(new OnDeleteClickListener(movie, position));

    }

    @Override
    public int getItemCount() {
        return movieList.size();
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
            } else {
                movie.setSeen(true);
                ((ImageView) v).setImageResource(R.drawable.ic_visibility_white_24dp);
                Toast.makeText(mContext, R.string.film_marked_seen, Toast.LENGTH_SHORT).show();
            }
            movieDao.insertOrReplace(movie);
        }
    }

    private class OnDeleteClickListener implements View.OnClickListener {

        private Movie movie;
        private int position;

        public OnDeleteClickListener(Movie movie, int position) {
            this.movie = movie;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            MovieListAdapter.this.movieDao.delete(movie);
            MovieListAdapter.this.movieList.remove(movie);
            MovieListAdapter.this.notifyItemRemoved(position);
        }
    }
}
