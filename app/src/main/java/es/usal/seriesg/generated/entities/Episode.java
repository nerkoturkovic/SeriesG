package es.usal.seriesg.generated.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EPISODE.
 */
public class Episode {

    private Long tmdbId;
    private Long seasonId;

    public Episode() {
    }

    public Episode(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Episode(Long tmdbId, Long seasonId) {
        this.tmdbId = tmdbId;
        this.seasonId = seasonId;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

}