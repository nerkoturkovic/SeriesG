package es.usal.seriesg.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by nerko on 12/6/15.
 */
public class SeriesGDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "es.usal.seriesg.generated.entities");
        schema.setDefaultJavaPackageDao("es.usal.seriesg.generated.dao");

        Entity series = schema.addEntity("TVSerie");
        series.addLongProperty("tmdbId").primaryKey();
        series.addBooleanProperty("following").notNull();
        series.addBooleanProperty("favorite").notNull();
        series.addStringProperty("title").notNull();
        series.addStringProperty("network");
        series.addStringProperty("status").notNull();
        series.addStringProperty("posterPath");

        Entity episode = schema.addEntity("Episode");
        episode.addLongProperty("tmdbId").primaryKey();
        episode.addLongProperty("seasonId");

        Entity movie = schema.addEntity("Movie");
        movie.addLongProperty("tmdbId").primaryKey();
        movie.addBooleanProperty("seen").notNull();
        movie.addStringProperty("posterPath");
        movie.addStringProperty("title").notNull();
        movie.addStringProperty("status").notNull();
        movie.addDateProperty("releaseDate").notNull();
        movie.addStringProperty("productionCompanies").notNull();
        movie.addIntProperty("runtime").notNull();

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
