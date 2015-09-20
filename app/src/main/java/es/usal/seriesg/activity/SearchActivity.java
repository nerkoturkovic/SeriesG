package es.usal.seriesg.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.google.common.base.Strings;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.fragments.SearchFilmFragment;
import es.usal.seriesg.fragments.SearchTVFragment;
import es.usal.seriesg.tabs.widget.SlidingTabLayout;

/**
 * Created by nerko on 24/6/15.
 */
public class SearchActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

    private static final int TABS_NUM = 2;
    private static final int SEARCH_TYPE_TV = 0;
    private static final int SEARCH_TYPE_FILMS = 1;

    @InjectView(R.id.pager)
    ViewPager mViewPager;
    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.tabs)
    SlidingTabLayout mTabs;


    private SearchPagerAdapter mSearchPagerAdapter;

    private SearchView mSearchView;
    private String[] searchTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        searchTypes = SeriesGApplication.getMResources().getStringArray(R.array.search_types);

        ButterKnife.inject(this);

        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mSearchPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSearchPagerAdapter);

        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mViewPager);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                search(mSearchView.getQuery().toString(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.requestFocus();
    }

    public class SearchPagerAdapter extends FragmentPagerAdapter {
        public SearchPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TABS_NUM;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case SEARCH_TYPE_TV:
                    return new SearchTVFragment();
                case SEARCH_TYPE_FILMS:
                    return new SearchFilmFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return searchTypes[position];
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        int pos = mViewPager.getCurrentItem();
        return search(query, pos);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private boolean search(String query, int position) {
        if(!Strings.isNullOrEmpty(query)) {
            switch (position) {
                case SEARCH_TYPE_TV:
                    ((SearchTVFragment) getSupportFragmentManager().getFragments().get(position)).search(query);
                    return true;
                case SEARCH_TYPE_FILMS:
                    ((SearchFilmFragment) getSupportFragmentManager().getFragments().get(position)).search(query);
                    return true;
            }
        }
        return false;
    }
}
