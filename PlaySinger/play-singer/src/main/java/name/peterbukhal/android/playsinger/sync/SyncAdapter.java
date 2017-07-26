package name.peterbukhal.android.playsinger.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import name.peterbukhal.android.playsinger.R;
import name.peterbukhal.android.playsinger.data.Database;
import name.peterbukhal.android.playsinger.model.impl.RadioStation;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    Context mContext;
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContext = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContext = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        CSVReader reader = null;
        Database database = null;

        try {
            database = new Database(mContext);
            Dao<RadioStation, Long> radioStationDao = (Dao<RadioStation, Long>) database.createDao(RadioStation.class);
            reader = new CSVReader(new InputStreamReader(mContext.getResources().openRawResource(R.raw.stations_full)), '\t', CSVWriter.NO_QUOTE_CHARACTER);

            String[] line;
            while ((line = reader.readNext()) != null) {
                RadioStation station = new RadioStation();
                station.setName(line[0]);
                station.setDescription(line[1]);
                station.setGenre(line[2]);
                station.setCountry(line[3]);
                station.setLanguage(line[4]);
                station.setSource1(line[5]);
                station.setSource2(line[6]);
                station.setSource3(line[7]);
                station.setSource4(line[8]);
                station.setSource5(line[9]);
                station.setSource6(line[10]);

                if (station.getSource1().endsWith("pls")) {
                    radioStationDao.createOrUpdate(station);
                    Log.d("SyncAdapter", station.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (database != null)
                database.closeConnection();
        }
    }

}