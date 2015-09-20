package es.usal.seriesg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.EpisodeDao;
import es.usal.seriesg.generated.entities.Episode;
import es.usal.seriesg.service.tmdb.model.EpisodeListItem;

/**
 * Created by nerko on 12/7/15.
 */
public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.seen)
        ImageView seenImage;
        @InjectView(R.id.name)
        TextView nameText;
        @InjectView(R.id.air_date)
        TextView airDateText;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    private Context context;
    private List<EpisodeListItem> episodesList;
    private EpisodeDao episodeDao;
    private Long seasonId;

    public EpisodesAdapter(Context context, List<EpisodeListItem> episodesList, Long seasonId) {
        this.context = context;
        this.episodesList = episodesList;
        this.seasonId = seasonId;
        this.episodeDao = SeriesGApplication.getEpisodeDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EpisodeListItem episodeItem = this.episodesList.get(position);
        Episode episode = this.episodeDao.load(episodeItem.getId());
        if(episode != null) {
            holder.seenImage.setImageResource(R.drawable.ic_check_box_white_24dp);
        } else {
            holder.seenImage.setImageResource(R.drawable.ic_check_white_24dp);
        }
        holder.seenImage.setOnClickListener(new OnSeenClickListener(position));

        holder.nameText.setText(episodeItem.getName());
        if(episodeItem.getAirDate() != null) {
            holder.airDateText.setText(new SimpleDateFormat("d/M/yyyy").format(episodeItem.getAirDate()));
        }
    }

    @Override
    public int getItemCount() {
        return this.episodesList.size();
    }

    private class OnSeenClickListener implements View.OnClickListener {

        private int position;

        public OnSeenClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            EpisodeListItem episodeItem = EpisodesAdapter.this.episodesList.get(position);
            Episode episode = EpisodesAdapter.this.episodeDao.load(episodeItem.getId());
            if(episode == null) {
                ((ImageView) v).setImageResource(R.drawable.ic_check_box_white_24dp);
                episode = new Episode(episodeItem.getId());
                episode.setSeasonId(EpisodesAdapter.this.seasonId);
                EpisodesAdapter.this.episodeDao.insertOrReplace(episode);
            } else {
                ((ImageView) v).setImageResource(R.drawable.ic_check_white_24dp);
                EpisodesAdapter.this.episodeDao.delete(episode);
            }
        }
    }
}
