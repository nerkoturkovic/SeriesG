package es.usal.seriesg.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.adapter.SearchFilmsAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.service.tmdb.interfaces.TmdbFilmService;
import es.usal.seriesg.service.tmdb.model.MoviePageItem;
import es.usal.seriesg.service.tmdb.model.PageResult;

/**
 * Created by nerko on 30/6/15.
 */
public class SearchFilmFragment extends Fragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.no_items_found)
    TextView noItemsFoundText;

    private SearchFilmsAdapter mAdapter;
    private List<MoviePageItem> mList;

    public SearchFilmFragment() {
    }

    protected int page = 1;
    protected int totalPages = 0;
    protected String lastQuery;


    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.inject(this, v);
        int numCols = 1;
        if(SeriesGApplication.getMResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 2;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SeriesGApplication.getContext(), numCols, OrientationHelper.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mList = new ArrayList<MoviePageItem>();
        mAdapter = new SearchFilmsAdapter(mList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    search(lastQuery);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void search(String query) {
        if(!Strings.isNullOrEmpty(query) && SeriesGApplication.isNetworkAvailable()) {
            if (Strings.isNullOrEmpty(lastQuery) || !query.equalsIgnoreCase(lastQuery)) {
                mList.clear();
                page = 1;
                totalPages = 0;
                lastQuery = query;
            }
            new FindFilms().execute(query);
        }
    }


    class FindFilms extends AsyncTask<String, Void, PageResult<MoviePageItem>> {

        private TmdbFilmService mTmdbFilmService = SeriesGApplication.getTmdbFilmService();

        @Override
        protected PageResult<MoviePageItem> doInBackground(String... params) {
            if(page == totalPages) {
                return null;
            }
            return mTmdbFilmService.search(params[0], page);
        }

        @Override
        protected void onPostExecute(PageResult<MoviePageItem> filmPageItemPageResult) {
            if(filmPageItemPageResult != null) {
                mList.addAll(filmPageItemPageResult.getResults());
                mAdapter.notifyDataSetChanged();
                totalPages = filmPageItemPageResult.getTotalPages();
                if(page < totalPages) {
                    page = filmPageItemPageResult.getPage() + 1;
                }
            }
            if(mList.isEmpty()) {
                noItemsFoundText.setVisibility(View.VISIBLE);
            } else {
                noItemsFoundText.setVisibility(View.GONE);
            }
            super.onPostExecute(filmPageItemPageResult);
        }
    }

}
