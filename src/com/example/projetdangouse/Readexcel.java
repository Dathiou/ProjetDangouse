package com.example.projetdangouse;
import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Readexcel {

	public static double[] readex(String file,int col) throws IOException, BiffException  {
		File inputWorkbook = new File(file);
		Workbook w;

		w = Workbook.getWorkbook(inputWorkbook);
		// Get the first sheet
		Sheet sheet = w.getSheet(0);
		// Loop over first 10 column and lines
		double[] x_ponderation = new double[sheet.getRows()];
		
	

		
			for (int i = 0; i < sheet.getRows(); i++) {
				
				Cell cellx = sheet.getCell(col, i);
				x_ponderation[i]= Double.parseDouble(cellx.getContents());
				
			}
	
			/*//pour lire
		for (int i = 0; i < sheet.getRows(); i++){
			
				System.out.println(x_ponderation[i]);
		}
		*/
		return x_ponderation; 
	}
	/*	
	
	 public static void main(String[] args) throws IOException, BiffException {
 	    Readexcel test = new Readexcel();
 	    test.read("src/projet_fft/Bose.xls",0); //l'indice des colonnes commence à zero
 	  }
 	  */
}