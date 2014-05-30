package com.example.projetdangouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class traitementBC {
	
	
	
	public static double largeurBCfreq(double f,double[] x,double[] y)  {
		



		double largeurBCfreq=InterpolationLineaire.interpLinear(x,y,f);
		return largeurBCfreq;
	}


	public static int testboucle(double[] y)  {
		double n = 0;
		int l=0;
		for (int i=1;i>y.length;i++){
			if (y[i] < 0.0000000124){
				n = n+1;
			}
		}

		if (n==y.length){
			l=1;
		}

		return l;


	}

	//méthode qui traite la BC au nive	au du maximum de tableau et qui met à 0 la BC. 
	//le tableau final est rassemblé dans y final
	public static double[] bandecritique(double[] x, double[] y,double[] x_ponderation,double[] y_ponderation,double SAMPLE_RATE, int V, double sensitivity,int limAbscisse,double[] puissancepic,double[] freqBC, double[] largeurBC) throws IOException {

		double[] yfinal = new double[y.length];
		//double largeurBCfreq=200;
		//double fcentrale=400;
		ArrayList<Double> maxfreq = new ArrayList<Double>(); 
		//ArrayList<Double> PdB = new ArrayList<Double>();  
		double sommeBC = 0.001;//valeur arbitraire pour initialiser
		
		while (sommeBC>0){
			sommeBC =0;
			double maxValue = 0;
			double fcentrale = 0;
			//double imax=0;
			for(int i=1;i < y.length-1;i++){
				if(y[i] > maxValue && y[i-1]<y[i] && y[i+1]<y[i] && y[i-1]!=0 && y[i+1]!=0){
					maxValue = y[i];  
					fcentrale = x[i];
					//imax = i;
					}
			}  
			double largeurBCfreq=largeurBCfreq(fcentrale,freqBC,largeurBC);
			
			if(fcentrale-largeurBCfreq/2<0){
				double[] BC = new double[ (int) ( (fcentrale+largeurBCfreq/2)*y.length/limAbscisse)];
				double[] absBC = new double[ (int) ( (fcentrale+largeurBCfreq/2)*y.length/limAbscisse)];
				//double[] BC = new double[ (int) ( largeurBCfreq*y.length/limAbscisse)];
				//double[] BC = Arrays.copyOfRange(y, 0,(int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse)));
				//System.arraycopy(y, (int)( (fcentrale-largeurBCfreq/2)*y.length/13000), BC, 0, (int)(( largeurBCfreq)*y.length/13000));
				//double[] absBC = Arrays.copyOfRange(x, 0,(int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse)));
				
				
				System.arraycopy(y, 0, BC, 0, (int) ( (fcentrale+largeurBCfreq/2)*y.length/limAbscisse));
				System.arraycopy(y, 0, absBC, 0, (int) ( (fcentrale+largeurBCfreq/2)*y.length/limAbscisse));
				ArrayList<Double> max = Maximum_locaux.maxlocaux(BC,absBC,maxfreq);
				
				/*
			plot.addLegend("SOUTH");
			plot.addLinePlot("FFT de la Tranche"  , BC);
			JFrame frame = new JFrame("FFT");
			frame.setSize(1600, 1600);
			frame.setContentPane(plot);
			frame.setVisible(true);
				 */
				//System.out.println(" BC = "+ BC[4]);
				Iterator<Double> it = max.iterator();
				Iterator<Double> j1 = maxfreq.iterator();

				while(it.hasNext()){
					double amp = it.next();
					double f = j1.next();
					sommeBC = 20*Math.log10(Math.pow(10,sommeBC/20) + Math.pow(10,Puissance.pSPL_f(V,amp,sensitivity,f,x_ponderation,y_ponderation,puissancepic)/20));
					//PdB.add(Puissance.pSPL_f(V,amp,sensitivity,f,x_ponderation,y_ponderation));
					System.out.println(" BwfdfC = "+ sommeBC);
				}

				for(int i1=0;i1 < (int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse));i1++){
					yfinal[i1]=sommeBC;
					y[i1]=0;
				}
				//System.out.println(" puissance SPL à "+ f + " = "  + Puissance.pSPL_f(V,amp,sensitivity,f));
			}

			else{ 
				//System.out.println ("taille de la fft complete= "+ y.length);
				double[] BC = new double[ (int) ( largeurBCfreq*y.length/limAbscisse)];
				double[] absBC = new double[ (int) ( largeurBCfreq*y.length/limAbscisse)];

				//System.out.println ("max BC " + maxValue + "; fcentr = "+ fcentrale +" ; Tranche = ["+ (int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse))+";"+(int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse))+"]");

				//double[] BC = Arrays.copyOfRange(y, (int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse)),(int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse)));
				//System.out.println ("taille de la bande critique= "+ BC.length);
				System.arraycopy(y, (int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse)), BC, 0, (int) ( largeurBCfreq*y.length/limAbscisse));
				System.arraycopy(y, (int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse)), absBC, 0, (int) ( largeurBCfreq*y.length/limAbscisse));

				//double[] absBC = Arrays.copyOfRange(x, (int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse)),(int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse)));
				ArrayList<Double> max = Maximum_locaux.maxlocaux(BC,absBC,maxfreq);
				/*
			for(int i=1;i < BC.length;i++){  
				System.out.println (BC[i]+ " à "+ absBC[i] +"Hz" );
			}


			Plot2DPanel plot = new Plot2DPanel();
			plot.addLegend("SOUTH");
			plot.addLinePlot("FFT de la Tranche"  ,BC);
			JFrame frame = new JFrame("FFT");
			frame.setSize(1600, 1600);
			frame.setContentPane(plot);
			frame.setVisible(true);

				 */


				//System.out.println(" BC = "+ BC[4]);
				Iterator<Double> it = max.iterator();
				Iterator<Double> j1 = maxfreq.iterator();

				while(it.hasNext()){
					double amp = it.next();
					double f = j1.next();
					sommeBC = 20*Math.log10(Math.pow(10,sommeBC/20) + Math.pow(10,Puissance.pSPL_f(V,amp,sensitivity,f,x_ponderation,y_ponderation,puissancepic)/20));
					//PdB.add(Puissance.pSPL_f(V,amp,sensitivity,f,x_ponderation,y_ponderation));
					//System.out.println(" niveau tranche = "+ sommeBC);
				}

				if((int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse))<y.length){
					for(int i1=(int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse));i1 < (int) ( (fcentrale+largeurBCfreq/2)*y.length/(limAbscisse));i1++){
						yfinal[i1]=sommeBC;
						y[i1]=0;
					}
				}
				else{
					for(int i1=(int) ( (fcentrale-largeurBCfreq/2)*y.length/(limAbscisse));i1 < y.length;i1++){
						yfinal[i1]=sommeBC;
						y[i1]=0;
					}
				}

			}
			//System.out.println(" puissance SPL à "+ f + " = "  + Puissance.pSPL_f(V,amp,sensitivity,f));
		}
		/*
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLegend("SOUTH");
		plot.addLinePlot("FFT de la Tranche"  , y);
		JFrame frame = new JFrame("FFT");
		frame.setSize(1600, 1600);
		frame.setContentPane(plot);
		frame.setVisible(true);
*/
		 
		return yfinal;
	}
}







