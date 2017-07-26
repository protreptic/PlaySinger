package name.peterbukhal.android.playsinger.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import name.peterbukhal.android.playsinger.R;
import name.peterbukhal.android.playsinger.data.Database;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_launcher);

        new PrepareStorage().execute();
    }

    public class PrepareStorage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Database.init(getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            startActivity(new Intent(getBaseContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME));
        }

    }

}
