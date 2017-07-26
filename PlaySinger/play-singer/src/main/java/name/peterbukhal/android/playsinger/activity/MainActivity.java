package name.peterbukhal.android.playsinger.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.DrawerBuilder;

import name.peterbukhal.android.playsinger.R;
import name.peterbukhal.android.playsinger.fragment.RadioStationsFragment;
import name.peterbukhal.android.playsinger.service.PlaybackService;

import static name.peterbukhal.android.playsinger.service.PlaybackService.EXTRA_PLAYBACK_COMMAND;
import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_ACTION;
import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_PAUSE;
import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_RESUME;
import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_STOP;

public class MainActivity extends AppCompatActivity {

    public static final String ACCOUNT_TYPE = "name.peterbukhal.android.playsinger";
    public static final String ACCOUNT = "PlaySinger";

    private LocalBroadcastManager mBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        mBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withHasStableIds(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .build();

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_content, new RadioStationsFragment());
            fragmentTransaction.commit();

            createSyncAccount(getApplicationContext());
        }

        startService(new Intent(getApplicationContext(), PlaybackService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_playback, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pause: {
                mBroadcastManager.sendBroadcast(
                                new Intent(PLAYBACK_ACTION)
                                        .putExtra(EXTRA_PLAYBACK_COMMAND, PLAYBACK_PAUSE));
            } break;
            case R.id.resume: {
                mBroadcastManager.sendBroadcast(
                        new Intent(PLAYBACK_ACTION)
                                .putExtra(EXTRA_PLAYBACK_COMMAND, PLAYBACK_RESUME));
            } break;
            case R.id.stop: {
                mBroadcastManager.sendBroadcast(
                        new Intent(PLAYBACK_ACTION)
                                .putExtra(EXTRA_PLAYBACK_COMMAND, PLAYBACK_STOP));
            } break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public static Account createSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);

        return newAccount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getApplicationContext(), PlaybackService.class));
    }
}
