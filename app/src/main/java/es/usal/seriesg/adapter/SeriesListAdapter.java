package es.usal.seriesg.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.activity.SerieDetailsActivity;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.async.FindTVSerie;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.generated.entities.TVSerie;
import es.usal.seriesg.service.tmdb.TMDBUtils;

/**
 * Created by nerko on 6/7/15.
 */
public class SeriesListAdapter extends RecyclerView.Adapter<SeriesListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.front_face)
        ImageView frontFace;
        @InjectView(R.id.title)
        TextView titleText;
        @InjectView(R.id.status)
        TextView statusText;
        @InjectView(R.id.favorite)
        ImageView favorite;
        @InjectView(R.id.network)
        TextView networkText;
        @InjectView(R.id.pop_menu)
        ImageView popMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private Context mContext;
    private List<TVSerie> tvSerieList;
    private TVSerieDao tvSerieDao;

    public SeriesListAdapter(Context context, List<TVSerie> tvSerieList) {
        this.mContext = context;
        this.tvSerieDao = SeriesGApplication.getTVSerieDao();
        this.tvSerieList = tvSerieList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_list_row, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SeriesGApplication.isNetworkAvailable()) {
                    Intent details = new Intent(mContext, SerieDetailsActivity.class);
                    details.putExtra("id", SeriesListAdapter.this.tvSerieList.get(vh.getAdapterPosition()).getTmdbId());
                    mContext.startActivity(details);
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setMessage(R.string.network_not_available).setNegativeButton(R.string.ok, null);
                    adb.create().show();
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TVSerie tvSerie = this.tvSerieList.get(position);

        Picasso
                .with(this.mContext)
                .load(TMDBUtils.BASE_IMAGES_URL_500 + tvSerie.getPosterPath())
                .error(R.drawable.ic_image_white_24dp)
                .fit()
                .into(holder.frontFace);

        holder.titleText.setText(tvSerie.getTitle());
        holder.statusText.setText(tvSerie.getStatus());
        holder.networkText.setText(tvSerie.getNetwork());

        if(tvSerie.getFavorite()) {
            holder.favorite.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        } else {
            holder.favorite.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
        }
        holder.favorite.setOnClickListener(new FavoriteClickListener(tvSerie));

        holder.popMenu.setOnClickListener(new PopMenuClickListener(tvSerie, position));
    }

    @Override
    public int getItemCount() {
        return this.tvSerieList.size();
    }

    private class FavoriteClickListener implements View.OnClickListener {

        private TVSerie tvSerie;

        public FavoriteClickListener(TVSerie tvSerie) {
            this.tvSerie = tvSerie;
        }

        @Override
        public void onClick(View v) {
            if(this.tvSerie.getFavorite()) {
                this.tvSerie.setFavorite(false);
                ((ImageView) v).setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                Toast.makeText(SeriesGApplication.getContext(), R.string.tv_not_favorite, Toast.LENGTH_SHORT).show();
            } else {
                this.tvSerie.setFavorite(true);
                ((ImageView) v).setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                Toast.makeText(SeriesGApplication.getContext(), R.string.tv_favorite, Toast.LENGTH_SHORT).show();
            }
            Collections.sort(SeriesListAdapter.this.tvSerieList, new Comparator<TVSerie>() {
                        @Override
                        public int compare(TVSerie lhs, TVSerie rhs) {
                            if(lhs.getFavorite() && !rhs.getFavorite()) {
                                return -1;
                            } else if (!lhs.getFavorite() && rhs.getFavorite()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
            );
            SeriesListAdapter.this.notifyDataSetChanged();
            SeriesListAdapter.this.tvSerieDao.insertOrReplace(tvSerie);
        }
    }

    private class PopMenuClickListener implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private TVSerie tvSerie;
        private int position;

        public PopMenuClickListener(TVSerie tvSerie, int position) {
            this.tvSerie = tvSerie;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popupMenu = new PopupMenu(SeriesGApplication.getContext(), v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.serie_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()) {
                case R.id.update:
                    if(SeriesGApplication.isNetworkAvailable()) {
                        new FindTVSerie(tvSerie, position, SeriesListAdapter.this).execute(tvSerie.getTmdbId());
                    }
                    return true;
                case R.id.delete:
                    tvSerie.setFollowing(false);
                    tvSerieDao.update(tvSerie);
                    //SeriesListAdapter.this.tvSerieDao.delete(tvSerie);
                    SeriesListAdapter.this.tvSerieList.remove(tvSerie);
                    SeriesListAdapter.this.notifyDataSetChanged();
                    return true;
            }
            return false;
        }
    }

}
