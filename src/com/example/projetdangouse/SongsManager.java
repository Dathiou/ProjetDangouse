package com.example.projetdangouse;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {

	// Chemin pour arriver jusqu'au dossier où les wav/mp3 sont stockées
	final String MEDIA_PATH = new String("/storage/emulated/0/Music/");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	// Constructeur
	public SongsManager(){

	}
	
	
	//Fonction qui prend tous les wav et mp3 du dossier et les range dans un ArrayList appelé songsList
	public ArrayList<HashMap<String, String>> getPlayList(){
		File home = new File(MEDIA_PATH);

		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				song.put("songLenght", ""+file.length());

				// Ajoute chaque chanson à songsList
				songsList.add(song);
			}
		}
		return songsList;
	}

	//Classe qui permet de filtrer les types de fichiers pour ne garder que les wav et mp3
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".wma") || name.endsWith(".WMA") || name.endsWith(".wav") || name.endsWith(".WAV")) ||name.endsWith(".mp3") ||name.endsWith(".MP3");
		}
	}
}


