package es.usal.seriesg.service.tmdb.model;

import java.util.Date;
import java.util.List;

/**
 * Created by nerko on 12/7/15.
 */
public class TMDBSeason {

    private Date airDate;
    private String name;
    private String overview;
    private Long id;
    private String posterPath;
    private Integer seasonNumber;
    private List<EpisodeListItem> episodes;

    public Date getAirDate() {
        return airDate;
    }

    public void setAirDate(Date airDate) {
        this.airDate = airDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public List<EpisodeListItem> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<EpisodeListItem> episodes) {
        this.episodes = episodes;
    }
}
