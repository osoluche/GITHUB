package com.dsindigo.comprendemx;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class unzipfile {

	public void unzip(String location, String filezip){

		try {
			FileInputStream is = new FileInputStream(filezip);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
			
			try {
				ZipEntry ze;
			
				while ((ze = zis.getNextEntry()) != null) {
					
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					byte[] buffer = new byte[1024];
					int count;
					
					while ((count = zis.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
					}
					
					String filename = ze.getName();
					
					byte[] bytes = baos.toByteArray();
					
					File nf = new File(filename);
					String  filepath = nf.getAbsolutePath();
					String newpath = filepath.substring(0, filepath.lastIndexOf(File.separator));
					
					File folder = new File(location+ File.separator +newpath);
					
					if(folder.exists())
					{}
					else
					{
						Log.d("logmx ", "Creado " + folder.toString());
						File io = new File(location+newpath);
						io.mkdirs();
					}
					
					//Crea el archivo
					OutputStream out = new FileOutputStream(location+filename);
					out.write(bytes);
					out.close();
				}
			} 
			catch (OutOfMemoryError outofmemory)  
		     {  
		      System.out.println("Out of memory trying to unzip!");  
		      System.out.println("Error message: " + outofmemory.getMessage());  
		      outofmemory.printStackTrace();              
		     }
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					zis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}