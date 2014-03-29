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
import android.database.SQLException;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class HistorialEvaluaciones extends Activity {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mDialog;
	public static final int progress_bar_type = 1;

	String eventST[] = new CreateMenu().MenuItemsHome();
	String eventID[] = new CreateMenu().MenuEventsHome();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("inicio", "");
		
		// Remover barra superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_planclases);

		final TableLayout tabla = (TableLayout) findViewById(R.id.menulayout);

		// Recupera el menu activo
		Intent intent = getIntent();
		String getExtra = intent.getStringExtra("currentMenu");

		//Comprueba carpetas
		ConectorJson folders = new ConectorJson();
		folders.ComprobarCarpetas();		

		for (int i = 0; i < eventST.length; i++) {

			// Crea la tabla para el menu
			final TableRow tableRow = new TableRow(this);
			tableRow.setTag(eventID[i]);
			tableRow.setPadding(0, 5, 0, 5);
			tableRow.setLayoutParams(new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			tableRow.setBackgroundResource(R.drawable.ic_bottomline);

			final ImageView imagen = new ImageView(this);
			final String eventName = eventID[i].toString().trim();

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
							Intent i = new Intent(v.getContext(),
									EvaluacionesFormativas.class);
							i.putExtra("currentMenu", intentClass);
							startActivity(i);
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
						if (intentClass == "PlanesConsultados") {
							Intent i = new Intent(v.getContext(),
									PlanesConsultados.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}
						if (intentClass == "HistorialEvaluaciones") {
							return;
						}
						if (intentClass == "Sincronizar") {
							if(isOnline()){
								
								SincronizarEvaluaciones cev = new SincronizarEvaluaciones();
								cev.execute();
								
								Sincronizarplan ccp = new Sincronizarplan();
								ccp.execute();
								
								ConexionBackend cxb = new ConexionBackend();
								cxb.execute();	
							}
							else
							{
								Toast.makeText(getApplicationContext(), "No hay conexion con el servidor.", Toast.LENGTH_SHORT).show();
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

		// Inicia rendereo

		// Consulta del historial

		try {
			
			Log.d("inicia consulta", "");
			
			if (isOnline()) {
				ConsultarHistorial json = new ConsultarHistorial();
				json.execute();

				final TableLayout tablaPlanes = (TableLayout) findViewById(R.id.cargadorplanes);

				try {
					JSONObject historial;
					historial = json.get();

					JSONArray metodos = historial.getJSONArray("idOasHist");

					ConectorSQL dbo = new ConectorSQL(getBaseContext());
					dbo.abrir();

					// Actualizar db
					for (int i = 0; i < metodos.length(); i++) {

						String values = metodos.getString(i);
						JSONObject appdata = new JSONObject(values);

						String planclase = appdata.getString("nombreevaluacion");
						String idplancla = appdata.getString("id_oa");

						String verificador = dbo.consultarSolicitudEv(idplancla);
						if (Integer.parseInt(verificador) == 0) {
							dbo.registrarSolicitudEv(idplancla, planclase);
						}
					}

					// Mostrar contenido
					Cursor sc = dbo.obtenerSolicitudesEv();
					if (sc.moveToFirst()) {
						do {
							// Genericos de la lista
							final TableRow tableRows = new TableRow(this);
							tableRows.setPadding(0, 5, 0, 5);
							tableRows.setLayoutParams(new TableLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.FILL_PARENT));
							tableRows
									.setBackgroundResource(R.drawable.ic_bottomline);

							final ImageView imgview = new ImageView(this);
							imgview.setImageResource(R.drawable.ic_planes_list);

							final TextView newText = new TextView(this);
							newText.setText(sc.getString(1));
							newText.setTextColor(Color.rgb(255, 255, 255));
							newText.setTextSize(15);
							newText.setGravity(Gravity.LEFT);
							newText.setLayoutParams(new TableRow.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.MATCH_PARENT));
							newText.setPadding(5, 15, 0, 0);

							// Agregar vistas a la tabla
							tableRows.addView(imgview);
							tableRows.addView(newText);
							// idpc, nombre, status, codigo
							String estado = sc.getString(2);

							Log.d("emx", estado);

							// Registrados sin actividad
							if (estado.equals("R")) {
								
								final ImageView imgRespon = new ImageView(this);
								Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_blank);
								imgRespon.setImageBitmap(bImage);
								imgRespon.setPadding(15, 13, 0, 0);
								
								final Button solbtn = new Button(this);
								solbtn.setText("Solicitar");
								solbtn.setTextColor(Color.WHITE);
								solbtn.setTag(sc.getString(0));
								solbtn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {

										// Solicitar autorizacion
										String plancla = v.getTag().toString();

										ConsultarSolicitud cnn = new ConsultarSolicitud();
										cnn.execute(plancla);

										try {
											String idsolicitud = cnn.get();

											if (idsolicitud.length() > 0) {
												ConectorSQL dbx = new ConectorSQL(getApplicationContext());
												dbx.abrir();
												dbx.actualizarSolicitudEv(plancla,idsolicitud);
												dbx.cerrar();

												// Recargar
												Intent intent = getIntent();
												finish();
												startActivity(intent);
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										} catch (ExecutionException e) {
											e.printStackTrace();
										}
									}
								});
								tableRows.addView(imgRespon);
								tableRows.addView(solbtn);
							}

							if (estado.equals("S")) {
								// Preguntar si esta autorizada
								ConsultarAutorizacion cau = new ConsultarAutorizacion();
								cau.execute(sc.getString(3));
								String auth = cau.get();

								// Autorizada
								if(auth.equals("1"))
								{
									final ImageView imgRespon = new ImageView(this);
									Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_ok_answer);
									imgRespon.setImageBitmap(bImage);
									imgRespon.setPadding(15, 13, 0, 0);
									
									//Proceso descarga
									DescargarPlan dcp = new DescargarPlan();
									dcp.execute(sc.getString(0));

									String calificacion = dcp.get();

									// Proceso descarga
									final TextView solbtn = new TextView(this);
									solbtn.setText(calificacion);
									solbtn.setTextColor(Color.WHITE);

									tableRows.addView(imgRespon);
							        tableRows.addView(solbtn);
								} 
								else if( auth.equals("0")) /* RECHAZADO */
								{	
									final ImageView imgRespon = new ImageView(this);
									Bitmap bImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_bad_answer);
									imgRespon.setImageBitmap(bImage);
									imgRespon.setPadding(15, 13, 0, 0);
									
									final Button solbtn = new Button(this);
									solbtn.setText("Solicitar");
									solbtn.setTextColor(Color.WHITE);
									solbtn.setTag(sc.getString(0));
									solbtn.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {

											// Solicitar autorizacion
											String plancla = v.getTag().toString();

											ConsultarSolicitud cnn = new ConsultarSolicitud();
											cnn.execute(plancla);

											try {
												String idsolicitud = cnn.get();

												if (idsolicitud.length() > 0) {
													ConectorSQL dbx = new ConectorSQL(getApplicationContext());
													dbx.abrir();
													dbx.actualizarSolicitudEv(plancla,idsolicitud);
													dbx.cerrar();

													// Recargar
													Intent intent = getIntent();
													finish();
													startActivity(intent);
												}
											} catch (InterruptedException e) {
												e.printStackTrace();
											} catch (ExecutionException e) {
												e.printStackTrace();
											}
										}
									});
									
									tableRows.addView(imgRespon);
									tableRows.addView(solbtn);
								}
								else
								{
									TextView txt = new TextView(this);
									txt.setText("Esperando autorización ...");
									txt.setTextColor(Color.rgb(255, 255, 255));
									txt.setTextSize(15);
									txt.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
									txt.setPadding(5, 15, 0, 0);
									tableRows.addView(txt);
								}
							}

							// Agregar row a la tabla
							tablaPlanes.addView(tableRows);
						} while (sc.moveToNext());
					}

					dbo.cerrar();

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),"No hay conexión con el servidor", Toast.LENGTH_SHORT).show();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Operaciones async
	public class ConsultarHistorial extends
			AsyncTask<String, String, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {

			ConectorJson cnx = new ConectorJson();
			JSONObject appdata = cnx.ConsultarHistorial();

			// Log.d("logmx consultar do", appdata.toString());
			return appdata;
		}
	}

	public class ConsultarSolicitud extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			ConectorJson cnx = new ConectorJson();
			String respuesta = cnx.EnviarSolicitudEv(params[0]);

			Log.d("logmx respuesta", " Solicitud =" + params[0] + "r="
					+ respuesta);
			return respuesta;
		}
	}

	public class SolicitarDescarga extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			ConectorJson cnx = new ConectorJson();
			cnx.SolicitarDescarga(params[0]);
			return null;
		}
	}

	public class ConsultarAutorizacion extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			ConectorJson cnx = new ConectorJson();
			String response = cnx.ConsultarAutorizacion(params[0]);
			return response;
		}
	}

	public class DescargarPlan extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected String doInBackground(String... params) {

			ConectorJson cnx = new ConectorJson();
			JSONObject jsondata = cnx.getEvaluacion(params[0]);

			String calificacion = null;
			Log.d("logmxrecive ", jsondata.toString());

			try {
				calificacion = jsondata.getString("nuResult");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			// Cerrar bloque dialogo
			mDialog.dismiss();
			return calificacion;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("Sincronizando con el servidor, este proceso puede tardar varios minutos ...");
		mDialog.setIndeterminate(false);
		mDialog.setMax(100);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(false);
		return mDialog;
	}

	// Funciones para sincronizar
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

										Log.d("logmx plan nuevo ", "Procesando "
												+ plandeclase);
										Log.d("logmx server", CreateMenu.servernames);

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
							"Lecciones y Evaluaciones actualizados.", Toast.LENGTH_SHORT)
							.show();
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

			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
			HttpClient httpclient = new DefaultHttpClient(httpParams);

			String urljson = CreateMenu.servernames + "/sync?data=" + Uri.encode(appdata.toString());

			try {
				HttpGet httpget = new HttpGet(urljson);
				HttpResponse response = httpclient.execute(httpget);
				int code = response.getStatusLine().getStatusCode();
				Log.d("LMX", "status = " + code);

				String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(jsonResult);
				Log.d("object server=>", object.toString());

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

	@Override
	public void onBackPressed() {
		return;
	}
	


	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();		
		if (netInfo != null && netInfo.isAvailable()) {
			
			Log.d("entro", "");
			
			ValidaMe con = new ValidaMe();
			con.execute();
			String rescon;
			
			try {
				rescon = con.get();
				if(rescon != null && !rescon.isEmpty() ){
					Log.d("entro http", "");
					
					if(Integer.parseInt(rescon) == 200){
						Log.d("entro 200", "");
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
	
}