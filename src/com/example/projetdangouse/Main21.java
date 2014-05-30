package com.example.projetdangouse;

import java.io.*;
import java.lang.System;

//public class CoreJavaSound extends Object implements LineListener {
// File soundFile;

public class Main21 {

	
	static double nbpoints;
	static double SAMPLE_RATE;
	static double[] y = new double[25];
	static double[] x = new double[25];
	
	
	public static void main(String[] args) throws Exception {

		double[] x_ponderation = Readexcel.readex("src/projetDangouse/Bose.xls",0);
		double[] y_ponderation = Readexcel.readex("src/projetDangouse/Bose.xls",1);


		y[1]=80;
		y[2]=100;
		y[3] = 100;
		y[4] = 100;
		y[5] = 100;
		y[6] = 120;
		y[7] = 140;
		y[8] = 150;
		y[9] = 160;
		
		y[10] = 190;
		y[11] = 210;
		y[12] = 240;
		y[13] = 280;
		y[14] = 320;
		y[15] = 380;
		y[16] = 450; 
		y[17] = 550;
		y[18] = 700;
		y[19] = 900;
		y[20] = 1100;
		y[21] = 1300;
		y[22] = 1800;
		y[23] = 2500;
		y[24] = 3500;


		x[1]=50;
		x[2]=150;
		x[3] = 250;
		x[4] = 350;
		x[5] = 450;
		x[6] = 570;
		x[7] = 700;
		x[8] = 840;
		x[9] = 1000;
		x[10] = 1170;
		x[11] = 1370;
		x[12] = 1600;
		x[13] = 1850;
		x[14] = 2150;
		x[15] = 2500;
		x[16] = 2900; 
		x[17] = 3400;
		x[18] = 4000;
		x[19] = 4800;
		x[20] = 5800;
		x[21] = 7000;
		x[22] = 8500;
		x[23] = 10500;
		x[24] = 13500;
		
		long tempsDebutwav = System.currentTimeMillis();
		File file = new File("src/projetDangouse/testproj.wav");
		
		int SAMPLE_RATE = 0;


		WaveHeader1 head1 = new WaveHeader1 (file);

		SAMPLE_RATE= head1.getSampleRate();
		int numSample = head1.getNumSamples();
		//int samplesize = head1.getSampleSize();
		int channel = head1.getChannels();

		double[] sample_d =new double[numSample]; //tableau droit ou mono si channel =1
		double[] sample_g =null;

		if (channel == 2){
			sample_g = new double[numSample];
		}
		head1.getSamples(sample_d, sample_g);		

		long tempsFinwav = System.currentTimeMillis();
		float secondeswav = (tempsFinwav - tempsDebutwav);
		System.out.println("Paréparation wav en "+ secondeswav/1000 + " secondes.");
		
		int V1=15;
		int Vreel = 12;

		double sensitivity=100;
		int limAbscisse=13000;

		double A = 0.0101;
		double B = 0.5334;
		double[] puissancepic = new double[16];
		for(int k=0; k<=15; k++) {
			puissancepic[k] = A*Math.exp(B*k);
		}

		System.out.println(" fe = " + SAMPLE_RATE);

		//System.out.println(" duree = " + nbpoints/SAMPLE_RATE);

		
		long tempsDebut = System.currentTimeMillis();
		double curseur = 0.05;

		double duree = 0.04;
		double[] xtranche = null;
		
		
		if (curseur - duree/2>0) { //si on peut prendre une tranche de part et d'autre
			double[] xtranche1 = new double[(int) (duree*SAMPLE_RATE)];
		System.arraycopy(sample_d,(int) (curseur*SAMPLE_RATE-(duree*SAMPLE_RATE)/2),xtranche1,0,  (int) (duree*SAMPLE_RATE));
		xtranche = xtranche1;
		}
		else { //si on est au début
			double[] xtranche1 = new double[(int) ((curseur + duree/2)*SAMPLE_RATE)];
			System.arraycopy(sample_d,(int) (curseur*SAMPLE_RATE-(duree*SAMPLE_RATE)/2),xtranche1,0,  (int) (curseur*SAMPLE_RATE+(duree*SAMPLE_RATE)/2));
			xtranche = xtranche1;
		}
		
		double[]x_Hamming= Fenetrage.hamming(xtranche);

		double [] xfft= Fenetrage.Arraycompletepower2(x_Hamming,12);//zeropad
		//double [] tab_ok_zeropadding = new double[(int) Math.pow(2,10)];
		//System.arraycopy(tab_ok, 0, tab_ok_zeropadding, 0, tab_ok.length);
		//for(int k=0; k<tab_ok_zeropadding.length; k++)
		//System.out.println(" tableau = " + tab_ok_zeropadding[k]);

		/*DoubleFFT_1D fftDo = new DoubleFFT_1D(tab_ok_zeropadding.length);
        double[] fftproj = new double[tab_ok_zeropadding.length * 2];
        System.arraycopy(tab_ok_zeropadding, 0, fftproj, 0, tab_ok_zeropadding.length);
        fftDo.realForwardFull(fftproj);
		 */

		//Tranche prête - début FFT
		long tempsDebutmaxl = System.currentTimeMillis();
		double[] a= new double[xfft.length];
		double[] fftproj = Fft_class.fft(xfft, a, true); //fft dépend du nombre de point du tableau... le zeropadding fait réduire l'amplitude...

		double[] fftnorm = Fenetrage.fftnorm(fftproj,sample_d.length);
		long tempsFinmaxl = System.currentTimeMillis();
		float seconds = (tempsFinmaxl - tempsDebutmaxl);
		System.out.println("FFT effectuée en "+ seconds/1000 + " secondes.");

		double [] ab = Fenetrage.abscisse_graduee_borne(SAMPLE_RATE, fftnorm.length,limAbscisse);

		double[] fftr= new double[ab.length];
		//ArrayList<Double> maxfreq = new ArrayList<Double>();    
		//ArrayList<Double> PdB = new ArrayList<Double>();  
		System.arraycopy(fftnorm, 0,fftr, 0, ab.length);// on selectionne de 0 à 13000Hz

		// FFT prête - on étudie les max locaux en enlevant lobs secondaires
		//pour chaque pic local, environ 100ms de traitement...
		// la fft met 435ms pour s'executer


// bande critique et calcul de puissance : traitement pics à pics : détection maximum puis détection max locaux dans la BC
		long tempsDebutBC = System.currentTimeMillis();
		System.out.println(" taille bascisse = " + ab.length);
		double[] yfinal = traitementBC.bandecritique(ab,fftr,x_ponderation,y_ponderation,SAMPLE_RATE, V1, sensitivity,limAbscisse,puissancepic,x,y);
		long tempsFinBC = System.currentTimeMillis();
		float secondsa = (tempsFinBC - tempsDebutBC);
		System.out.println("traitement BC "+ secondsa/1000 + " secondes.");
		// tableau calculé pour un volume arbitraire

		//Calcul pour le volume reel : on peut donc effectuer les calculs et ajuster les valeurs apres !
		int Y = Vreel-V1;
		double[] yfinalVreel = new double[yfinal.length];
		for(int k=0; k<yfinalVreel.length; k++) {
			yfinalVreel[k] = yfinal[k] + (10*B/Math.log(10))*Y;
		}

		long tempsFin = System.currentTimeMillis();
		float secondes = (tempsFin - tempsDebut);
		System.out.println("Opération effectuée en "+ secondes/1000 + " secondes.");


		/*
	    Plot2DPanel plot = new Plot2DPanel();
		plot.addLegend("SOUTH");
		plot.addLinePlot("FFT de la Tranche"  ,ab, fftnorm);
		JFrame frame = new JFrame("FFTd");
		frame.setSize(1600, 1600);
		frame.setContentPane(plot);
		frame.setVisible(true);
		 */

		/*
		Plot2DPanel plot1 = new Plot2DPanel();
		plot.addLegend("SOUTH");
		plot.addLinePlot("FFT de la Tranche"  ,ab, yfinal);
		JFrame frame1 = new JFrame("FFT");
		frame1.setSize(1600, 1600);
		frame1.setContentPane(plot1);
		frame1.setVisible(true);
		 */
	}

}
