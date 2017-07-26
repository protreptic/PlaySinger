package name.peterbukhal.android.playsinger.data;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;

import name.peterbukhal.android.playsinger.model.impl.RadioChannel;
import name.peterbukhal.android.playsinger.model.impl.RadioStation;

public class Database {

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JdbcPooledConnectionSource mConnection;

    public static void dropStorage(Context context) {
        File file = new File(context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + context.getPackageName() + "-1.0.h2.db");

        file.delete();
    }

    public static void init(Context context) {
        Database database = new Database(context);

        try {
            TableUtils.createTableIfNotExists(database.getConnection(), RadioStation.class);
            TableUtils.createTableIfNotExists(database.getConnection(), RadioChannel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        database.closeConnection();
    }

    public Database(Context context) {
        String storageFolder = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/" + context.getPackageName() + "-1.0";

        try {
            mConnection = new JdbcPooledConnectionSource("jdbc:h2:" + storageFolder + ";AUTO_SERVER=TRUE;IGNORECASE=TRUE;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JdbcPooledConnectionSource getConnection() {
        return mConnection;
    }

    public void closeConnection() {
        try {
            mConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public Dao<?, Long> createDao(Class<?> type) {
        Dao<?, Long> result = null;

        try {
            result = (Dao) DaoManager.createDao(mConnection, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
