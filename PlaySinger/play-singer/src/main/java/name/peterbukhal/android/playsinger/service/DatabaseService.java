package name.peterbukhal.android.playsinger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import name.peterbukhal.android.playsinger.model.impl.RadioStation;

public class DatabaseService extends Service {

    public static final String TAG = DatabaseService.class.getSimpleName();
    private static final String SOURCE_URL = "http://www.radiosure.com/rsdbms/stations2.zip";
    private String tempFile;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", new Locale("ru", "RU"));

            URL url = new URL(SOURCE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("updated", format.format(new Date(System.currentTimeMillis())));
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ZipInputStream zis = new ZipInputStream(connection.getInputStream());

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    System.out.println("Unzipping " + entry.getName());
                    tempFile = Environment.getDownloadCacheDirectory() + "/" + entry.getName();
                    FileOutputStream fout = new FileOutputStream(tempFile);

                    for (int c = zis.read(); c != -1; c = zis.read()) {
                        fout.write(c);
                    }

                    zis.closeEntry();
                    fout.close();
                }

                zis.close();
            }

            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<RadioStation> stations = new ArrayList<>();

        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(tempFile), '\t', CSVWriter.NO_QUOTE_CHARACTER);

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (stations.size() > 5) break;

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

//                if (station.getSource1().endsWith(".pls")) {
//                    ExtendedPls extendedPls = new ExtendedPls(station.getSource1());
//                    station.setRadioChannels(extendedPls.getPlaylistEntriesString());
//                    stations.add(station);
//                }

                Log.d(TAG, station.toString());
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopSelf();

        return 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
