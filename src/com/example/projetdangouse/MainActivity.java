package com.example.projetdangouse;





import java.io.IOException;

import jxl.read.biff.BiffException;
import android.support.v7.app.ActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

	final String defaut = "Indiquez votre âge :";
	final String phrasechoc = "Raaah les vieux!";
	private Button gogo;
	private Button next;
	private EditText age;
	private Spinner spinner_marque;
	private Spinner spinner_model;


	// Activité principale, demande à l'utilisateur sonâge, la marque et le modèle de son casque
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		gogo = (Button)findViewById(R.id.gogo);
		next = (Button)findViewById(R.id.next);
		age = (EditText)findViewById(R.id.age);
		spinner_marque = (Spinner) findViewById(R.id.spinner_marque);
		spinner_model = (Spinner) findViewById(R.id.spinner_model);

		// On attribue un listener adapté aux vues qui en ont besoin
		gogo.setOnClickListener(gogoListener);
		next.setOnClickListener(nextListener);


		// Ajout d'un menu déroulant pour la marque du casque

		String[] Marque= new String[]{"BOSE","AKG","SONY","Marque"};
		ArrayAdapter<String> adapter_marque = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, Marque){
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if (position == getCount()) {
					((TextView) v.findViewById(android.R.id.text1)).setText("");
					((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
				}
				return v;

			}      

			@Override
			public int getCount() {
				return super.getCount()-1; // you dont display last item. It is used as hint.
			}
		};

		//Ajout d'un menu déroulant pour le modèle du casque
		String[] Rien= new String[]{"","Modèle"};
		ArrayAdapter<String> adapter_Rien = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, Rien){
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if (position == getCount()) {
					((TextView) v.findViewById(android.R.id.text1)).setText("");
					((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
				}
				return v;

			}      

			public int getCount() {
				return super.getCount()-1; // you dont display last item. It is used as hint.
			}
		};

		String[] BOSE= new String[]{"Circum","Supra","AE2i"};
		final ArrayAdapter<String> adapter_BOSE = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, BOSE);


		String[] SONY= new String[]{"MB200","SOny3","SOny5"};
		final ArrayAdapter<String> adapter_SONY = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, SONY);

		adapter_marque.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_marque.setAdapter(adapter_marque);
		spinner_marque.setSelection(adapter_marque.getCount());

		spinner_model.setAdapter(adapter_Rien);
		spinner_model.setSelection(adapter_Rien.getCount());



		spinner_marque.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void  onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if(parent.getItemAtPosition(pos).toString().equals("BOSE"))
				{
					spinner_model.setAdapter(adapter_BOSE);
					adapter_BOSE.notifyDataSetChanged();
				}
				else if(parent.getItemAtPosition(pos).toString().equals("SONY"))
				{
					spinner_model.setAdapter(adapter_SONY);
					adapter_SONY.notifyDataSetChanged();
				}
			}



			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}


	 //Bouton gogo qui donne des informations sur ce qu'on a prit
		private OnClickListener gogoListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
	
				String a = age.getText().toString();
				//			if(a == ""){
				//				float aValue = 0;
				//			}else{
				//			float aValue = Float.valueOf(a);
				//			}
				Toast.makeText(MainActivity.this,
						"Âge : " + a +
						"\n" + "Casque Sélectionné : " + 
						"\n" + String.valueOf(spinner_marque.getSelectedItem())
						+ 
						"\n" + String.valueOf(spinner_model.getSelectedItem()) ,
						Toast.LENGTH_LONG).show();
			}
		};


	//Bouton Next qui va nous faire passer à l'activité suivante
	private OnClickListener nextListener = new OnClickListener() {
		String naze = "";
		private String marqueSelectionne; 
		private String modeleSelectionne; 
		@Override
		public void onClick(View v) {
			marqueSelectionne = String.valueOf(spinner_marque.getSelectedItem());
			modeleSelectionne = String.valueOf(spinner_model.getSelectedItem());
			String a = age.getText().toString(); //On accepte de changer d'activité que si toutes les informations demandées ont été fournies
			if(a.equals(naze) || marqueSelectionne.equals("Marque") || modeleSelectionne.equals("Modèle")){         
				Toast.makeText(MainActivity.this,"Veuillez indiquer votre âge et le casque utilisé." ,Toast.LENGTH_LONG).show();
			}else{
				//float aValue = Float.valueOf(a);
				Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
				
				try {
					double[] x_ponderation = Readexcel.readex("/storage/emulated/0/Documents/Bose.xls",0);
					double[] y_ponderation = Readexcel.readex("/storage/emulated/0/Documents/Bose.xls",1);

					
					intent.putExtra("x_ponderation", x_ponderation);
					intent.putExtra("y_ponderation", y_ponderation);
					
					startActivity(intent);


				} catch (BiffException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
		}
	};
}





