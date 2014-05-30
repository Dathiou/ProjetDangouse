package com.example.projetdangouse;

public class Fenetrage {
	static double l;//(longueur de la tranche à définir en point)
	
	public static double[] fftnorm(double[] array, int n){  
		double maxValue = 0; 
		for(int k=0; k<array.length; k++) {
			array[k] = array[k]*2/(n*0.4265929745356499);
		}
		for(int i=0;i < array.length;i++){  
			if(array[i] > maxValue){  
				maxValue = array[i];  
			}  
		}
		//System.out.println(" max = " + maxValue);
		return array;
	}

	public static double[] Arraycompletepower2(double[] array,int nz){
		l=Math.log(array.length)/Math.log(2);
		int n = (int) l +1;  // n arrondi inf de l
		int n2 = (int) Math.pow(2,n); // n2 est la puissance de 2 directent inf à la longueur de array
		System.out.println(" longueur signal temporel initial= " + array.length);
		System.out.println(" 2^n supérieur= " + n2);
		double [] tab_ok_zeropadding = new double[(int) Math.pow(2,nz)];
		System.arraycopy(array, 0, tab_ok_zeropadding, 0, array.length);
		System.out.println(" longueur finale signal temporel= " + tab_ok_zeropadding.length);
		//int nb_point_tranche = (int) nbpoints; //on ne cherche à garder que n2 points centrés
		//double[] tab_puissance_deux = Arrays.copyOfRange(array, (nb_point_tranche-n2)/2, (nb_point_tranche+n2)/2);
		return tab_ok_zeropadding; // la tbleau retourné est de taille 2^n et est centré sur la tranche
	}

	public static double[] abscisse_graduee_borne(double SAMPLE_RATE,int l,int limAbscisse){

		double[] abscisse = new double[l];
		double[] abs = new double[abscisse.length];
		abscisse[0]=0;	
		abs[0]=0;
		int k =0;

		while(abs[k]<limAbscisse){
			k=k+1;
			abscisse[k] = abscisse[k-1]+1;
			abs[k] = abscisse[k]*SAMPLE_RATE/(l);
		}
		double[] ab= new double[k-1];

		System.arraycopy(abs, 0, ab, 0, k-1);

		return ab;

	}
	public static double[] blackmann(double[] rectangle){
		double coef = 2*3.1416/rectangle.length; 
		for (int u = 0; u < rectangle.length; u++ ){
			rectangle[u] = rectangle[u]* (0.42659 - 0.49656*Math.cos(coef*u) + 0.076849*Math.cos(2*coef*u)) ;
		}
		return rectangle;
	}

	public static double[] hamming(double[] rectangle){
		double coef = 2*3.1416/rectangle.length; 
		for (int u = 0; u < rectangle.length; u++ ){
			rectangle[u] = rectangle[u]* (0.53836 - 0.46164*Math.cos(coef*u)) ;
		}
		return rectangle;
	}

	public static double[] hann(double[] rectangle){
		double coef = 2*3.1416/rectangle.length; 
		for (int u = 0; u < rectangle.length; u++ ){
			rectangle[u] = rectangle[u]* (0.5 - 0.5*Math.cos(coef*u)) ;
		}
		return rectangle;
	}
	/*
	public static double[] tab_puissance_deux(double[] array){

		double i = nbpoints;
		int nb_point_tranche = (int) i; //on ne cherche à garder que n2 points centrés
		int n2 = puissance_deux(array);
		//int n2= (int) Math.pow(2, 14);
		double[] tab_puissance_deux = Arrays.copyOfRange(array, (nb_point_tranche-n2)/2, (nb_point_tranche+n2)/2);
		return tab_puissance_deux; // la tbleau retourné est de taille 2^n et est centré sur la tranche
	}
	 */

}
