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
import es.usal.seriesg.async.FindTVSerie;
import es.usal.seriesg.fragments.SearchDialogFragment;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.generated.entities.TVSerie;
import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.model.TVPageItem;

/**
 * Created by nerko on 2/7/15.
 */
public class SearchSeriesAdapter extends RecyclerView.Adapter<SearchSeriesAdapter.ViewHolder> {

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

    private List<TVPageItem> tvPageItemList;
    private TVSerieDao tvSeriesDao;
    private Context mContext;

    public SearchSeriesAdapter(List<TVPageItem> tvPageItemList, Context context) {
        this.tvPageItemList = tvPageItemList;
        this.tvSeriesDao = SeriesGApplication.getTVSerieDao();
        this.mContext = context;
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
                    TVPageItem tvPageItem = tvPageItemList.get(position);
                    Bundle args = new Bundle();
                    args.putInt(SearchDialogFragment.TYPE_PARAM, SearchDialogFragment.TV_SERIE_TYPE);
                    args.putString(SearchDialogFragment.IMAGE_URL_PARAM, tvPageItem.getPosterPath());
                    args.putString(SearchDialogFragment.TITLE_PARAM, tvPageItem.getName());
                    args.putDouble(SearchDialogFragment.RATING_PARAM, tvPageItem.getVoteAverage());
                    args.putString(SearchDialogFragment.OVERVIEW_PARAM, tvPageItem.getOverview());
                    if(tvPageItem.getFirstAirDate() != null) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(tvPageItem.getFirstAirDate());
                        args.putString(SearchDialogFragment.FIRST_AIR_DATE_PARAM, String.valueOf(c.get(Calendar.YEAR)));
                    }
                    long[] genres = new long[tvPageItem.getGenreIds().size()];
                    for(int i=0;i<tvPageItem.getGenreIds().size();i++) {
                        genres[i] = tvPageItem.getGenreIds().get(i);
                    }
                    args.putLongArray(SearchDialogFragment.GENRES_PARAM, genres);
                    SearchDialogFragment sdf = new SearchDialogFragment();
                    sdf.setArguments(args);
                    sdf.show(((FragmentActivity) mContext).getSupportFragmentManager(), SearchDialogFragment.TAG_NAME);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TVPageItem tvPageItem = this.tvPageItemList.get(position);
        TVSerie tvSerie = tvSeriesDao.load(tvPageItem.getId());
        if(tvSerie == null) {
            tvSerie = new TVSerie(tvPageItem.getId());
        }
        Picasso.with(SeriesGApplication.getContext())
                .load(TMDBUtils.BASE_IMAGES_URL_342 + tvPageItem.getPosterPath())
                .error(R.drawable.ic_image_white_24dp)
                .fit()
                .into(holder.frontFace);
        holder.title.setText(tvPageItem.getName());
        if(tvPageItem.getFirstAirDate()!=null) {
            Calendar c = Calendar.getInstance();
            c.setTime(tvPageItem.getFirstAirDate());
            holder.airDate.setText(String.valueOf(c.get(Calendar.YEAR)));
        } else {
            holder.airDate.setText("");
        }
        if(tvSerie.getFollowing()) {
            holder.watched.setImageResource(R.drawable.ic_visibility_white_24dp);
        } else {
            holder.watched.setImageResource(R.drawable.ic_visibility_off_white_24dp);
        }

        holder.watched.setOnClickListener(new OnWatchedClickListener(tvSerie));
    }

    @Override
    public int getItemCount() {
        return tvPageItemList.size();
    }


    private class OnWatchedClickListener implements View.OnClickListener {
        private TVSerie tvSerie;

        public OnWatchedClickListener(TVSerie tvSerie) {
            this.tvSerie = tvSerie;
        }

        @Override
        public void onClick(View v) {
            if(tvSerie.getFollowing()) {
                tvSerie.setFollowing(false);
                ((ImageView) v).setImageResource(R.drawable.ic_visibility_off_white_24dp);
                Toast.makeText(mContext, R.string.tv_marked_not_following, Toast.LENGTH_SHORT).show();
                tvSeriesDao.insertOrReplace(tvSerie);
            } else {
                tvSerie.setFollowing(true);
                ((ImageView) v).setImageResource(R.drawable.ic_visibility_white_24dp);
                Toast.makeText(mContext, R.string.tv_marked_following, Toast.LENGTH_SHORT).show();
                new FindTVSerie(tvSerie).execute(tvSerie.getTmdbId());
            }
        }
    }
}
