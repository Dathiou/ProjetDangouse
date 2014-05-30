package com.example.projetdangouse;

import java.io.IOException;

public class Puissance {


	public static double pSPL_f(int vol, double amp, double sensitivity, double f,double[] x_ponderation,double[] y_ponderation,double[] puissancepic) throws IOException{  
		double pelec = puissancepic[vol]*amp*amp; //puissance elec d'un pic unique d'amplitude amp
		double y=InterpolationLineaire.interpLinear(x_ponderation,y_ponderation,f);
		//System.out.println("valeur prise dans le tableau= " + y);
		double pSPL_f= sensitivity+10*Math.log10(pelec) + y; //valeur à la fréquence f de l'amplification du  casque
		return pSPL_f;
	}
	//public static void main(String[] args) throws IOException, BiffException {
		
	
/*double level_pic = pSPL_f(15,1,100,1000.78);
		System.out.println("puissance du pic en dB = "+level_pic);
	
*/
	//}
}