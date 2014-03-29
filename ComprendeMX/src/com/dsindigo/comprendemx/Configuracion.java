package com.dsindigo.comprendemx;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Configuracion extends Activity implements OnClickListener{
	String eventST[] = new CreateMenu().MenuITems();  
	String eventID[] = new CreateMenu().MenuEvents();
	String publicMac = "";
	String password = "";
	public EditText txt;
	
	TableRow rowpass;
	TableRow rowuser;
	TableRow rowdire;
	TableRow rowclav;
	TableRow rowbtns;
	TableRow rowtemp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remover barra superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.acitivty_configuracion);
		TextView usernm = (TextView)findViewById(R.id.username);
		usernm.setText(CreateMenu.currusername + " Vamos a configurar tu aplicación");
		
		//Advice
		Toast.makeText(getApplicationContext(), "Si deseas cancelar, presiona la tecla de retroceso.", Toast.LENGTH_LONG).show();
		
		//Buscar mac y guardar
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String direccionMac = wInfo.getMacAddress();
		publicMac = direccionMac;
		
		final TableLayout tabla = (TableLayout)findViewById(R.id.menulayout);
		
		//Recupera el menu activo
		Intent intent = getIntent();
		String getExtra = intent.getStringExtra("currentMenu");
		
		for (int i = 0; i < eventST.length; i++) {

			// Crea la tabla para el menu
	        final TableRow tableRow = new TableRow(this);
	        tableRow.setTag(eventID[i]);
	        tableRow.setPadding(0, 5, 0, 5);
	        tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	        tableRow.setBackgroundResource(R.drawable.ic_bottomline);
	        
	        final ImageView imagen = new ImageView(this);
	        final String eventName = eventID[i].toString();
	        
	        //Cambia el icono al seleccionar evento
	        if( eventName.equals(getExtra)){
	        	int imageId = getResources().getIdentifier("ic_menu_"+i+"b","drawable", getPackageName());
	        	imagen.setImageResource(imageId);
	        }else{
	        	int imageId = getResources().getIdentifier("ic_menu_"+i,"drawable", getPackageName());
	        	imagen.setImageResource(imageId);
	        }
	        
	        final TextView button = new TextView(this);
	        button.setText(eventST[i]);
	        button.setTextColor(Color.rgb(85,85,85));
	        button.setTextSize(getRequestedOrientation());
	        button.setGravity(Gravity.LEFT);
	        button.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT));
	        button.setPadding(5, 15, 0, 0);
	        
	        tableRow.setOnClickListener(new View.OnClickListener() {
	        	
	        	@Override
				public void onClick(View v) {
	        		return;
				}
			});
	        
	        tableRow.addView(imagen);
	        tableRow.addView(button);
	        tabla.addView(tableRow);
	    }
		
		
		//Validacion de password
		rowpass = (TableRow)findViewById(R.id.rowacceso);
		rowuser = (TableRow)findViewById(R.id.rowusuario);
		rowdire = (TableRow)findViewById(R.id.rowdireccion);
		rowclav = (TableRow)findViewById(R.id.rowpassword);
		rowbtns = (TableRow)findViewById(R.id.rowbutton);
		rowtemp = (TableRow)findViewById(R.id.rowtemporizador);
		
		rowpass.setAlpha(0);
		rowuser.setAlpha(0);
		rowdire.setAlpha(0);
		rowbtns.setAlpha(0);
		rowtemp.setAlpha(0);
		
		EditText txtclave = (EditText)findViewById(R.id.claveacceso);
		txtclave.addTextChangedListener(new TextWatcher(){
	        
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if(TextUtils.equals(s, "1qazxsw2")){
					rowclav.setAlpha(0);
					rowpass.setAlpha(1);
					rowuser.setAlpha(1);
					rowdire.setAlpha(1);
					rowbtns.setAlpha(1);
					rowtemp.setAlpha(1);
				}
			}
		});
		
		//Operaciones
		EditText srvrnm = (EditText)findViewById(R.id.txtserver);
		EditText nmuser = (EditText)findViewById(R.id.txtusername);
		TextView txtmac = (TextView)findViewById(R.id.macaddress);
		EditText tempor = (EditText)findViewById(R.id.temporizador);
		
		txtmac.setText(publicMac);
		
		ConectorSQL ap = new ConectorSQL(getBaseContext());
		ap.abrir();
		String servername = ap.obtenerServerPage();
		String usernamest = ap.obtenerUserName();
		String temporizae = ap.obtenerTemporizador();
		
		/*Tiempo ms a segundos de la db*/
		if(temporizae.isEmpty() || temporizae.length() <= 0  || temporizae == null){
			temporizae = "3000";
		}
		
		int tiempos = Integer.parseInt(temporizae) / 1000;
		
		/*Asignar a los input text*/
		srvrnm.setText(servername);
		nmuser.setText(usernamest);
		tempor.setText(""+tiempos);
		
		ap.cerrar();
		
		//Btn de guardar
		Button  btnsalvar = (Button)findViewById(R.id.btnguardarconf);
		btnsalvar.setOnClickListener(this);
		
		Button btncancelar = (Button)findViewById(R.id.btncancelar);
		btncancelar.setText("Cancelar");
		btncancelar.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v == findViewById(R.id.btncancelar))
		{
			Intent i = new Intent( getBaseContext() , MainActivity.class);
			i.putExtra("currentMenu", "MainActivity");
			startActivity(i); 
		}
		
		if(v == findViewById(R.id.btnguardarconf))
		{
		
			EditText txtserver = (EditText)findViewById(R.id.txtserver);
			EditText txtuserna = (EditText)findViewById(R.id.txtusername);
			EditText tempor = (EditText)findViewById(R.id.temporizador);
			
			String infoserver = txtserver.getText().toString();
			String infouserna = txtuserna.getText().toString();
			String infotempo  = tempor.getText().toString();
			
			/*Valida si el tempo esta vacio*/
			if(infotempo.length() <= 0)
			{
				infotempo = "30";
			}
			
			/*Tiempo de segundos a ms*/
			int tiempoms = Integer.parseInt(infotempo) * 1000;
			infotempo = "" + tiempoms;
			
			if(infoserver.length() > 0)
			{
				
				Pattern regex = Pattern.compile("^http://.*");
				Matcher compr = regex.matcher(infoserver);
				
				if(compr.matches())
				{
					ValidarServer vs = new ValidarServer();
					vs.execute(infoserver);
					
					try {
						String rs = vs.get();
						
						if(rs.equals("OFF"))
						{
							Toast.makeText(getBaseContext(), "Error intentando comunicarse con el servidor", Toast.LENGTH_LONG).show();
						}
						else
						{
							
							String lastss = infoserver.substring(infoserver.length()-1, infoserver.length());
							String flagsm = "/";
							
							if(!lastss.equals(flagsm))
							{
								infoserver = infoserver + "/";
							}
							
							ConectorSQL db = new ConectorSQL(getBaseContext());
							db.abrir();
							
							Log.d("save: ", infotempo);
							if(db.actualizarConfiguracion(infoserver, infouserna, publicMac, infotempo))
							{
								new AlertDialog.Builder(this)
							      .setMessage("Configuracion Guardada!")
							      .setTitle("ComprendeMX")
							      .setCancelable(true)
							      .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							         @Override
									public void onClick(DialogInterface dialog, int whichButton){
							        	 Intent i = new Intent( getBaseContext() , MainActivity.class);
							        	 i.putExtra("currentMenu", "MainActivity");
							        	 startActivity(i); 
							         }})
							      .show();
							}
							db.cerrar();
							
						}
							
					} 
					catch (InterruptedException e){} 
					catch (ExecutionException e){}
				}
				else
				{
					Builder alerta =  new AlertDialog.Builder(this);
					alerta.setMessage("Ingresa una URL valida");
					alerta.setTitle("ComprendeMx");
					alerta.setCancelable(true);
					alerta.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int whichButton){}});
					alerta.show();
				}			
			}
			else
			{
				Builder alertaErr = new AlertDialog.Builder(this);
				alertaErr.setMessage("Es requerido tener una URL de servidor");
			    alertaErr.setTitle("ComprendeMX");
			    alertaErr.setCancelable(true);
			    alertaErr.setNeutralButton(android.R.string.cancel,
			         new DialogInterface.OnClickListener() {
			         @Override
					public void onClick(DialogInterface dialog, int whichButton){}});
			     alertaErr.show();
			}
		}
	}
	
	private class ValidarServer extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			
			String servidor = params[0].toString();
			String conStatus = null;
			try {
				URL u = new URL(servidor);
				HttpURLConnection hub = (HttpURLConnection) u.openConnection();
				hub.setConnectTimeout(3000);
				hub.connect();
				conStatus = "ON";
			} catch (Exception e) { 
				conStatus = "OFF";
				Log.d("Conexion: ", "Server timeout");
			}	
			return conStatus;
		}	
	}
	
	@Override
	public void onBackPressed() {	
		Intent i = new Intent( getBaseContext() , MainActivity.class);
		i.putExtra("currentMenu", "MainActivity");
		startActivity(i);
	}
	
}
