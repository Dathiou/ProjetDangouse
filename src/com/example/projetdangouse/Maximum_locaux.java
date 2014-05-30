package com.example.projetdangouse;
import java.util.ArrayList;
public class Maximum_locaux {

	public static ArrayList<Double> maxlocaux(double[] array, double [] freq,  ArrayList<Double> maxfreq)  {
		double max = 0 ;
		
		for(int i=0;i < array.length;i++){  
			if(array[i] > max){  
				max = array[i];  
			}  
		}
		//System.out.println(" grandmaxfft = " + max);
		double seuil = max/(Math.exp(Math.log(10)*25/20));
		//System.out.println(" seuil amplitude = " + seuil);
		
		ArrayList<Double> tabmax = new ArrayList<Double>();                  // M tableau répertoriant l'amplitude des max locaux de la BC
		int l=1;
		
		for(int j=1;j<= array.length-2;j++){  
			if(array[j-1]<array[j] && array[j+1]<array[j] && array[j]> seuil && array[j-1]!=0 && array[j+1]!=0){ //pour chaque max local au dessus d'un seuil 
				tabmax.add(array[j]);// tableau avec tous les maximums locaux
				maxfreq.add(freq[j]);// avec les frequences correspondantes à chaque max
				l=l+1;
				//System.out.println(" freq = " + freq[j]);
				//System.out.println(" max = " + array[j]);
			}  
		}	
		return tabmax;
	}
}
