package es.usal.seriesg.generated.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import es.usal.seriesg.generated.entities.TVSerie;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table TVSERIE.
*/
public class TVSerieDao extends AbstractDao<TVSerie, Long> {

    public static final String TABLENAME = "TVSERIE";

    /**
     * Properties of entity TVSerie.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property TmdbId = new Property(0, Long.class, "tmdbId", true, "TMDB_ID");
        public final static Property Following = new Property(1, boolean.class, "following", false, "FOLLOWING");
        public final static Property Favorite = new Property(2, boolean.class, "favorite", false, "FAVORITE");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Network = new Property(4, String.class, "network", false, "NETWORK");
        public final static Property Status = new Property(5, String.class, "status", false, "STATUS");
        public final static Property PosterPath = new Property(6, String.class, "posterPath", false, "POSTER_PATH");
    };


    public TVSerieDao(DaoConfig config) {
        super(config);
    }
    
    public TVSerieDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'TVSERIE' (" + //
                "'TMDB_ID' INTEGER PRIMARY KEY ," + // 0: tmdbId
                "'FOLLOWING' INTEGER NOT NULL ," + // 1: following
                "'FAVORITE' INTEGER NOT NULL ," + // 2: favorite
                "'TITLE' TEXT NOT NULL ," + // 3: title
                "'NETWORK' TEXT," + // 4: network
                "'STATUS' TEXT NOT NULL ," + // 5: status
                "'POSTER_PATH' TEXT);"); // 6: posterPath
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'TVSERIE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TVSerie entity) {
        stmt.clearBindings();
 
        Long tmdbId = entity.getTmdbId();
        if (tmdbId != null) {
            stmt.bindLong(1, tmdbId);
        }
        stmt.bindLong(2, entity.getFollowing() ? 1l: 0l);
        stmt.bindLong(3, entity.getFavorite() ? 1l: 0l);
        stmt.bindString(4, entity.getTitle());
 
        String network = entity.getNetwork();
        if (network != null) {
            stmt.bindString(5, network);
        }
        stmt.bindString(6, entity.getStatus());
 
        String posterPath = entity.getPosterPath();
        if (posterPath != null) {
            stmt.bindString(7, posterPath);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TVSerie readEntity(Cursor cursor, int offset) {
        TVSerie entity = new TVSerie( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // tmdbId
            cursor.getShort(offset + 1) != 0, // following
            cursor.getShort(offset + 2) != 0, // favorite
            cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // network
            cursor.getString(offset + 5), // status
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // posterPath
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TVSerie entity, int offset) {
        entity.setTmdbId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFollowing(cursor.getShort(offset + 1) != 0);
        entity.setFavorite(cursor.getShort(offset + 2) != 0);
        entity.setTitle(cursor.getString(offset + 3));
        entity.setNetwork(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStatus(cursor.getString(offset + 5));
        entity.setPosterPath(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TVSerie entity, long rowId) {
        entity.setTmdbId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TVSerie entity) {
        if(entity != null) {
            return entity.getTmdbId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
