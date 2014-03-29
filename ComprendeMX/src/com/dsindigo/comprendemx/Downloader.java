package com.dsindigo.comprendemx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class Downloader
{
	protected String  urldw = null;
	
	public Downloader()
	{
		//Verificacion del folder comprende MX
		File comprendemx = new File(Environment.getExternalStorageDirectory() + File.separator + "ComprendeMX");
		if (comprendemx.exists()){
			//Nada que hacer puesto que existe
		}else {
			//Crea el directorio en caso de que no exista
			comprendemx.mkdir();
		}
	}
	
	public  boolean DownloadMyFile(String myurl, String tipo)
	{
		
		try {
			
			//Archivo para descargar
			URL url = new URL(myurl);

			//Crear la nueva conexion
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			//Configurar la conexion y conectar
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.connect();

			//Define el lugar donde se descargará
			String destination = Environment.getExternalStorageDirectory() + File.separator + "ComprendeMX" + File.separator + tipo;
			
			//Recupera el nombre del archivo en base a la url
			String filenamedw = myurl.substring(myurl.lastIndexOf('/') + 1).replace("%20","_");
			Log.d("logmxdown ", "" + filenamedw);
			
			//Crear el archivo especificando el path y obvio el nombre del archivo
			File file = new File(destination, filenamedw);
			Log.d("logmxdown ", "filepath "+destination+"/"+filenamedw);
			
			//Aqui se pondran los datos descargados
			FileOutputStream fileOutput = new FileOutputStream(file);

			//Lee la informacion de internet
			InputStream inputStream = urlConnection.getInputStream();

			//Es el total size del file para descargar
			int totalSize = urlConnection.getContentLength();
			
			//Varable para alojar el total de bytes descargados
			int downloadedSize = 0;

			//Crear el buffer
			byte[] buffer = new byte[1024];
			int bufferLength = 0; //used to store a temporary size of the buffer

			//now, read through the input buffer and write the contents to the file
			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

				//add the data in the buffer to the file in the file output stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				
				//add up the size so we know how much is downloaded
				downloadedSize += bufferLength;
				
				//Si es necesario, mostrar el % de descarga
			    Log.d("downMX", downloadedSize +"/"+ totalSize);
			}
			
			//close the output stream when done
			fileOutput.close();
			
			if(file.exists())
			{
				return true;
			}

		//catch some possible errors...
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}