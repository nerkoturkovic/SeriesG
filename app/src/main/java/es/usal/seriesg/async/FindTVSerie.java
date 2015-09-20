package es.usal.seriesg.async;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.generated.entities.TVSerie;
import es.usal.seriesg.service.tmdb.interfaces.TmdbSeriesService;
import es.usal.seriesg.service.tmdb.model.TMDBSerie;

/**
 * Created by nerko on 7/7/15.
 */
public class FindTVSerie extends AsyncTask<Long, Void, TMDBSerie> {

    private TmdbSeriesService tmdbSeriesService = SeriesGApplication.getTmdbSeriesService();
    private TVSerieDao tvSerieDao = SeriesGApplication.getTVSerieDao();
    private TVSerie tvSerie;
    private RecyclerView.Adapter adapter;
    private int position;

    public FindTVSerie(TVSerie tvSerie) {
        this.tvSerie = tvSerie;
    }

    public FindTVSerie(TVSerie tvSerie, int position, RecyclerView.Adapter adapter) {
        this.tvSerie = tvSerie;
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected TMDBSerie doInBackground(Long... params) {
        return tmdbSeriesService.get(params[0]);
    }

    @Override
    protected void onPostExecute(TMDBSerie tvItem) {
        tvSerie.setPosterPath(tvItem.getPosterPath());
        if(tvItem.getNetworks() != null && !tvItem.getNetworks().isEmpty()) {
            tvSerie.setNetwork(tvItem.getNetworks().get(0).getName());
        }
        tvSerie.setStatus(tvItem.getStatus());
        tvSerie.setTitle(tvItem.getName());
        tvSerieDao.insertOrReplace(tvSerie);
        if(adapter != null) {
            adapter.notifyItemChanged(position);
        }
        super.onPostExecute(tvItem);
    }
}