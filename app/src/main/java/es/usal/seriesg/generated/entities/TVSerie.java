package es.usal.seriesg.generated.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TVSERIE.
 */
public class TVSerie {

    private Long tmdbId;
    private boolean following;
    private boolean favorite;
    /** Not-null value. */
    private String title;
    private String network;
    /** Not-null value. */
    private String status;
    private String posterPath;

    public TVSerie() {
    }

    public TVSerie(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public TVSerie(Long tmdbId, boolean following, boolean favorite, String title, String network, String status, String posterPath) {
        this.tmdbId = tmdbId;
        this.following = following;
        this.favorite = favorite;
        this.title = title;
        this.network = network;
        this.status = status;
        this.posterPath = posterPath;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public boolean getFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /** Not-null value. */
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    /** Not-null value. */
    public String getStatus() {
        return status;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

}