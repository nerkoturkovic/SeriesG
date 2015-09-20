package es.usal.seriesg.service.tmdb.model;

import java.util.List;

/**
 * Created by nerko on 3/6/15.
 */
public class PageResult<T> {

    private Integer page;
    private Integer totalPages;
    private Integer totalResults;
    private List<T> results;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
