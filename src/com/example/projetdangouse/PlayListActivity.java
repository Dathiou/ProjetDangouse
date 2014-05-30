package com.example.projetdangouse;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends ListActivity {
	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private Button precedent;
	double[] x_ponderation;
	double[] y_ponderation;
	
	@Override
	//Activité qui affiche toutes les chansons disponibles
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		final Intent intent = getIntent();
		x_ponderation = intent.getDoubleArrayExtra("x_ponderation");
		y_ponderation = intent.getDoubleArrayExtra("y_ponderation");
		 

		
		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		precedent = (Button)findViewById(R.id.precedent);
		precedent.setOnClickListener(precedentListener);

		SongsManager plm = new SongsManager();
		// Va chercher les chansons stockées dans le téléphone
		this.songsList = plm.getPlayList();

		// looping through playlist
		for (int i = 0; i < songsList.size(); i++) {
			// creating new HashMap
			HashMap<String, String> song = songsList.get(i);

			// adding HashList to ArrayList
			songsListData.add(song);
		}

		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
				R.id.songTitle });
		setListAdapter(adapter);


		// selecting single ListView item
		ListView lv = getListView();
		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting listitem index
				int songIndex = position;


				Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
				// Sending songIndex to PlayerActivity
				intent.putExtra("songIndex" , songIndex);
				intent.putExtra("x_ponderation", x_ponderation);
				intent.putExtra("y_ponderation", y_ponderation);
				startActivity(intent);
				// Closing PlayListView
				//finish();
			}
		});


	}
	private OnClickListener precedentListener = new OnClickListener() {
		public void onClick(View v) {
			PlayListActivity.this.finish();
		}
	};
}

