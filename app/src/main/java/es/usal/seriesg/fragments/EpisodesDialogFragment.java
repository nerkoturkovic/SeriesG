package es.usal.seriesg.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.activity.SerieDetailsActivity;
import es.usal.seriesg.adapter.EpisodesAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.decorator.DividerItemDecoration;
import es.usal.seriesg.service.tmdb.interfaces.TmdbSeriesService;
import es.usal.seriesg.service.tmdb.model.EpisodeListItem;
import es.usal.seriesg.service.tmdb.model.TMDBSeason;

/**
 * Created by nerko on 12/7/15.
 */
public class EpisodesDialogFragment extends DialogFragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @InjectView(R.id.no_episodes)
    TextView noEpisodesText;

    private List<EpisodeListItem> episodes;
    private EpisodesAdapter mAdapter;
    private Long serieId;
    private Integer seasonNumber;
    private Long seasonId;
    private TmdbSeriesService tmdbSeriesService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.serieId = getArguments().getLong("id", 0L);
        this.seasonNumber = getArguments().getInt("seasonNumber", 0);
        this.tmdbSeriesService = SeriesGApplication.getTmdbSeriesService();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.episodes_dialog, container, false);
        ButterKnife.inject(this, v);

        getDialog().setTitle(getActivity().getString(R.string.season) + " " + this.seasonNumber);

        setupView();

        try {
            this.seasonId = new GetSeason().execute().get().getId();
        } catch (ExecutionException | InterruptedException e) {
            this.dismiss();
        }

        return v;
    }

    private void setupView() {
        LinearLayoutManager llm = new LinearLayoutManager(SeriesGApplication.getContext(), OrientationHelper.VERTICAL, false);
        this.mRecyclerView.setLayoutManager(llm);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
    }

    private void updateView(List<EpisodeListItem> episodes) {
        this.episodes = episodes;
        this.mAdapter = new EpisodesAdapter(getActivity(), this.episodes, this.seasonId);
        this.mRecyclerView.setAdapter(mAdapter);

        this.mProgressBar.setVisibility(View.GONE);
        this.mRecyclerView.setVisibility(View.VISIBLE);
        if(this.episodes.isEmpty()) {
            this.noEpisodesText.setVisibility(View.VISIBLE);
        }
    }

    private class GetSeason extends AsyncTask<Void, Void, TMDBSeason> {
        @Override
        protected TMDBSeason doInBackground(Void... params) {
            TMDBSeason season = EpisodesDialogFragment.this.tmdbSeriesService.getSeason(EpisodesDialogFragment.this.serieId, EpisodesDialogFragment.this.seasonNumber);
            return season;
        }

        @Override
        protected void onPostExecute(TMDBSeason tmdbSeason) {
            super.onPostExecute(tmdbSeason);
            updateView(tmdbSeason.getEpisodes());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent data = new Intent();
        data.putExtra("position", this.seasonNumber);
        getTargetFragment().onActivityResult(getTargetRequestCode(), SerieDetailsActivity.SeasonsFragment.REQUEST_CODE, data);
    }
}
