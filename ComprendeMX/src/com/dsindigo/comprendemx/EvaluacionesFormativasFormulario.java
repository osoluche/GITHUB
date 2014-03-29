package com.dsindigo.comprendemx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class EvaluacionesFormativasFormulario extends Activity implements
		OnClickListener {

	String eventST[] = new CreateMenu().MenuItemsHome();
	String eventID[] = new CreateMenu().MenuEventsHome();
	String idpregunta = null;
	String idevaluacion = null;
	String touchState = null;

	private ProgressDialog mDialog;

	ZoomControls zoom;
	ImageView imgview;
	private int currentX;
	private int currentY;
	LinearLayout container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remover barra superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_evaluaciones_formulario);

		Button btnsalir = (Button) findViewById(R.id.regresarbtn);
		btnsalir.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d("logmxdx", "clicked regresar");
				Intent i = new Intent(v.getContext(),
						EvaluacionesFormativas.class);
				i.putExtra("currentMenu", "EvaluacionesFormativas");
				startActivity(i);

			}
		});

		// Recupera los parametros extras
		Intent intent = getIntent();
		String getExtra = intent.getStringExtra("currentMenu");
		String evaluacion = intent.getStringExtra("evaluacion");

		// Asigna id requerido para update
		idevaluacion = evaluacion;

		// Crea el menu
		final TableLayout tabla = (TableLayout) findViewById(R.id.menulayout);
		for (int i = 0; i < eventST.length; i++) {

			// Crea la tabla para el menu
			final TableRow tableRow = new TableRow(this);
			tableRow.setTag(eventID[i]);
			tableRow.setPadding(0, 5, 0, 5);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
			button.setTextSize(getRequestedOrientation());
			button.setGravity(Gravity.LEFT);
			button.setLayoutParams(new TableRow.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT));
			button.setPadding(5, 15, 0, 0);

			tableRow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Object clickTagged = v.getTag();
					String intentClass = clickTagged.toString();

					try {
						if (intentClass == "MainActivity") {
							Intent i = new Intent(v.getContext(),
									MainActivity.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}

						if (intentClass == "PlanClases") {
							Intent i = new Intent(v.getContext(),
									PlanClases.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}

						if (intentClass == "EvaluacionesFormativas") {
							return;
						}

						if (intentClass == "Resultados") {
							Intent i = new Intent(v.getContext(),
									Resultados.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}

						if (intentClass == "Configuracion") {
							Intent i = new Intent(v.getContext(),
									Configuracion.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
						}

						if (intentClass == "LiberarEspacio") {
							Intent i = new Intent(v.getContext(),
									LiberarEspacio.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						if (intentClass == "PlanesConsultados") {
							Intent i = new Intent(v.getContext(),
									PlanesConsultados.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						if (intentClass == "HistorialEvaluaciones") {
							Intent i = new Intent(v.getContext(),
									HistorialEvaluaciones.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}

						if (intentClass == "Sincronizar") {

							if (isOnline()) {

								// Envia los planes de clase tipo DB
								Sincronizarplan ccp = new Sincronizarplan();
								ccp.execute();

								ConexionBackend cxb = new ConexionBackend();
								cxb.execute();
							} else {
								Toast.makeText(getApplicationContext(),
										"No hay conexión con el servidor",
										Toast.LENGTH_SHORT).show();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getBaseContext(),
								"Acitivity " + intentClass + " not found",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			tableRow.addView(imagen);
			tableRow.addView(button);
			tabla.addView(tableRow);
		}

		// Inicializa los objetos
		imgview = (ImageView) findViewById(R.id.imagenpregunta);
		RadioGroup tbl = (RadioGroup) findViewById(R.id.respuestaopt);
		Button btn = (Button) findViewById(R.id.siguiente);

		// Inician operaciones del view
		ConectorSQL db = new ConectorSQL(this);
		db.abrir();

		Cursor evaluaciones = db.consultarEvaluacion(evaluacion);

		if (evaluaciones.moveToFirst()) {

			// idnombre, idpregunta, idarchivo
			// Asinga idpregunta para evaluacion
			idpregunta = evaluaciones.getString(1);

			// Muestra el numero de pregunta en curso
			String contadorpregunta = "Pregunta número: "
					+ evaluaciones.getString(1).toString();
			TextView lblContador = (TextView) findViewById(R.id.contadorpreguntas);
			lblContador.setText(contadorpregunta);
			lblContador.setTextSize(17);
			lblContador.setPadding(0, 15, 0, 10);
			lblContador.setTextColor(Color.WHITE);

			// Colocar Recursos;
			// AssetManager assetManager = getAssets();
			try {

				container = (LinearLayout) findViewById(R.id.linearlt);

				String fs = evaluaciones.getString(2);
				Bitmap bmp = BitmapFactory.decodeFile(fs);

				// set the drawable to imageview
				imgview.setImageBitmap(bmp);
				imgview.setScaleType(ScaleType.FIT_CENTER);

				imgview.setPadding(20, 20, 0, 0);

				btn.setOnClickListener(this);

				// zoom
				zoom = (ZoomControls) findViewById(R.id.zoomControls1);
				zoom.setOnZoomInClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						imgview.scrollTo(0, 0);
						container.scrollTo(0, 0);

						float x = imgview.getScaleX();
						float y = imgview.getScaleY();

						Log.d("zoom in => ", "x: "+ imgview.getScaleX());
						
						imgview.setScaleX((float) (x + .5));
						imgview.setScaleY((float) (y + .5));
					}
				});

				zoom.setOnZoomOutClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						imgview.scrollTo(0, 0);
						container.scrollTo(0, 0);

						float x = imgview.getScaleX();
						float y = imgview.getScaleY();

						Log.d("zoom out => ", "x: "+ imgview.getScaleX());
						
						if(imgview.getScaleX() > 1)
						{
							imgview.setScaleX((float) (x - .5));
							imgview.setScaleY((float) (y - .5));
						}
						else
						{
							Log.d("Alcanzo el limite", "");
						}
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

			btnsalir.setVisibility(View.GONE);

		} else {

			// Actualizar ID de la evaluacion como visitado
			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			dbo.reportarevaluacion(evaluacion);
			dbo.cerrar();

			// Pintar tabla
			TableRow trow = (TableRow) findViewById(R.id.tableImageContainer);
			trow.setBackgroundColor(Color.argb(1, 32, 32, 32));

			// Coloca una imagen generica
			imgview.setImageResource(R.drawable.img_dummy_all);

			// Desabilita los radio
			tbl.setVisibility(View.GONE);

			// Desabilita el boton
			btn.setVisibility(View.GONE);

			// Desabilitar movimientos
			touchState = "disabled";

			// Envio segundo plano
			EnviarEvaluaciones px = new EnviarEvaluaciones();
			px.execute();

		}

		db.cerrar();
	}

	@Override
	public void onClick(View v) {
		RadioGroup rg = (RadioGroup) findViewById(R.id.respuestaopt);
		int idButton = rg.getCheckedRadioButtonId();

		if (idButton > 0) {
			RadioButton b = (RadioButton) findViewById(idButton);
			String idrespuesta = (String) b.getText();

			ConectorSQL db = new ConectorSQL(getBaseContext());
			db.abrir();
			db.acutalizarEvaluacion(idevaluacion, idpregunta, idrespuesta);
			db.cerrar();

			Intent i = new Intent(getBaseContext(),
					EvaluacionesFormativasFormulario.class);
			i.putExtra("currentMenu", "EvaluacionesFormativas");
			i.putExtra("evaluacion", idevaluacion);
			startActivity(i);
		} else {
			Toast.makeText(getBaseContext(), "Selecciona una opción",
					Toast.LENGTH_SHORT).show();
		}
	}

	// Descarga de plan de clases
	private class EnviarEvaluaciones extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			
			// Crear array para el json
			ConectorSQL db = new ConectorSQL(getBaseContext());
			db.abrir();

			Cursor r = db.reactivosEvaluacion(idevaluacion);
			
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
			db.cerrar();

			// Crear json para envio
			JSONObject appdata = new JSONObject();
			try {
				appdata.put("cdRequest", 3);
				appdata.put("idOa", idevaluacion);
				appdata.put("nbDevice", CreateMenu.uidd);
				appdata.put("questions", respuestasArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			String urljson = CreateMenu.servernames + "/sync?data="+ Uri.encode(appdata.toString());

			Log.d("logmxurlserver", urljson);

			try {
				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);
				String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(jsonResult);
				
				String respuesta = object.getString("success");
				if(respuesta.equalsIgnoreCase("1"))
				{
					ConectorSQL dbo = new ConectorSQL(getBaseContext());
					dbo.abrir();
					dbo.confirmarEvaluacionSync(idevaluacion);
					dbo.cerrar();
				}
				
				

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return urljson;
		}

	}

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

	@Override
	public void onBackPressed() {
		Toast.makeText(getBaseContext(), "Ups! no se vale hacer trampa!",
				Toast.LENGTH_SHORT).show();
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (touchState == null) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				int x2 = (int) event.getRawX();
				int y2 = (int) event.getRawY();
				container.scrollBy(currentX - x2, currentY - y2);
				currentX = x2;
				currentY = y2;
				break;
			}
			case MotionEvent.ACTION_UP: {
				break;
			}
			}
		}
		return true;
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

								JSONObject app = json.GetPlayById(Integer
										.parseInt(plandeclase));

								try {

									String response = app.getString("success")
											.toString();

									if (Integer.parseInt(response) == 1) {

										String titulo = app
												.getString("nbResource");
										String idplan = app
												.getString("idplanclase");
										String enlace = app
												.getString("txDownloadUrl");

										// Obtener el nombre de la carpeta
										String fileName = Uri.parse(enlace)
												.getLastPathSegment();
										String fileLoca = (String) fileName
												.subSequence(0,
														fileName.length() - 4);

										ConectorSQL conn = new ConectorSQL(
												getBaseContext());
										conn.abrir();

										// Prevenir duplicado de registro
										Cursor c = conn.contadorPlanes(idplan);
										if (c.moveToFirst()) {
											if (c.getInt(0) == 0) {

												// Iniciar descarga
												Downloader df = new Downloader();
												boolean responsedw = df
														.DownloadMyFile(enlace,
																"");

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
								Log.d("logmx plan checker ",
										" Plan de clases existente "
												+ plandeclase);
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
			Log.d("logmxSync", "Inicia sincronizacion de evaluaciones");
			SyncEvaluaciones cev = new SyncEvaluaciones();
			cev.execute();
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

												String pregunta = jr
														.getString("idQuestion");
												String respuesta = jr
														.getString("response");

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
			mDialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Planes y evaluaciones actualizados.", Toast.LENGTH_SHORT)
					.show();
		}
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
	
	protected Dialog onCreateDialog(int id) {
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("Sincronizando con el servidor, este proceso puede tardar varios minutos ...");
		mDialog.setIndeterminate(false);
		mDialog.setMax(100);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(false);
		mDialog.show();
		return mDialog;
	}

	private class Sincronizarplan extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();

			Cursor p = dbo.planesdb();
			if (p.moveToFirst()) {
				do {
					ReportarPlanVisitado rpv = new ReportarPlanVisitado();
					rpv.execute(p.getString(0));
				} while (p.moveToNext());
			}
			dbo.cerrar();
			return null;
		}

	}

	protected class ReportarPlanVisitado extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			JSONObject appdata = new JSONObject();
			try {
				appdata.put("cdRequest", 4);
				appdata.put("nbDevice", CreateMenu.direccionMac);
				appdata.put("idPc", params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			String urljson = CreateMenu.servernames + "/sync?data="
					+ Uri.encode(appdata.toString());
			String success = "";
			try {

				Log.d("logmxsena", appdata.toString());

				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);

				String jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(jsonResult);

				Log.d("logmx", "response" + object.toString());

				success = object.getString("success");

			} catch (ClientProtocolException e) {
				e.printStackTrace();

				success = null;
			} catch (IOException e) {
				e.printStackTrace();
				success = null;
			} catch (JSONException e) {
				e.printStackTrace();
				success = null;
			}

			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			if (success == null || Integer.parseInt(success) == 0) {
				dbo.reportarplan(params[0], "DB");
			}

			if (Integer.parseInt(success) == 1) {
				dbo.reportarplan(params[0], "WB");
			}
			dbo.cerrar();

			CreateMenu.appEnlinea = "ON";
			return null;
		}
	}

	protected class ValidarServidor extends AsyncTask<String, String, String> {

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

			HttpClient httpclient = new DefaultHttpClient();
			String urljson = CreateMenu.servernames + "/sync?data="
					+ Uri.encode(appdata.toString());
			Log.d("logmx", appdata.toString());

			try {
				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);

				String jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(jsonResult);

				Log.d("object", object.toString());

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
}