package name.peterbukhal.android.playsinger.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import name.peterbukhal.android.playsinger.R;
import name.peterbukhal.android.playsinger.activity.MainActivity;
import name.peterbukhal.android.playsinger.model.impl.ExtendedPls;
import name.peterbukhal.android.playsinger.model.impl.RadioStation;

public class PlaybackService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnInfoListener {

    public static final String TAG = "Playback";

    public static final String PLAYBACK_PLAY = "name.peterbukhal.android.playsinger.service.action.PLAY";
    public static final String PLAYBACK_PAUSE = "name.peterbukhal.android.playsinger.service.action.PAUSE";
    public static final String PLAYBACK_RESUME = "name.peterbukhal.android.playsinger.service.action.RESUME";
    public static final String PLAYBACK_STOP = "name.peterbukhal.android.playsinger.service.action.STOP";

    public static final String EXTRA_PLAYBACK_COMMAND = "extra_playback_command";
    public static final String EXTRA_RADIO_STATION = "extra_radio_station";

    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNotification() {
        int icon;
        String message;

        if (!mPrepared && !mError) {
            icon = R.drawable.ic_public_white_48dp;
            message = getString(R.string.text_loading);
        } else if (mBuffering) {
            icon = R.drawable.ic_public_white_48dp;
            message = String.format(Locale.getDefault(), "Buffering %d%%", mPercent);
        } else if (mError) {
            icon = R.drawable.ic_error_white_48dp;
            message = getString(R.string.text_station_inaccessible);
        } else if (mPaused) {
            icon = R.drawable.ic_pause_white_48dp;
            message = getString(R.string.playback_paused);
        } else if (mStopped) {
            icon = R.drawable.ic_stop_white_48dp;
            message = getString(R.string.playback_stopped);
        } else {
            icon = R.drawable.ic_play_arrow_white_48dp;
            message = mRadioStation.getGenre() + " - " + mRadioStation.getCountry();
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(icon)
                        .setContentTitle(mRadioStation.getName())
                        .setContentText(message)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mRadioStation.getDescription()));

        startForeground(412132, builder.build());
    }

    private boolean mPrepared;
    private boolean mPaused;
    private boolean mStopped;
    private boolean mBuffering;

    private RadioStation mRadioStation;
    private PendingIntent pendingIntent;
    private LocalBroadcastManager mBroadcastManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && intent.getExtras().containsKey(EXTRA_PLAYBACK_COMMAND)) {
                String action = intent.getStringExtra(EXTRA_PLAYBACK_COMMAND);

                switch (action) {
                    case PLAYBACK_PLAY: {
                        if (intent.getExtras().containsKey(EXTRA_RADIO_STATION)) {
                            mRadioStation = intent.getParcelableExtra(EXTRA_RADIO_STATION);

                            new Prepare().execute(mRadioStation);
                        }
                    } break;
                    case PLAYBACK_PAUSE: {
                        new Pause().execute();
                    } break;
                    case PLAYBACK_RESUME: {
                        new Resume().execute();
                    } break;
                    case PLAYBACK_STOP: {
                        new Stop().execute();
                    } break;
                    default: {
                        stopSelf();
                    }
                }
            }
        }

    };

    public static final String PLAYBACK_ACTION = "name.peterbukhal.android.playsinger.PLAYBACK_ACTION";

    @Override
    public void onCreate() {
        mBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        mBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(PLAYBACK_ACTION));

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnInfoListener(this);

        pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), MainActivity.class), 0);
    }

    private class Prepare extends AsyncTask<RadioStation, Void, Void> {

        @Override
        protected Void doInBackground(RadioStation... stations) {
            ExtendedPls extendedPls = new ExtendedPls(stations[0].getSource1());
            List<String> uris = extendedPls.getPlaylistEntriesString();

            try {
                if (!uris.isEmpty()) {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(getBaseContext(), Uri.parse(uris.get(0)));
                    mMediaPlayer.prepareAsync();

                    mPrepared = false;
                    mError = false;
                }
            } catch (IOException e) {
                mError = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateNotification();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        new Play().execute();
    }

    private class Play extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mPaused = false;
                mPrepared = true;
                mStopped = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateNotification();
        }

    }

    private class Pause extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mMediaPlayer != null && !mPaused) {
                mMediaPlayer.pause();
                mPaused = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateNotification();
        }

    }

    private class Resume extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mMediaPlayer != null && mPaused) {
                mMediaPlayer.start();
                mPaused = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateNotification();
        }

    }

    private class Stop extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mStopped = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateNotification();
        }

    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();

        mBroadcastManager.unregisterReceiver(broadcastReceiver);

        stopForeground(true);
    }

    private boolean mError;

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mError = true;

        Toast.makeText(getApplicationContext(), "Station \"" + mRadioStation.getName() + "\" inaccessible", Toast.LENGTH_LONG).show();

        return true;
    }

    private int mPercent = -1;

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mPercent = percent;

        if (percent >= 0 && percent <= 100) {
            mBuffering = true;
            updateNotification();

            Log.d(TAG, String.format("Buffering ... %d%%", percent));
        } else {
            mBuffering = false;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        updateNotification();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN: {
                Log.d(TAG, "MEDIA_INFO_UNKNOWN: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING: {
                Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                updateNotification();
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START");
            } break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                updateNotification();
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
            } break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING: {
                Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE: {
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE: {
                Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE: {
                Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE: extra = " + extra);
            } break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT: {
                Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT: extra = " + extra);
            } break;
            default: {
                Log.d(TAG, "Unknown type of info or warning: what = " + what + " extra = " + extra);
            }
        }

        return true;
    }

}
