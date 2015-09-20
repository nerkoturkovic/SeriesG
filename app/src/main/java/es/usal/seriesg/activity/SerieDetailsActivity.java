package es.usal.seriesg.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.adapter.SeasonListAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.decorator.DividerItemDecoration;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.generated.entities.TVSerie;
import es.usal.seriesg.service.tmdb.TMDBUtils;
import es.usal.seriesg.service.tmdb.interfaces.TmdbSeriesService;
import es.usal.seriesg.service.tmdb.model.Genres;
import es.usal.seriesg.service.tmdb.model.ProductionCompany;
import es.usal.seriesg.service.tmdb.model.TMDBSerie;
import es.usal.seriesg.tabs.widget.SlidingTabLayout;

/**
 * Created by nerko on 10/7/15.
 */
public class SerieDetailsActivity extends FragmentActivity {

    private static final int TABS_NUM = 2;

    private static final int OVERVIEW = 0;
    private static final int SEASONS = 1;

    @InjectView(R.id.pager)
    ViewPager mViewPager;
    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.tabs)
    SlidingTabLayout mTabs;

    private SerieDetailPagerAdapter mSerieDetailPagerAdapter;
    private String[] tabsTitles;

    private TVSerieDao tvSerieDao;
    private TVSerie tvSerie;
    private static TMDBSerie tmdbSerie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.tabsTitles = getResources().getStringArray(R.array.serie_detail);

        long id = getIntent().getLongExtra("id", 0l);

        this.tvSerieDao = SeriesGApplication.getTVSerieDao();
        this.tvSerie = this.tvSerieDao.load(id);

        try {
            this.tmdbSerie = new LoadSerie().execute(this.tvSerie).get();
        } catch (ExecutionException | InterruptedException e) {
            this.finish();
        }

        setContentView(R.layout.serie_detail);
        ButterKnife.inject(this);

        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mSerieDetailPagerAdapter = new SerieDetailPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSerieDetailPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mViewPager);


        this.setTitle(tmdbSerie.getName());

    }


    /**
     * Fragments, adapter and asynctask used for this activity
     */
    private class LoadSerie extends AsyncTask<TVSerie, Void, TMDBSerie> {

        private ProgressDialog progressDialog;

        public LoadSerie() {
            progressDialog = new ProgressDialog(SerieDetailsActivity.this);
            progressDialog.setMessage(SerieDetailsActivity.this.getResources().getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected TMDBSerie doInBackground(TVSerie... params) {
            TmdbSeriesService tmdbSeriesService = SeriesGApplication.getTmdbSeriesService();
            return tmdbSeriesService.get(params[0].getTmdbId());
        }

        @Override
        protected void onPostExecute(TMDBSerie tmdbSerie) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private class SerieDetailPagerAdapter extends FragmentPagerAdapter {
        public SerieDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TABS_NUM;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case OVERVIEW:
                    return OverviewFragment.newInstance();
                case SEASONS:
                    return SeasonsFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabsTitles[position];
        }
    }

    public static class OverviewFragment extends Fragment {

        @InjectView(R.id.backdrop)
        ImageView backdropImage;
        @InjectView(R.id.rating)
        TextView ratingText;
        @InjectView(R.id.overview)
        TextView overviewText;
        @InjectView(R.id.status)
        TextView statusText;
        @InjectView(R.id.network)
        TextView networkText;
        @InjectView(R.id.genres)
        TextView genresText;
        @InjectView(R.id.air_date)
        TextView airDateText;
        @InjectView(R.id.produced_by)
        TextView producedByText;
        @InjectView(R.id.production_companies)
        TextView productionCompaniesText;

        public static OverviewFragment newInstance() {
            return new OverviewFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.serie_overview, container, false);
            ButterKnife.inject(this, v);

            Picasso.with(getActivity())
                    .load(TMDBUtils.BASE_IMAGES_URL_500 +
                            (Strings.isNullOrEmpty(tmdbSerie.getBackdropPath()) ? tmdbSerie.getPosterPath() : tmdbSerie.getBackdropPath()))
                    .error(R.drawable.ic_image_white_24dp)
                    .fit()
                    .into(this.backdropImage);
            this.overviewText.setText(tmdbSerie.getOverview());
            this.statusText.setText(tmdbSerie.getStatus());
            if(tmdbSerie.getNetworks()!= null && !tmdbSerie.getNetworks().isEmpty()) {
                this.networkText.setText(tmdbSerie.getNetworks().get(0).getName());
            }
            List<String> genres = new ArrayList<String>();
            for(Genres.Genre g : tmdbSerie.getGenres()) {
                genres.add(g.getName());
            }
            this.genresText.setText(Joiner.on(", ").join(genres));
            Calendar c = Calendar.getInstance();
            c.setTime(tmdbSerie.getFirstAirDate());
            this.airDateText.setText(String.valueOf(c.get(Calendar.YEAR)));
            List<String> productionCompanies = new ArrayList<String>();
            for(ProductionCompany pc : tmdbSerie.getProductionCompanies()) {
                productionCompanies.add(pc.getName());
            }
            if(!productionCompanies.isEmpty()) {
                this.producedByText.setVisibility(View.VISIBLE);
                this.productionCompaniesText.setText(Joiner.on(", ").join(productionCompanies));
            }
            if(tmdbSerie.getVoteCount() == 0D) {
                this.ratingText.setVisibility(View.GONE);
            }
            this.ratingText.setText(tmdbSerie.getVoteAverage() + " / " + tmdbSerie.getVoteCount() + " votes");

            return v;
        }
    }

    public static class SeasonsFragment extends Fragment {

        public static final int REQUEST_CODE = 1;

        @InjectView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private SeasonListAdapter mAdapter;

        public static SeasonsFragment newInstance() {
            return new SeasonsFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.recycler_view, container, false);
            ButterKnife.inject(this, v);

            LinearLayoutManager llm = new LinearLayoutManager(SeriesGApplication.getContext(), OrientationHelper.VERTICAL, false);
            this.mAdapter = new SeasonListAdapter(getActivity(), tmdbSerie.getSeasons(), SerieDetailsActivity.tmdbSerie.getId(), this);
            this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
            this.mRecyclerView.setLayoutManager(llm);
            this.mRecyclerView.setHasFixedSize(true);
            this.mRecyclerView.setAdapter(mAdapter);

            return v;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == REQUEST_CODE) {
                int position = data.getIntExtra("position", 0);
                this.mAdapter.notifyItemChanged(position);
            }
        }
    }
}
