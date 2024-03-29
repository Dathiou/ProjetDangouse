package com.example.projetdangouse;
/* This class allows reading of samples of a WAV file.

* This may only work with the "basic" wav format, to which there are extensions.

* This is VERSION 3, 5/25/2007: supports
*			writeWav(short [] int, ...),	-- v2
* 			getShortSamples(...)			-- v2
*			added other short[] methods		-- v3
* 			changed writeWav to avoid copy into bsamples 	-- v3
*/

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioFormat;


public class WaveHeader1 {

	private FileInputStream fis;
	private int
	pkglen,
	channels,
	sampleRate,
	sampleSize, // Bytes per sample
	numSamples,
	bitspersample;

	public  WaveHeader1(FileInputStream thefis) {
		fis = thefis;
		boolean ok = readHeader();
		if (!ok) {
			System.err.println("bad header!");
		}
	}

	public WaveHeader1(File f) {
		long length = f.length();
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException fnfe) {
			System.err.println("WavReader cannot open file " + f);
			channels = sampleRate = sampleSize = numSamples = 0;
			return;
		}
		readHeader();
//		int computedSampleCount = (int)(length - 44)/(sampleSize);
//		if (computedSampleCount != numSamples) {
//			System.err.println("numSample disagreement: from header: " +
//					numSamples +
//					", from file size: " +
//					computedSampleCount
//					);
//			numSamples = computedSampleCount;
//		}
	}

	/*
	 * readHeader processes the header
	 */

	boolean readHeader() {
		//AudioInputStream ais = new AudioInputStream(is, );

		/* now read wav format
		 *		+-------------+
		 *		| "RIFF" (4)  |		RIFF chunk : 12 bytes
		 *		+-------------+
		 *		|  pkglen(4)  |
		 *		+-------------+
		 *		| "WAVE" (4)  |
		 *		+-------------+

		 *		+-------------+
		 *		|             |
		 *		| "fmt_" (4)  |		FORMAT : 24 bytes
		 *		|             |
		 *		+-------------+
		 *		|             |
		 *		|  0x10  (4)  |		for PCM
		 *		|             |
		 *		+-------------+
		 *		|  0x01  (2)  |		for linear quantization
		 *		+-------------+
		 *		| 1 or 2 (2)  |		1 for mono, 2 for stereo
		 *		+-------------+
		 *		|             |
		 *		|  rate  (4)  |		sample rate, in Hertz, eg 8000
		 *		|             |
		 *		+-------------+
		 *		|             |
		 *		|  B/sec (4)  |		Bytes per second, = rate * B/samp
		 *		|             |
		 *		+-------------+
		 *		| B/samp (2)  |		Bytes per sample (eg 2 for 16-bit mono)
		 *		+-------------+
		 *		| b/samp (2)  |		bits per individual single-channel sample
		 *		+-------------+

		 *		+-------------+
		 *		| "data" (4)  |
		 *		+-------------+
		 *		|  len   (4)  |		length of data, in bytes; SOMETIMES WRONG!!
		 *		+-------------+

		 */
		try {
			String s = readString(fis);
			msg_assert(s.equals("RIFF"), "missing \"RIFF\" tag in RIFF chunk");

			pkglen = readLEint(fis);
			System.err.println("Pkglen = " + pkglen);

			s = readString(fis);
			msg_assert(s.equals("WAVE"), "missing \"WAVE\" tag in RIFF chunk");

			//---------------------

			s = readString(fis);
			msg_assert(s.equals("fmt "), "missing \"fmt_\" tag in FORMAT chunk; found \"" + s + "\"");

			int pcm = readLEint(fis);
			msg_assert(pcm == 0x10, "unknown length of FORMAT chunk: " + pcm);

			int lin = readLEshort(fis);
			msg_assert(lin == 1, "code for nonlinear quantization: " + lin);

			channels = readLEshort(fis);
			System.err.println("number of channels = " + channels);

			sampleRate = readLEint(fis);
			System.err.println("sample rate: " + sampleRate);

			int Bpsec = readLEint(fis);

			sampleSize = readLEshort(fis);
			System.err.println("Bytes per sample = samplesize = " + sampleSize);

			msg_assert(Bpsec == sampleRate * sampleSize,
					"inconsistent Bytes/sec: given: " + Bpsec + ", computed: "
							+ sampleRate * sampleSize);

			bitspersample = readLEshort(fis);
			System.err.println("Bytes per sample single= " + bitspersample);
			msg_assert(bitspersample == 8*sampleSize/channels,
					"bits/sample given as " + bitspersample + " but computed as "
							+ 8*sampleSize/channels);


			//-------------------------

			s = readString(fis);
			msg_assert(s.equals("data"), "missing \"data\" tag in DATA chunk; found \"" + s + "\"");

			int size = readLEint(fis);
			
			numSamples = size / sampleSize;

			System.err.println("Number of samples according to header: " + numSamples);
			return true;
		} catch (IOException ioe) {
			System.err.println("Problem with header format!");
			return false;
		}

	}


	/*
	 * getters for the fields above
	 */
	public int getChannels()   {return channels;}
	public int getSampleRate() {return sampleRate;}
	public int getSampleSize() {return sampleSize;}
	public int getNumSamples() {return numSamples;}

	/*
	 * form of assert
	 */

	private static void msg_assert(boolean cond, String message) throws IOException {
		if (! cond) {
			System.err.println(message);
			throw new IOException();
		}
	}

	/*
	 * reads a 4-byte string from the file and returns it
	 */

	private static String readString(InputStream fis) throws IOException {
		byte[] buf = new byte[4];
		int bytesread = fis.read(buf, 0, 4);
		if (bytesread != 4) throw new IOException();
		return new String(buf);
	}

	/*
	 * reads a 4-byte little-endian integer from the file and returns it
	 */
	private static int readLEint(InputStream fis) throws IOException {
		byte[] buf = new byte[4];
		int bytesread = fis.read(buf, 0, 4);
		if (bytesread != 4) throw new IOException();

		return (((buf[3] & 0xff) << 24) |
				((buf[2] & 0xff) << 16) |
				((buf[1] & 0xff) <<  8) |
				((buf[0] & 0xff)      ) );
	}

	/*
	 * reads a 2-byte little-endian integer from the file and returns it
	 */
	private static int readLEshort(InputStream fis) throws IOException {
		byte[] buf = new byte[2];
		int bytesread = fis.read(buf, 0, 2);
		if (bytesread != 2) throw new IOException();


		return ( ((buf[1] & 0xff) <<  8) |
				((buf[0] & 0xff)      ) );
	}

	/*
	 * The following reads the next sound sample, of the specified size
	 */
	public int readValue(int vsize, int channels) throws IOException {
		if (vsize/channels==1) {   //8bits
			return(fis.read());
		} else if (vsize/channels==2 ) {   // 16 bits
			return (short) readLEshort(fis);
		} else if (vsize/channels==4) {    //32 bits
			return readLEint(fis);
		} else {
			System.out.println("bad value size = " + vsize + " given to readValue");
			return -1;
		}
	}

	public int readValue() throws IOException {
		return readValue(sampleSize,channels);
	}

	/*
	 * getSamples returns an array of all the samples 
	 */

	public double[] getSamples(double[] sample_d, double[] sample_g) {

		if (sample_g == null) {

			for (int i = 0; i<numSamples; i++) {
				try {
					sample_d[i] = readValue()/(Math.pow(2, bitspersample-1)-1);
				} catch (IOException ioe) {
					System.err.println("WavReader.getSamples: problem at position " +
							i + " out of " + numSamples);
					while (i<numSamples) {
						sample_d[i] = 0;
						i++;
					}
				}
			}
		}
		else {
			for (int i = 0; i<numSamples; i++) {
				try {
					sample_d[i] = readValue()/(Math.pow(2, bitspersample-1)-1);
					sample_g[i] = readValue()/(Math.pow(2, bitspersample-1)-1);
				} catch (IOException ioe) {
					System.err.println("WavReader.getSamples: problem at position " +
							i + " out of " + numSamples);
					while (i<numSamples) {
						sample_d[i] = 0;
						sample_g[i] = 0;
						i++;
					}
				}
			}
		}
		return sample_d;
	}

	/*
	 * getShortSamples returns an array of short of all the samples
	 */

	public short[] getShortSamples() {
		short [] samples = new short[numSamples];
		for (int i = 0; i<numSamples; i++) {
			try {
				samples[i] = (short)readValue();
			} catch (IOException ioe) {
				System.err.println("WavReader.getSamples: problem at position " +
						i + " out of " + numSamples);
				while (i<numSamples) {
					samples[i] = 0;
					i++;
				}
			}
		}
		return samples;
	}


	/*
	 * some static utility methods
	 */

	public static void printstats(int[] samples) {
		final int max16 = 32767;
		int sumsamples = 0, sumabsolutes = 0, clipcount = 0, maxlevel=0;
		for (int i = 0; i < samples.length; i++) {
			int sample = samples[i];
			int asample = (sample > 0) ? sample : -sample;
			sumsamples += sample;
			sumabsolutes += asample;
			if (asample > maxlevel) maxlevel=asample;
			if (asample >= max16) clipcount++;
		}
		System.err.println("Average signal level: " +
				(sumabsolutes)/samples.length
				+ ", max level = " + maxlevel
				+ ", clipping ratio: " + ((double)clipcount)/samples.length
				+ ", DC: " + sumsamples/samples.length
				);

	}

	// short[] version
	public static void printstats(double[] samples) {
		final int max16 = 32767;
		double sumsamples = 0, sumabsolutes = 0, clipcount = 0, maxlevel=0;
		for (int i = 0; i < samples.length; i++) {
			double sample = samples[i];
			double asample = (sample > 0) ? sample : -sample;
			sumsamples += sample;
			sumabsolutes += asample;
			if (asample > maxlevel) maxlevel=asample;
			if (asample >= max16) clipcount++;
		}
		System.err.println("Average signal level: " +
				(sumabsolutes)/samples.length
				+ ", max level = " + maxlevel
				+ ", clipping ratio: " + ((double)clipcount)/samples.length
				+ ", DC: " + sumsamples/samples.length
				);

	}

	// same except uses multiple lines
	public static void printstats2(int[] samples) {
		final int max16 = 32767; //2^15-1
		int sumsamples = 0, sumabsolutes = 0, clipcount = 0, maxlevel=0;
		for (int i = 0; i < samples.length; i++) {
			int sample = samples[i];
			int asample = (sample > 0) ? sample : -sample;
			sumsamples += sample;
			sumabsolutes += asample;
			if (asample > maxlevel) maxlevel=asample;
			if (asample >= max16) clipcount++;
		}
		System.err.println("Average signal level: " +
				((double)sumabsolutes)/samples.length
				+ ", max level = " + maxlevel
				+ "\n   "
				+ ", clipping ratio: " + ((double)clipcount)/samples.length
				+ ", DC: " + sumsamples/samples.length
				);

	}

	// short[] version
	public static void printstats2(short[] samples) {
		final int max16 = 32767;
		int sumsamples = 0, sumabsolutes = 0, clipcount = 0, maxlevel=0;
		for (int i = 0; i < samples.length; i++) {
			int sample = samples[i];
			int asample = (sample > 0) ? sample : -sample;
			sumsamples += sample;
			sumabsolutes += asample;
			if (asample > maxlevel) maxlevel=asample;
			if (asample >= max16) clipcount++;
		}
		System.err.println("Average signal level: " +
				((double)sumabsolutes)/samples.length
				+ ", max level = " + maxlevel
				+ "\n   "
				+ ", clipping ratio: " + ((double)clipcount)/samples.length
				+ ", DC: " + sumsamples/samples.length
				);

	}

	/*
	 * The following eliminates the DC bias
	 */
	public static void DCzero (int [] samples) {
		long sum = 0;
		for (int i = 0; i<samples.length; i++) {
			sum += samples[i];
		}
		int bias = (int) (sum/samples.length);
		for (int i = 0; i< samples.length; i++) {
			samples[i] -= bias;
		}
	}

	// short[] version
	public static void DCzero (short [] samples) {
		long sum = 0;
		for (int i = 0; i<samples.length; i++) {
			sum += samples[i];
		}
		int bias = (int) (sum/samples.length);
		for (int i = 0; i< samples.length; i++) {
			samples[i] -= bias;
		}
	}

	/*
	 * The following adjusts the volume by the indicated factor
	 */
	public static void adjustVolume (double adjFactor, int[] samples) {
		for (int i = 0; i< samples.length; i++) {
			samples[i] = (int)(samples[i]*adjFactor);
		}
	}

	// short[] version
	public static void adjustVolume (double adjFactor, short[] samples) {
		for (int i = 0; i< samples.length; i++) {
			samples[i] = (short)(samples[i]*adjFactor);
		}
	}


	/*
	 * The following converts a sample[] to a .wav file,
	 * using the native java methods (rather than writing directly)
	 */

	
	/*
	 * The following class wraps around a short[] and makes it an InputStream,
	 * returning first the lo byte and then the hi.
				byte lo = (byte) (sa[i] & 0xFF);
				byte hi = (byte) ((sa[i] >> 8) & 0xFF);

	 */
	static class Short2InputStream extends InputStream {
		private short[] sa;
		private int index;
		private boolean islow;	// next byte is low
		byte lo, hi;
		int readcount = 0;

		public Short2InputStream(short[] samples) {
			sa = samples;
			index = 0;	// next index
			islow = true;	// next byte is low
		}

		public void printstats () {
			System.err.println("Short2InputStream: # reads = " + readcount
					+ ", len=" + sa.length + ", index=" + index);
		}

		public int read() {
			readcount++;
			if (index >= sa.length) {
				return -1;		// end of file
			} else if (islow) {
				lo = (byte) (sa[index] & 0xFF);
				hi = (byte) ((sa[index] >> 8) & 0xFF);
				islow = false;
				return (lo & 0xFF);		// stupid sign extension
			} else { // is high
				index++;
				islow = true;
				return  hi & 0xFF;		// ditto
			}
		}

		/*
		public int read(byte[] b, int off, int len) throws NullPointerException {
			for (int i = 0; i < len; i++) {
				int val = read();
				if (val == -1) {	// eof
					if (i==0) return -1;
					return 0;
				}
				b[off+i] = (byte) val;
			}
			return len;
		}
		/* */
	}
}
