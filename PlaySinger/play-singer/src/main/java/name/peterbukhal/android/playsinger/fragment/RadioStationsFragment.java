package name.peterbukhal.android.playsinger.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import name.peterbukhal.android.playsinger.R;
import name.peterbukhal.android.playsinger.data.Database;
import name.peterbukhal.android.playsinger.model.impl.RadioStation;
import name.peterbukhal.android.playsinger.util.Fonts;

import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_PLAY;
import static name.peterbukhal.android.playsinger.service.PlaybackService.EXTRA_PLAYBACK_COMMAND;
import static name.peterbukhal.android.playsinger.service.PlaybackService.EXTRA_RADIO_STATION;
import static name.peterbukhal.android.playsinger.service.PlaybackService.PLAYBACK_ACTION;

public class RadioStationsFragment extends Fragment implements OnRefreshListener {

	protected Typeface mRobotoCondensedBold;
	protected Typeface mRobotoCondensedRegular;
	protected Typeface mRobotoCondensedLight;

	protected RecyclerView mRecyclerView;
	private RecyclerView.Adapter<?> mRecyclerViewAdapter;
	
	private SwipeRefreshLayout mSwipeRefreshWidget;
	
	protected ArrayList<RadioStation> mRadioStations = new ArrayList<>();
	
	private Handler mHandler = new Handler();
	
    private final Runnable mRefreshDone = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(false);
        }

    };
    
    private final Runnable mRefreshBegin = new Runnable() {

        @Override
        public void run() {
            mSwipeRefreshWidget.setRefreshing(true);
        }

    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns());
		layoutManager.setReverseLayout(false);
		layoutManager.setSmoothScrollbarEnabled(true);

		mRecyclerView = new RecyclerView(getContext());
		mRecyclerView.setPadding(0, 3, 4, 3);
		mRecyclerView.setHasFixedSize(false);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setVerticalScrollBarEnabled(true);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mRecyclerView.setLayoutParams(layoutParams);

		mSwipeRefreshWidget = new SwipeRefreshLayout(getContext());
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setLayoutParams(layoutParams);
		mSwipeRefreshWidget.addView(mRecyclerView);

		return mSwipeRefreshWidget;
	}

	@Override
	public void onRefresh() {
		refreshData();
	}

	private class ReadRadioStations extends AsyncTask<Void, Void, List<RadioStation>> {

		@Override
		protected void onPreExecute() {
			mHandler.removeCallbacks(mRefreshDone);
			mHandler.removeCallbacks(mRefreshBegin);
			mHandler.postDelayed(mRefreshBegin, 250);

			mRadioStations.clear();
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<RadioStation> doInBackground(Void... params) {
			List<RadioStation> stations = new ArrayList<>();

			Database database = new Database(getContext());

			Dao<RadioStation, Long> radioStationDao = (Dao<RadioStation, Long>) database.createDao(RadioStation.class);

			try {
				mRadioStations.addAll(radioStationDao.queryBuilder().limit(200L).query());
			} catch (SQLException e) {
				e.printStackTrace();
			}

			database.closeConnection();

			return stations;
		}

		@Override
		protected void onPostExecute(List<RadioStation> radioStations) {
			mRecyclerViewAdapter.notifyDataSetChanged();

			mHandler.removeCallbacks(mRefreshBegin);
			mHandler.removeCallbacks(mRefreshDone);
			mHandler.postDelayed(mRefreshDone, 10);
		}
	}

	private void refreshData() {
		new ReadRadioStations().execute();
	}

	public class RadioStationAdapter extends RecyclerView.Adapter<CityViewHolder> {
		
		@Override
		public int getItemCount() {
			return mRadioStations.size();
		}
		
		@Override
		public CityViewHolder onCreateViewHolder(ViewGroup parent, int position) {
			return new CityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.l_radio_station, parent, false));
		}

		@Override
		public void onBindViewHolder(CityViewHolder holder, int position) {
			final RadioStation radioStation = mRadioStations.get(position);

			holder.name.setText(radioStation.getName());
			holder.name.setTypeface(mRobotoCondensedBold);

			String description = radioStation.getDescription().length() <= 85 ? radioStation.getDescription() : radioStation.getDescription().substring(0, 85) + "...";

			holder.description.setText(description);
			holder.description.setTypeface(mRobotoCondensedBold);

			holder.itemView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					LocalBroadcastManager
							.getInstance(getActivity())
							.sendBroadcast(
									new Intent(PLAYBACK_ACTION)
											.putExtra(EXTRA_PLAYBACK_COMMAND, PLAYBACK_PLAY)
											.putExtra(EXTRA_RADIO_STATION, radioStation));
				}
			});
		}

	}

	private int columns() {
		int columns = 2;
		
		Configuration config = getResources().getConfiguration();
		
		int screenSize = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 2;
		    } else {
		    	columns = 1;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 3;
		    } else {
		    	columns = 2;
		    }
		} 
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	columns = 4;
		    } else {
		    	columns = 2;
		    }
		} 
		
		return columns;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable("cities", mRadioStations);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRobotoCondensedBold = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Bold");
		mRobotoCondensedRegular = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Regular");
		mRobotoCondensedLight = Fonts.get(getActivity()).getTypeface("RobotoCondensed-Light");

		mRecyclerViewAdapter = new RadioStationAdapter();
		mRecyclerView.setAdapter(mRecyclerViewAdapter); 

		if (savedInstanceState != null && savedInstanceState.containsKey("cities")) {
			mRadioStations.addAll((ArrayList) savedInstanceState.getSerializable("cities"));
			mRecyclerViewAdapter.notifyDataSetChanged();
		}

		if (mRadioStations.isEmpty())
			refreshData();
	}
	
	public static class CityViewHolder extends RecyclerView.ViewHolder {
		
		public TextView name;
		public TextView description;
		
		public CityViewHolder(View itemView) {
			super(itemView); 

			name = (TextView) itemView.findViewById(R.id.name);
			description = (TextView) itemView.findViewById(R.id.description);
		}
		
	}

}
