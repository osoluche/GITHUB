package com.dsindigo.comprendemx;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ListaResultados extends Activity {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog pDialog;
	public static final int progress_bar_type = 1;
	String eventST[] = new CreateMenu().MenuItemsHome();
	String eventID[] = new CreateMenu().MenuEventsHome();
	
	String idevaluacion = null;
	
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remover barra superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.acivity_resultados);

		// Recupera el menu activo
		Intent intent = getIntent();
		String getExtra = intent.getStringExtra("currentMenu");
		idevaluacion = intent.getStringExtra("evaluacion");
		
		final TableLayout tabla = (TableLayout) findViewById(R.id.menulayout);

		if (TextUtils.isEmpty(getExtra)) {
			getExtra = "MainActivity";
		}

		for (int i = 0; i < eventST.length; i++) {

			// Crea la tabla para el menu
			final TableRow tableRow = new TableRow(this);
			tableRow.setTag(eventID[i]);
			tableRow.setPadding(0, 5, 0, 5);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			tableRow.setBackgroundResource(R.drawable.ic_bottomline);

			final ImageView imagen = new ImageView(this);
			final String eventName = eventID[i].toString();

			// Cambia el icono al seleccionar evento
			if (eventName.equals(getExtra)) {
				int imageId = getResources().getIdentifier(
						"ic_menu_" + i + "b", "drawable", getPackageName());
				imagen.setImageResource(imageId);
			} else {
				int imageId = getResources().getIdentifier("ic_menu_" + i,
						"drawable", getPackageName());
				imagen.setImageResource(imageId);
			}

			final TextView button = new TextView(this);
			button.setText(eventST[i]);
			button.setTextColor(Color.rgb(85, 85, 85));
			button.setTextSize(15);
			button.setGravity(Gravity.LEFT);
			button.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT));
			button.setPadding(5, 15, 0, 0);

tableRow.setOnClickListener(new View.OnClickListener() {
	        	
	        	@Override
				public void onClick(View v) {
					
	        		Object clickTagged = v.getTag();
					String intentClass = clickTagged.toString();
					
					try{
						if(intentClass == "MainActivity"){
							Intent i = new Intent( v.getContext(), MainActivity.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}
						
						if(intentClass == "PlanClases"){
							return;
						}
						
						if(intentClass == "EvaluacionesFormativas"){
							Intent i = new Intent(v.getContext(), EvaluacionesFormativas.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}
						
						if(intentClass == "Resultados"){
							Intent i = new Intent(v.getContext(), Resultados.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}
						
						if(intentClass == "Configuracion"){
							Intent i = new Intent(v.getContext(), Configuracion.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}

						if (intentClass == "LiberarEspacio") {
							Intent i = new Intent(v.getContext(),LiberarEspacio.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						
						if(intentClass == "PlanesConsultados")
						{
							Intent i = new Intent(v.getContext(),PlanesConsultados.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						
						if(intentClass == "HistorialEvaluaciones"){
							Intent i = new Intent(v.getContext(), HistorialEvaluaciones.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						
						if (intentClass == "Sincronizar") {
							
							if(isOnline())
							{
								//Enviar evaluaciones tipo SI
								SincronizarEvaluaciones cev = new SincronizarEvaluaciones();
								cev.execute();
								
								//Envia los planes de clase tipo DB
								Sincronizarplan ccp = new Sincronizarplan();
								ccp.execute();
								
								ConexionBackend cxb = new ConexionBackend();
								cxb.execute();
							}
							else
							{
								Toast.makeText(getApplicationContext(), "No hay conexión con el servidor", Toast.LENGTH_SHORT).show();
							}
						}

						
					}catch(Exception e){
						e.printStackTrace();
						Toast.makeText(getBaseContext(), "Acitivity "+ intentClass  +" not found", Toast.LENGTH_SHORT).show();
					}
				}
			});

			tableRow.addView(imagen);
			tableRow.addView(button);
			tabla.addView(tableRow);
		}

		// OPERACIONES DEL ACTIVITY
		final TableLayout table = (TableLayout) findViewById(R.id.cargadorespuestas);

		// Cabezeros
		final TableRow headerRow = new TableRow(this);
		final TextView headerCur = new TextView(this);
		final TextView headerRes = new TextView(this);
		final TextView headerAns = new TextView(this);
		final TextView headerImg = new TextView(this);

		headerCur.setWidth(700);
		headerRes.setText("Seleccion");
		headerRes.setTextColor(Color.rgb(255, 255, 255));
		headerRes.setTextSize((float) 14.0);
		headerRes.setGravity(Gravity.CENTER);
		headerRes.setPadding(0, 10, 0, 0);
		headerRes.setWidth(80);

		headerAns.setText("Correcta");
		headerAns.setTextColor(Color.rgb(255, 255, 255));
		headerAns.setTextSize((float) 14.0);
		headerAns.setGravity(Gravity.CENTER);
		headerAns.setPadding(0, 10, 0, 0);
		headerAns.setWidth(80);

		headerImg.setText("Aciertos");
		headerImg.setTextColor(Color.rgb(255, 255, 255));
		headerImg.setTextSize((float) 14.0);
		headerImg.setGravity(Gravity.CENTER);
		headerImg.setPadding(0, 10, 0, 0);
		headerImg.setWidth(80);

		headerRow.addView(headerCur);
		headerRow.addView(headerRes);
		headerRow.addView(headerAns);
		headerRow.addView(headerImg);

		table.addView(headerRow);

		ConectorSQL cnx = new ConectorSQL(getBaseContext());
		cnx.abrir();
		Cursor c = cnx.resultadosEvaluacionesByEvaluacion(idevaluacion);

		if (c.moveToFirst()) {
			do {
				// Contenedores
				final TableRow currentRow = new TableRow(this);
				final TextView txtCursor = new TextView(this);
				final TextView txtRespon = new TextView(this);
				final TextView txtCorrec = new TextView(this);
				final ImageView imgRespon = new ImageView(this);

				// _id, idnombre, idpregunta, idrespuesta, idcontesta
				String idevaluac = c.getString(0);
				String idnombres = c.getString(1);
				String ipregunta = c.getString(2);
				String respuesta = c.getString(3);
				String contestar = c.getString(4);

				currentRow.setTag(idevaluac);
				currentRow.setBackgroundResource(R.drawable.ic_bottomline);

				// Datos
				txtCursor.setText(idnombres + ": pregunta " + ipregunta);
				txtCursor.setTextColor(Color.rgb(255, 255, 255));
				txtCursor.setTextSize((float) 15.0);
				txtCursor.setPadding(0, 1, 0, 0);
				txtCursor.setWidth(700);

				txtRespon.setText(contestar);
				txtRespon.setTextColor(Color.rgb(255, 255, 255));
				txtRespon.setTextSize((float) 15.0);
				txtRespon.setBackgroundResource(R.drawable.ic_left_line);
				txtRespon.setGravity(Gravity.CENTER);
				txtRespon.setPadding(0, 10, 0, 0);
				txtRespon.setWidth(40);

				txtCorrec.setText(respuesta);
				txtCorrec.setTextColor(Color.rgb(255, 255, 255));
				txtCorrec.setTextSize((float) 15.0);
				txtCorrec.setBackgroundResource(R.drawable.ic_left_line);
				txtCorrec.setGravity(Gravity.CENTER);
				txtCorrec.setPadding(0, 10, 0, 0);
				txtCorrec.setWidth(40);

				Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_bad_answer);

				if (respuesta.equalsIgnoreCase(contestar)) {
					bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_ok_answer);
				}

				imgRespon.setImageBitmap(bImage);
				imgRespon.setPadding(15, 13, 0, 0);

				currentRow.addView(txtCursor);
				currentRow.addView(txtRespon);
				currentRow.addView(txtCorrec);
				currentRow.addView(imgRespon);

				currentRow.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Object clickEvent = v.getTag();
						String clickSelected = clickEvent.toString();

						String imgpath = null;

						ConectorSQL dbx = new ConectorSQL(context);
						dbx.abrir();
						Cursor c = dbx.imagenEvaluacion(clickSelected);
						if (c.moveToFirst()) {
							imgpath = c.getString(0);
						}
						dbx.cerrar();

						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.dialoglayout);
						dialog.setTitle("ComprendeMX - Resultados de Evaluaciones");

						String fs = imgpath;
						Bitmap bmp = BitmapFactory.decodeFile(fs);

						// set the custom dialog components - text, image and
						// button
						TextView text = (TextView) dialog
								.findViewById(R.id.text);
						text.setText("Android custom dialog example!");
						ImageView image = (ImageView) dialog
								.findViewById(R.id.image);
						image.setImageBitmap(bmp);

						Button dialogButton = (Button) dialog
								.findViewById(R.id.dialogButtonOK);

						// if button is clicked, close the custom dialog
						dialogButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

						dialog.show();

					}
				});

				table.addView(currentRow);
			} while (c.moveToNext());
		}
		
		cnx.cerrar();
		Log.d("LogMX ", "Finalizo de pintar tabla");
	}

	@Override
	public void onBackPressed() {
		return;
	}
	
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();		
		if (netInfo != null && netInfo.isAvailable()) {
			ValidaMe con = new ValidaMe();
			con.execute();
			String rescon;
			try {
				rescon = con.get();
				if(rescon != null && !rescon.isEmpty() ){
					if(Integer.parseInt(rescon) == 200){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			} catch (InterruptedException e) {
				return false;
			} catch (ExecutionException e) {
				return false;
			}
		}	
		return false;
	}
	
	private class ValidaMe extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			
			String servidor = CreateMenu.servernames + "sync";
			String conStatus = null;
			
			try {
				URL u = new URL(servidor);
				Log.d("server", "" + u);
				
				HttpURLConnection hub = (HttpURLConnection) u.openConnection();
				Log.d("httpurl", "ok");
				
				hub.setConnectTimeout(3000);
				Log.d("timeout", "ok");
				
				hub.connect();
				Log.d("connect", "ok");
				
				int code = hub.getResponseCode();
				conStatus = ""+code;
				Log.d("status", "ok " + code);
				
			} catch (Exception e) { 
				conStatus = null;
			}
			return conStatus;
		}
	}
	
	/* Enviar evaluciones realizadas */
	private class SincronizarEvaluaciones extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {

			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			
			Cursor cev = dbo.EvaluacionesFormativasSyncronizadas();
			Log.d("Contador", "#" + cev.getCount());
			
			if(cev.moveToFirst())
			{	
				do{
					Log.d("Row ", cev.getString(0) + "-" + cev.getString(2));
					
					/*Obtiene las respuestas y asigna al array*/
					Cursor r = dbo.reactivosEvaluacion(cev.getString(1));
					
					JSONArray respuestasArray = new JSONArray();
					if (r.moveToFirst()) {
						do {
							JSONObject respuestas = new JSONObject();
							try {
								String respuestavalor = null;
								if (r.getString(1).equals(r.getString(2))) {
									respuestavalor = "true";
								} else {
									respuestavalor = "false";
								}
								respuestas.put("idQuestion", r.getString(0));
								respuestas.put("response", r.getString(2));
								respuestas.put("state", respuestavalor);
								respuestasArray.put(respuestas);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} while (r.moveToNext());
					}
					
					/* Crear json para envio */
					JSONObject appdata = new JSONObject();
					try {
						appdata.put("cdRequest", 3);
						appdata.put("idOa", cev.getString(1));
						appdata.put("nbDevice", CreateMenu.uidd);
						appdata.put("questions", respuestasArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Log.d("Appdata", appdata.toString());
					
					/* Enviar la evaluacion */
					HttpClient httpclient = new DefaultHttpClient();
					String urljson = CreateMenu.servernames + "/sync?data="+ Uri.encode(appdata.toString());

					try {
						HttpGet httpget = new HttpGet(urljson);
						HttpResponse response = httpclient.execute(httpget);

						//Acentos y tildes
						HttpEntity resEnt = response.getEntity(); 				
						String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
						JSONObject object = new JSONObject(rep);

						String respuesta = object.getString("success");
						if(respuesta.equalsIgnoreCase("1"))
						{
							dbo.confirmarEvaluacionSync(cev.getString(1));
						}
						
						Log.d("procesado", "Ev=> "+ cev.getString(0));

					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}while(cev.moveToNext());
			}
			
			dbo.cerrar();
			return null;
		}
		
	}
	
	// Descarga de plan de clases
	private class ConexionBackend extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(1);
		}

		@Override
		protected String doInBackground(String... params) {
			ConectorJson json = new ConectorJson();
			JSONObject planes = json.obtenerListado();

			try {
				int success = Integer.parseInt(planes.getString("success"));
				if (success == 1) {
					JSONArray planesArr = planes.getJSONArray("idPcs");
					for (int x = 0; x < planesArr.length(); x++) {
						String plandeclase = planesArr.getString(x).toString();
						ConectorSQL sx = new ConectorSQL(getBaseContext());
						sx.abrir();
						Cursor ex = sx.ComprobarExistenciaPlan(plandeclase);
						if (ex.moveToFirst()) {
							if (Integer.parseInt(ex.getString(0)) == 0) {

								Log.d("logmx plan nuevo ", "Procesando "+ plandeclase);
								Log.d("logmx server", CreateMenu.servernames);

								JSONObject app = json.GetPlayById(Integer.parseInt(plandeclase));

								try {

									String response = app.getString("success").toString();
									Log.d("Plan Clase", "> "+response);
									
									if (Integer.parseInt(response) == 1) {

										String titulo = app.getString("nbResource");
										String idplan = app.getString("idplanclase");
										String enlace = app.getString("txDownloadUrl");
										
										// Obtener el nombre de la carpeta
										String fileName = Uri.parse(enlace).getLastPathSegment();
										String fileLoca = (String) fileName.subSequence(0,fileName.length() - 4);

										ConectorSQL conn = new ConectorSQL(getBaseContext());
										conn.abrir();

										// Prevenir duplicado de registro
										Cursor c = conn.contadorPlanes(idplan);
										if (c.moveToFirst()) {
											if (c.getInt(0) == 0) {

												// Iniciar descarga
												Downloader df = new Downloader();
												boolean responsedw = df.DownloadMyFile(enlace,"");

												if (responsedw) {
													
													// Descomprimir archivo
													String location = Environment
															.getExternalStorageDirectory()
															+ File.separator
															+ "ComprendeMX"
															+ File.separator;
													String filezip = Environment
															.getExternalStorageDirectory()
															+ File.separator
															+ "ComprendeMX"
															+ File.separator
															+ fileLoca + ".zip";

													ZipFile zipf = new ZipFile(
															filezip);
													zipf.extractAll(location);

													// Borrar archivo temporal
													File deletefw = new File(
															filezip);
													deletefw.delete();

													// Insertar plan de trabajo
													conn.insertarPlan(idplan,
															titulo, fileLoca);
												} else {
													Log.d("logmx", "nada! "
															+ idplan);
												}
											} else {
												Log.d("MX > ",
														"No se permiten duplicados");
											}
										}
										conn.cerrar();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								} catch (ZipException e) {
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
									Log.d("mx", "descarga fallida");
								}
							} else {
								//Log.d("logmx plan checker "," Plan de clases existente " + plandeclase);
							}
						}
						sx.cerrar();
					}
					return "Completado";
				} else {
					return "Fallido";
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String app) {
			SyncEvaluaciones syncE = new SyncEvaluaciones();
			syncE.execute();
		}
	}

	// Descarga de evaluaciones formativas
	private class SyncEvaluaciones extends AsyncTask<String, String, String> {

		protected void onPreExecute() {

			ConectorJson js = new ConectorJson();
			js.ComprobarCarpetas();
		}

		@Override
		protected String doInBackground(String... params) {

			ConectorJson json = new ConectorJson();
			JSONObject appdata = json.obtenerListado();

			try {
				String response = appdata.getString("success").toString();
				if (response.equalsIgnoreCase("1")) {

					JSONArray jsonarray = appdata.getJSONArray("idOas");

					for (int i = 0; i < jsonarray.length(); i++) {

						String ideval = jsonarray.getString(i);

						JSONObject jsonEval = json.getEvaluacion(ideval);

						Log.d("Evaldata => ", jsonEval.toString());
						try {

							String responsEval = jsonEval.getString("success")
									.toString();

							if (responsEval.equalsIgnoreCase("1")) {

								// Verificacion con la base
								ConectorSQL eqx = new ConectorSQL(
										getBaseContext());
								eqx.abrir();

								Cursor vev = eqx.verificarEvaluacion(ideval);

								if (vev.moveToFirst()) {
									if (Integer.parseInt(vev.getString(0)
											.toString()) == 0) {

										String downloadfile = jsonEval
												.getString("txDownloadUrl")
												.toString().replace(" ", "%20");
										String tituloEvala = jsonEval
												.getString("nbResource")
												.toString();

										// Obtener el nombre de la carpeta
										String EvalFileName = Uri
												.parse(downloadfile)
												.getLastPathSegment()
												.replace(" ", "_");
										String Localizacion = (String) EvalFileName
												.subSequence(
														0,
														EvalFileName.length() - 4);

										// Crear directorio de la evaluacion
										File directorioEv = new File(
												Environment
														.getExternalStorageDirectory()
														+ File.separator
														+ "ComprendeMX"
														+ File.separator
														+ "Evaluaciones"
														+ File.separator
														+ Localizacion);
										if (!directorioEv.exists()) {
											directorioEv.mkdir();
										}

										Log.d("logmx", "descarga => "
												+ downloadfile);

										// Iniciar descarga
										Downloader df = new Downloader();
										boolean responsedw = df.DownloadMyFile(
												downloadfile, "Evaluaciones"
														+ File.separator
														+ Localizacion);

										if (responsedw) {
											// Descomprimir archivo
											String location = Environment
													.getExternalStorageDirectory()
													+ File.separator
													+ "ComprendeMX"
													+ File.separator
													+ "Evaluaciones"
													+ File.separator
													+ Localizacion
													+ File.separator;
											String filezip = Environment
													.getExternalStorageDirectory()
													+ File.separator
													+ "ComprendeMX"
													+ File.separator
													+ "Evaluaciones"
													+ File.separator
													+ Localizacion
													+ File.separator
													+ Localizacion + ".zip";

											unzipfile ds = new unzipfile();
											ds.unzip(location, filezip);

											// Borrar archivo temporal
											File deletefw = new File(filezip);
											deletefw.delete();

											// Insertar Evaluacion formativa
											JSONArray arrayrespuestas = jsonEval
													.getJSONArray("questions");
											for (int k = 0; k < arrayrespuestas
													.length(); k++) {
												// Separacion de las preguntas
												JSONObject jr = new JSONObject(
														arrayrespuestas
																.getString(k)
																.toString());

												String pregunta =  jr.getString("idQuestion");
												String respuesta = jr.getString("response");

												String archivo = null;

												// Casting hardcodeado para
												// encontrar tipo de imagen
												File aJpg = new File(
														Environment
																.getExternalStorageDirectory()
																+ File.separator
																+ "ComprendeMx"
																+ File.separator
																+ "Evaluaciones"
																+ File.separator
																+ Localizacion
																+ "/"
																+ pregunta
																+ "_1.jpg");
												File aPng = new File(
														Environment
																.getExternalStorageDirectory()
																+ File.separator
																+ "ComprendeMx"
																+ File.separator
																+ "Evaluaciones"
																+ File.separator
																+ Localizacion
																+ "/"
																+ pregunta
																+ "_1.png");
												File aGif = new File(
														Environment
																.getExternalStorageDirectory()
																+ File.separator
																+ "ComprendeMx"
																+ File.separator
																+ "Evaluaciones"
																+ File.separator
																+ Localizacion
																+ "/"
																+ pregunta
																+ "_1.gif");

												if (aJpg.exists()) {
													archivo = aJpg.toString();
												} else if (aPng.exists()) {
													archivo = aPng.toString();
												} else if (aGif.exists()) {
													archivo = aGif.toString();
												}

												// Agregar evaluacion formativa
												// a la db
												eqx.insertarEvaluaciones(
														ideval, tituloEvala,
														pregunta, respuesta,
														archivo);
											}
										} else {
											Log.d("logmx", "No pude descargar "
													+ Localizacion);
										}
									} else {
										Log.d("logmx", "Evaluacion existente "
												+ ideval);
									}
								}
								eqx.cerrar();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// SyncEvaluacionesById nt = new
						// SyncEvaluacionesById(jsonarray.getString(i).toString());
						// nt.execute(ideval);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String app) {
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Lecciones y Evaluaciones actualizados.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class Sincronizarplan extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			
			Log.d("Logmx", "Envio de planes de clase");

			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			
			Cursor p = dbo.planesdb();
			if(p.moveToFirst())
			{
				do{
					ReportarPlanVisitado rpv = new ReportarPlanVisitado();
					rpv.execute(p.getString(0));
				}while(p.moveToNext());
			}
			dbo.cerrar();
			return null;
		}
		
		protected void onPostExecute(String app) {
			
			
		}
	}

	protected class ReportarPlanVisitado extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			
			Log.d("Logmx", "Enviando plan " + params[0]);
			
			JSONObject appdata = new JSONObject();
			try {
				appdata.put("cdRequest", 4);
				appdata.put("nbDevice", CreateMenu.direccionMac);
				appdata.put("idPc", params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			final HttpParams httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
			HttpClient httpclient = new DefaultHttpClient(httpParams);
			
			String urljson = CreateMenu.servernames + "/sync?data="+Uri.encode(appdata.toString());
			String success = ""; 
			try {
				
				Log.d("logmxsena", appdata.toString());
				
				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);
				
				//Acentos y tildes
				HttpEntity resEnt = response.getEntity(); 				
				String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
				JSONObject object = new JSONObject(rep);
				
				success = object.getString("success");
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				
				success = null;
			} catch (IOException e) {
				e.printStackTrace();
				success = null;
			} catch (JSONException e) {
				e.printStackTrace();
				success=null;
			}
			
			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			if(success == null || Integer.parseInt(success) == 0)
			{
				dbo.reportarplan( params[0], "DB");
			}
			
			ValidarServidor cxx = new ValidarServidor();
			cxx.execute();
			
			String verserver;
			try {
				verserver = cxx.get();
				if(verserver.equals("OK")){
					if(Integer.parseInt(success) == 1){
						dbo.reportarplan(params[0], "WB");
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			dbo.cerrar();
			
			CreateMenu.appEnlinea = "ON";
			return null;
		}
	}
	
	protected class ValidarServidor extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			
			JSONObject appdata = new JSONObject();
			try {
				appdata.put("cdRequest", 5);
				appdata.put("nbDevice", CreateMenu.direccionMac);
				appdata.put("ipDevice", "");
				appdata.put("idDevice", CreateMenu.direccionMac);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			final HttpParams httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
			HttpClient httpclient = new DefaultHttpClient(httpParams);
			
			String urljson = CreateMenu.servernames + "/sync?data="+Uri.encode(appdata.toString());
			
			try {
				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);
				
				//Acentos y tildes
				HttpEntity resEnt = response.getEntity(); 				
				String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
				JSONObject object = new JSONObject(rep);
				Log.d("Response", object.toString());
				
				return "OK";
				
			} catch (ClientProtocolException e) {
				return "PR";
			} catch (IOException e) {
				return "IO";
			} catch (JSONException e) {
				return "JS";
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Sincronizando con el servidor, este proceso puede tardar varios minutos ...");
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(false);
		pDialog.show();
		return pDialog;
	}

}