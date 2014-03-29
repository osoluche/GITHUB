package com.dsindigo.comprendemx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class ConectorJson {
	
	public JSONObject GetPlayById(int i) {

		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();

		try {
			appdata.put("cdRequest", 1);
			appdata.put("idPc", i);
			appdata.put("nbDevice", CreateMenu.uidd);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			
			Log.d("Parametros", "> "+ appdata.toString());
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
			JSONObject object = new JSONObject(rep);
			
			object.put("idplanclase", i);
			return object;
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject obtenerListado(){
		
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		
		try{
						
			appdata.put("cdRequest", 7);
			appdata.put("nbDevice", CreateMenu.uidd);
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			Log.d("Parametros", "> "+ appdata.toString());
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
			JSONObject object = new JSONObject(rep);
			//Log.d("Listado => ", object.toString());
			return object;
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public JSONObject getEvaluacion(String ideval)
	{
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		
		try{
			appdata.put("cdRequest", 2);
			appdata.put("idOa", ideval);
			appdata.put("nbDevice", CreateMenu.uidd);
			
			Log.d("logmxSend",  "==> "+ appdata.toString());
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
			JSONObject object = new JSONObject(rep);
			
			return object;
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public JSONObject ConsultarHistorial(){
		
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		
		try{
						
			appdata.put("cdRequest", 8);
			appdata.put("nbDevice", CreateMenu.uidd);
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			Log.d("", "urljson =>" + urljson);
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);			
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
			
			//Metodo anterior para leer el string
			//String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
			
			JSONObject object = new JSONObject(rep);
			
			return object;
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}

	public String ConsultarSolicitud(String planclase){
		
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		
		try{
			appdata.put("cdRequest", 9);
			appdata.put("nbDevice", CreateMenu.uidd);
			appdata.put("idPc", planclase);
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );	
			JSONObject object = new JSONObject(rep);
			
			String respuesta = "0";
			
			if(object.getString("success").equals("1"))
			{
				respuesta = object.getString("idDownloadRequest");
			}
			
			return respuesta;
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	public void SolicitarDescarga(String string) {
		// TODO Auto-generated method stub
		
	}

	public String ConsultarAutorizacion(String string) {
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		String respuesta = "0";
		
		try{
			appdata.put("cdRequest", 10);
			appdata.put("nbDevice", CreateMenu.uidd);
			appdata.put("idDownloadRequest", string);
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );	
			JSONObject object = new JSONObject(rep);
			
			respuesta = object.getString("success");
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return respuesta;
	}
	
	public String EnviarSolicitudEv(String planclase){
		
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject appdata = new JSONObject();
		
		try{
			appdata.put("cdRequest", 11);
			appdata.put("nbDevice", CreateMenu.uidd);
			appdata.put("idOa", planclase);
			
			String urljson = CreateMenu.servernames + "sync?data="+Uri.encode(appdata.toString());
			
			HttpGet httpget = new HttpGet(urljson);
			HttpResponse response = httpclient.execute(httpget);
			
			//Acentos y tildes
			HttpEntity resEnt = response.getEntity(); 				
			String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );	
			JSONObject object = new JSONObject(rep);
			
			String respuesta = "0";
			
			if(object.getString("success").equals("1"))
			{
				respuesta = object.getString("idDownloadRequest");
			}
			
			return respuesta;
			
		}catch(JSONException e){
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void ComprobarCarpetas()
	{
		//Comprobar CarpetaMX
		File cmx = new File(Environment.getExternalStorageDirectory()+ File.separator + "ComprendeMX");
		if(!cmx.exists())
		{
			cmx.mkdir();
		}
		
		//Comprobar CarpetaEvaluaciones
		File cev = new File(Environment.getExternalStorageDirectory() + File.separator + "ComprendeMX" + File.separator + "Evaluaciones");
		if(!cev.exists())
		{
			cev.mkdirs();
		}
	}
}