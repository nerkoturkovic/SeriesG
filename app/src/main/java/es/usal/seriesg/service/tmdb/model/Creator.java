package es.usal.seriesg.service.tmdb.model;

/**
 * Created by nerko on 7/7/15.
 */
public class Creator {

    private Long id;
    private String name;
    private String profilePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
