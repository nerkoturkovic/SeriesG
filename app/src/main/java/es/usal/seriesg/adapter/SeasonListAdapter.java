package es.usal.seriesg.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.activity.SerieDetailsActivity;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.fragments.EpisodesDialogFragment;
import es.usal.seriesg.generated.dao.EpisodeDao;
import es.usal.seriesg.generated.entities.Episode;
import es.usal.seriesg.service.tmdb.model.SeasonListItem;

/**
 * Created by nerko on 10/7/15.
 */
public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.ViewHolder> {

    private static final String TAG = "EPISODES_LIST_DIALOG";

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.season_name)
        TextView seasonNameText;
        @InjectView(R.id.episodes_count)
        TextView episodesCountText;
        @InjectView(R.id.release_date)
        TextView releaseDateText;
        @InjectView(R.id.episodes_progress)
        ProgressBar episodeProgress;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    private Context mContext;
    private List<SeasonListItem> mSeasonList;
    private Fragment targetFragment;
    private EpisodeDao mEpisodeDao;
    private Long serieId;

    public SeasonListAdapter(Context context, List<SeasonListItem> seasonList, Long serieId, Fragment targetFragment) {
        this.mContext = context;
        this.mSeasonList = seasonList;
        this.serieId = serieId;
        this.targetFragment = targetFragment;
        this.mEpisodeDao = SeriesGApplication.getEpisodeDao();
    }

    @Override
    public int getItemCount() {
        return this.mSeasonList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SeasonListItem seasonListItem = this.mSeasonList.get(position);
        List<Episode> episodes = this.mEpisodeDao.queryBuilder()
                .where(EpisodeDao.Properties.SeasonId.eq(seasonListItem.getId()))
                .list();
        holder.seasonNameText.setText(this.mContext.getResources().getString(R.string.season) + " " + seasonListItem.getSeasonNumber());
        holder.episodesCountText.setText(String.format("%d / %d", episodes.size(), seasonListItem.getEpisodeCount()));
        if(seasonListItem.getAirDate() != null) {
            holder.releaseDateText.setText(new SimpleDateFormat("d/M/yyyy").format(seasonListItem.getAirDate()));
        }
        holder.episodeProgress.setMax(seasonListItem.getEpisodeCount());
        holder.episodeProgress.setProgress(episodes.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_list_row, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
                SeasonListItem seasonListItem = SeasonListAdapter.this.mSeasonList.get(position);
                EpisodesDialogFragment dialog = new EpisodesDialogFragment();
                dialog.setTargetFragment(targetFragment, SerieDetailsActivity.SeasonsFragment.REQUEST_CODE);
                Bundle args = new Bundle();
                args.putLong("id", SeasonListAdapter.this.serieId);
                args.putInt("seasonNumber", seasonListItem.getSeasonNumber());
                dialog.setArguments(args);
                dialog.show(((SerieDetailsActivity) mContext).getSupportFragmentManager(), TAG);
            }
        });
        return vh;
    }

}
