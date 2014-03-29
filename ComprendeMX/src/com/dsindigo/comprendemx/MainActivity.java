package com.dsindigo.comprendemx;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog pDialog;
	public static final int progress_bar_type = 1;
	public WebView web;
	public TextView usernm;
	public TableLayout tabla = null;
	public String urlUpdate = null;
	private Handler handler;
	
	String eventST[] = new CreateMenu().MenuItemsHome();
	String eventID[] = new CreateMenu().MenuEventsHome();
	String idplanclase = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Recupera el menu activo
		Intent intent = getIntent();
		String getExtra = intent.getStringExtra("currentMenu");

		//Manejo de layout
		if(getExtra == null || getExtra.equalsIgnoreCase("MainActivity"))
		{
			//Version portada
			setContentView(R.layout.activity_main_long);

		}
		else
		{
			//Version plan de clase
			setContentView(R.layout.activity_main);
		}

		
		// Crea folders
		File comprendemx = new File(Environment.getExternalStorageDirectory() + File.separator + "ComprendeMX");
		if (comprendemx.exists()) 
		{} else {
			comprendemx.mkdir();
		}

		// Inicializa la base de datos
		ConectorSQL db = new ConectorSQL(getBaseContext());

		// Recupera el user name
		db.abrir();
		
		//Configura la direccion del servidor para todo el sitio
		CreateMenu.servernames = db.obtenerServerPage();
		CreateMenu.uidd = db.obtenerDeviceName();
		
		CreateMenu.direccionMac = db.obtenerMacAddress();
		CreateMenu.usertempo = db.obtenerTemporizador();
		
		if(CreateMenu.usertempo.isEmpty() || CreateMenu.usertempo.length() <= 0){
			CreateMenu.usertempo  = "3000";
		}
		
		Log.d("temporizador", "T:" + CreateMenu.usertempo);
		
		//Obtiene el nombre del usuario
		String username = db.obtenerUserName();
		usernm = (TextView) findViewById(R.id.username);
		
		try {
			String usernames = new String(username);
			usernm.setText("Hola que tal " + new String(usernames.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		
		CreateMenu.currusername = username;

		db.cerrar();
		
		Log.d("Primer online", "Notificaciones");
		if(isOnline())
		{			
			/* Lanzar push */
			handler = new Handler();
			runnable.run();			
		}
		
		if(!CreateMenu.currusername.isEmpty()){
			
			//Validar server
			ValidarServidor  vs= new ValidarServidor();
			vs.execute();
			String vsResponse = null;
			
			try {
				vsResponse = vs.get();
			} 
			catch (InterruptedException e1) {} 
			catch (ExecutionException e1) {}
			
			if(vsResponse != "OK")
			{
				Toast.makeText(getBaseContext(), "No hay conexión al servidor.", Toast.LENGTH_SHORT).show();
			}
		}
		
		// Creacion del menu
		tabla = (TableLayout) findViewById(R.id.menulayout);

		for (int i = 0; i < eventST.length; i++) {

			// Crea la tabla para el menu
			final TableRow tableRow = new TableRow(this);
			tableRow.setTag(eventID[i]);
			tableRow.setPadding(5, 5, 0, 5);
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
			button.setTextSize(15);
			button.setGravity(Gravity.LEFT);
			button.setLayoutParams(new TableRow.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT));
			button.setPadding(7, 15, 0, 0);

			tableRow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Intent intents = getIntent();
					String getExtras = intents.getStringExtra("currentMenu");
					if(getExtras == null || getExtras.equals("MainActivity"))
					{
						
					}
					else
					{
						//Detener el webview
						web.loadUrl("");
					}
					
					//Onclick
					Object clickTagged = v.getTag();
					String intentClass = clickTagged.toString();

					try {
						if (intentClass == "MainActivity") {
							
							String tokens = "MainActivity";
							Intent intent = getIntent();
							String getExtra = intent.getStringExtra("currentMenu");
							
							if(getExtra == null || getExtra.equals(tokens))
							{
//								String html = "<html><head><title>TITLE!!!</title></head>";
//								html += "<body style='background:#323232'><img src='splash.png' width='100%' style='position:absolute; top:0px; right:10px;'/></body></html>";
//								web.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null);
								return;
							}
							else
							{
								Intent i = new Intent(v.getContext(),MainActivity.class);
								i.putExtra("currentMenu", intentClass);
								finish();
								startActivity(i);
							}							
						}

						if (intentClass == "PlanClases") {
							Intent i = new Intent(v.getContext(),PlanClases.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}

						if (intentClass == "EvaluacionesFormativas") {
							Intent i = new Intent(v.getContext(),EvaluacionesFormativas.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}

						if (intentClass == "Resultados") {
							Intent i = new Intent(v.getContext(),Resultados.class);
							i.putExtra("currentMenu", intentClass);
							finish();
							startActivity(i);
						}

						if (intentClass == "Configuracion") {
							Intent i = new Intent(v.getContext(),Configuracion.class);
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

						if (intentClass == "Sincronizar") {
							
							String html = "<html><head><title>TITLE!!!</title></head>";
							html += "<body style='background:#323232'><img src='splash.png' width='100%' style='position:absolute; top:0px; right:10px;'/></body></html>";
							web.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null);

							
							Log.d("Segundo online", "menu sincronizar");
							if(isOnline())
							{
								//Envia las evaluaciones tipo SI
								SincronizarEvaluaciones sev = new SincronizarEvaluaciones();
								sev.execute();
									
								//Envia los planes de clase tipo DB
								Sincronizarplan ccp = new Sincronizarplan();
								ccp.execute();
								
								//Descarga planes de clase
								ConexionBackend cxb = new ConexionBackend();
								cxb.execute();
							}
							else
							{
								Toast.makeText(getApplicationContext(), "No hay conexión con el servidor.", Toast.LENGTH_SHORT).show();
							}
						}

						if (intentClass == "LiberarEspacio") {
							Intent i = new Intent(v.getContext(),LiberarEspacio.class);
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
						
						if(intentClass == "")
						{
							String html = "<html><head><title>TITLE!!!</title></head>";
							html += "<body style='background:#323232'><img src='splash.png' width='100%' style='position:absolute; top:0px; right:10px;'/></body></html>";
							web.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getBaseContext(), "Acitivity " + intentClass + " not found",Toast.LENGTH_SHORT).show();
					}
				}
			});

			tableRow.addView(imagen);
			tableRow.addView(button);
			tabla.addView(tableRow);

		}
		
		String plancuid = intent.getStringExtra("currentPlan");
		idplanclase = plancuid;
		
		String cargarwb = "";
		
		if (TextUtils.isEmpty(plancuid)) {
			db.abrir();
			cargarwb = db.obtenerServerPage();

			if (cargarwb.length() == 0) {
				
				new AlertDialog.Builder(this)
						.setMessage("No existe una configuración, vamos a crearla!")
						.setTitle("ComprendeMX")
						.setCancelable(true)
						.setNeutralButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,int whichButton){
										Intent i = new Intent(getBaseContext(),Configuracion.class);
										i.putExtra("currentMenu", "Configuracion");
										finish();
										startActivity(i);
									}
								}).show();
			}
			else
			{	
				if(CreateMenu.appEnlinea.length() >= 1)
				{
					
					//xqs
					ReportarActividad ra = new ReportarActividad();
					ra.execute();
					
					try {
						String uName = ra.get();
						
						if(uName != null)
						{
							usernm.setText("Hola, " + new String(uName.getBytes("UTF-8")));
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			db.cerrar();
			cargarwb ="NW";
		} 
		else 
		{	
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_layout_root));

			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText("Presiona el botón de retroceso para regresar al plan de clase.");
			
			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
			
			db.abrir();
			Cursor planview = db.obtenerPlanByID(Integer.parseInt(plancuid));

			if (planview.moveToFirst()) {
				cargarwb = "file://"+ Environment.getExternalStorageDirectory()+ File.separator + "ComprendeMX" + File.separator + planview.getString(1) + File.separator + "index.html";
				
				//Evita duplicado de peticion
				String cplan = intent.getStringExtra("currentPlan");
				String csign = intent.getStringExtra("signatureid");
				if(cplan.equals(csign)){
					
					Log.d("Tercer online", "reportar plan");
					
					if(isOnline()){
						ReportarPlanVisitado rpv = new ReportarPlanVisitado();
						rpv.execute(planview.getString(2));
					}
					else
					{
						db.ReportarPlanOffline(planview.getString(2));
					}
					csign = "0";
				}
			}
			db.cerrar();
		}

		web = (WebView) findViewById(R.id.cargadorpaginas);		
		web.setWebViewClient(new WebViewClient());
		
		WebSettings webSettings = web.getSettings();
		webSettings.setPluginsEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		
		webSettings.setLoadWithOverviewMode(false);
		webSettings.setUseWideViewPort(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDisplayZoomControls(false);
		
		web.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
			    return false;
			}
		});
		
		if(cargarwb.equalsIgnoreCase("NW"))
		{
			String html = "<html><head><title>TITLE!!!</title></head>";
			html += "<body style='background:#323232'><img src='splash.png' width='100%' style='position:absolute; top:0px; right:10px'/></body></html>";
			web.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "UTF-8", null);
		}else
		{
			web.loadUrl(cargarwb);
		}

	}
	
	// Enviar planes de clases visitados
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
	
	/* Enviar evaluciones realizadas */
	private class SincronizarEvaluaciones extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {

			Log.d("Entro a evaluaciones","CMX");
			
			ConectorSQL dbo = new ConectorSQL(getBaseContext());
			dbo.abrir();
			
			Cursor cev = dbo.EvaluacionesFormativasSyncronizadas();
			
			Log.d("Contador evaluaciones", "#" + cev.getCount());
			
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
		
	protected class ReportarActividad extends AsyncTask<String, String, String>{

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
				
				//Metodo anterior para leer el string
				//String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
				JSONObject object = new JSONObject(rep);
				
				//Devuelve el nuevo nombre del usuario
				String username = object.getString("nbStudent");
				
				//Actualiza la base de datos
				if(username.length() > 0)
				{
					ConectorSQL dbx = new ConectorSQL(getBaseContext());
					dbx.abrir();
					dbx.actualizarConfiguracion(CreateMenu.servernames, new String(username.getBytes("UTF-8")), CreateMenu.direccionMac, CreateMenu.usertempo);
					dbx.cerrar();
					return username;
				}
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
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
	
	@SuppressWarnings("unused")
	private class Conector extends AsyncTask<String, String, JSONObject> {
			
		@Override
		protected JSONObject doInBackground(String... params) {
			Log.d("x","y");
			
			try {
				InetAddress address = InetAddress.getByName("http://comprende.mx");
				if(address.isReachable(1000))
				{
					JSONObject object = null;
					String urljson = "http://comprende.mx/comprendeMx/comprende/actualiza-version.txt";
					
					final HttpParams httpParams = new BasicHttpParams();
				    HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
					HttpClient httpclient = new DefaultHttpClient(httpParams);
					
					HttpGet httpget = new HttpGet(urljson);
					HttpResponse response;
					try {	
						
						response = httpclient.execute(httpget);
						
						//Acentos y tildes
						HttpEntity resEnt = response.getEntity(); 				
						String rep = EntityUtils.toString( resEnt, HTTP.ISO_8859_1 );
						object = new JSONObject(rep);
						
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Log.d("xx", "zz");
					return object;
				}
				else
				{
					Log.d("no-connection", "sin internerd");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private class DescargarActualizacion extends AsyncTask<String, String, String>
	{
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected String doInBackground(String... params) {
			
			//Verificacion del folder
			File dwfolder =  new File(Environment.getExternalStorageDirectory() + File.separator + "ComprendeMX" + File.separator + "Descargas");
			if(!dwfolder.exists())
			{
				dwfolder.mkdir();
			}

			//Inicia la descarga
			Downloader df = new Downloader();
			boolean responsedw = df.DownloadMyFile(params[0], "Descargas");
			
			String EvalFileName = Uri.parse(params[0]).getLastPathSegment().replace(" ","_");
			String Localizacion = (String) EvalFileName.subSequence(0,EvalFileName.length() - 4);
			
			Log.d("lmx", Localizacion + "apk");
			
			//Comprobacion de la descarga
			if(responsedw)
			{
				File apkFile = new File(Environment.getExternalStorageDirectory()+File.separator+"ComprendeMx"+File.separator+"Descargas"+File.separator+ Localizacion +".apk");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
				startActivity(intent);
			}
			
			pDialog.dismiss();
			return null;
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

	@Override
	public void onBackPressed() {	
		if(idplanclase != null){
			if(idplanclase.length() > 0)
			{
				String webUrl = web.getUrl();
				String webname = webUrl.substring(webUrl.lastIndexOf('/') + 1);
				String indexPage = "index.html";
				
				if(webname.equals(indexPage))
				{
					Intent i = new Intent(getBaseContext(),PlanClases.class);
					i.putExtra("currentMenu", "PlanClases");
					finish();
					startActivity(i);
				}
				else
				{
					web.loadUrl("");
					Intent i = new Intent( getBaseContext() , MainActivity.class);
					i.putExtra("currentMenu", "PlanClases");
					i.putExtra("currentPlan", idplanclase);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}
		}
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
					}
					else
					{
						return false;
					}
				}
				else{
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
	
	private Runnable runnable = new Runnable() 
	{

	    public void run() 
	    {
	    	int tempo = Integer.parseInt(CreateMenu.usertempo);
	    	PreguntarNovedades solicitudes = new PreguntarNovedades();
	    	solicitudes.execute();
	    	handler.postDelayed(this, tempo);
	    }
	};
	
	private class PreguntarNovedades extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... params) {
			ConectorJson json = new ConectorJson();
			JSONObject planes = json.obtenerListado();
			
			try {
				
				if(planes.getString("success").equalsIgnoreCase("1"))
				{
					JSONArray countPlanes = planes.getJSONArray("idPcs");
					JSONArray countEvalua = planes.getJSONArray("idOas");
					
					if(countPlanes.length() > 0 || countEvalua.length() > 0)
					{	
						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						Notification notification = new Notification(R.drawable.ic_launcher,"Existe nuevo contenido para descargar.", System.currentTimeMillis());
						
						Intent notificationIntent = new Intent(getBaseContext(), MainActivity.class);
						PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,notificationIntent, 0);
						
						notification.setLatestEventInfo(MainActivity.this, "Comprende", "Notificaciones", pendingIntent);
						notificationManager.notify(0, notification);
						
					}
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String cantidad = String.valueOf(planes.length());
			return cantidad;
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		//Clear all notification
		NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();
	} 
}
