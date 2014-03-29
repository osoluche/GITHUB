package com.dsindigo.comprendemx;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConectorSQL {
	
	private static final String TAG = "ComprendeMX: ";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public ConectorSQL(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, "comprendeMX.db", null, 2);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				
				db.execSQL("create table planes (_id integer primary key autoincrement, idPc text not null, txtitle text not null, ubicacion text not null, fecha text, descargado text, syncronizado text, aprovado text);");
				db.execSQL("create table evaluaciones (_id integer primary key autoincrement, idevaluacion integer, idnombre text, idpregunta text, idrespuesta text, idarchivo text, idcontesta text, visitado text, syncronizado text);");
				db.execSQL("create table configuracion (_id integer primary key autoincrement, servername text, username text, macaddress text, temporizador text);");
				db.execSQL("create table solicitudes (_id integer primary key autoincrement, idpc text, nombre text, status text, codigo text);");
				db.execSQL("create table solicitudesev (_id integer primary key autoincrement, idoa text, nombre text, status text, codigo text);");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Actualizando base de datos de versión " + oldVersion + " a "
					+ newVersion + ", lo que destruiró todos los viejos datos");
			
			db.execSQL("DROP TABLE IF EXISTS configuracion");
			db.execSQL("DROP TABLE IF EXISTS evaluaciones");
			db.execSQL("DROP TABLE IF EXISTS planes");
			onCreate(db);
		}
	}

	// ---abrir la base de datos---
	public ConectorSQL abrir() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---cerrar la base de datos---
	public void cerrar() {
		DBHelper.close();
	}

	
	//CONSULTAS GENERICAS
	public Cursor contadorPlanes(String cid){
		String[] args = new String[]{cid};
		Cursor c = db.rawQuery("select count(*) as contador from planes where idPc=?", args);
		return c;
	}
	
	public boolean actualizarConfiguracion(String servername, String username, String publicmac, String temporizador)
	{
		Log.d("LogMX","entro!");
		
		Cursor querydb = db.rawQuery("select _id from configuracion where _id= 1", null);
		if(querydb.moveToFirst()){
		}
		else
		{
			Log.d("LogMX ", "no hay datos, creando");
		 	this.guardarConfiguracion("","","");
		}
		
		Log.d("LogMX","salio a enviar! update");
		
		ContentValues args = new ContentValues();
		args.put("servername", servername);
		args.put("username", username);
		args.put("macaddress", publicmac);
		args.put("temporizador", temporizador);
		
		//Reasingnar para reutilizar
		CreateMenu.direccionMac = publicmac;
		
		return db.update("configuracion", args, "_id = 1", null) > 0;
	}
	
	public long guardarConfiguracion(String servername, String username, String publicmac)
	{
		ContentValues contenido = new ContentValues();
		contenido.put("servername", servername);
		contenido.put("username", username);
		contenido.put("macaddress", publicmac);
		
		return db.insert("configuracion", null, contenido);
	} 
	
	public String obtenerServerPage(){
		
		Cursor querydb = db.rawQuery("select servername from configuracion where _id = 1", null);
		String servername = null;
		
		if(querydb.moveToFirst()){
			servername = querydb.getString(0);
		}
		
		if (servername == null)
		{
			servername = "";
		}
		return servername;
	}
	
	
	public String obtenerUserName()
	{
		Cursor query = db.rawQuery("select username from configuracion where _id = 1",null);
		String usrnm = null;
		if(query.moveToFirst())
		{
			usrnm = query.getString(0);
		}
		else
		{
			usrnm = "";
		}
		
		return usrnm;
	}
	
	
	public String obtenerMacAddress()
	{
		Cursor query = db.rawQuery("select macaddress from configuracion where _id = 1",null);
		String usrnm = null;
		if(query.moveToFirst())
		{
			usrnm = query.getString(0);
		}
		else
		{
			usrnm = "";
		}
		
		return usrnm;
	}
	//OPERACIONES PARA PLANES
	public long insertarPlan(String id, String title, String ubicacion){
		
		ContentValues contenido = new ContentValues();
		contenido.put("idPc", id);
		contenido.put("txtitle", title);
		contenido.put("ubicacion", ubicacion);
		return db.insert("planes", null, contenido);
	}

	
	//EVALUACIONES FORMATIVAS
	public long insertarEvaluaciones(String evaluacion, String titulo, String pregunta, String respuesta, String archivo){
	
		//_id, idevaluacion, idnombre, idpregunta, idrespuesta, idarchivo, idcontesta
		ContentValues contenido = new ContentValues();
		contenido.put("idevaluacion",evaluacion);
		contenido.put("idnombre", titulo);
		contenido.put("idpregunta", pregunta);
		contenido.put("idrespuesta", respuesta);
		contenido.put("idarchivo", archivo);
		contenido.put("idcontesta", "ND");
		
		return db.insert("evaluaciones", null, contenido);
	}
	
	public Cursor contadorEvaluaciones(String evaluacion){
		String[] args = new String[]{evaluacion};
		Cursor c = db.rawQuery("select count(*) as contador from evaluaciones where idevaluacion =?", args);
		return c;
	}
	
	public Cursor EvaluacionesFormativas()
	{
		String[] args = new String[]{"ND"};
		return db.query("evaluaciones", new String[]{ "idnombre, idevaluacion" }, "idcontesta = ?",args,"idevaluacion",null,"idevaluacion");
		//db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	public boolean eliminaRow(String DBName, long idFila) {
		return db.delete(""+DBName, "_id=" + idFila, null) > 0;
	}

	//Obtener todos los planes
	public Cursor obtenerPlanes() {
		return db.query("planes", new String[] { "_id", "txtitle", "fecha"}, null, null, null, null, null);
	}
	
	//Recuperar un plan en concreto
	public Cursor obtenerPlanByID(Integer cid) throws SQLException {	
		Cursor singleRes = db.query("planes", new String[]{"txtitle","ubicacion","idPc"}, "_id="+cid, null,null,null, null);
		return singleRes;
	}
	
	// ---actualizar una evaluacion ---
	
	//_id, idevaluacion, idnombre, idpregunta, idrespuesta, idarchivo, idcontesta
	public boolean acutalizarEvaluacion(String evaluacion, String pregunta, String respuesta) {	
		String[] params = new String[]{ evaluacion, pregunta };
		ContentValues args = new ContentValues();
		args.put("idcontesta", respuesta);
		
		return db.update("evaluaciones", args, "idevaluacion = ?  and idpregunta = ?", params) > 0;
	}

	public Cursor ComprobarExistenciaPlan(String plandeclase){
		String[] args = new String[]{plandeclase};
		Cursor c = db.rawQuery("select count(*) as contador from planes where idPc =?", args);
		return c;
	}

	//_id, idevaluacion, idnombre, idpregunta, idrespuesta, idarchivo, idcontesta
	public Cursor consultarEvaluacion(String getsplan) {
		String[] params = new String[]{getsplan};
		Cursor c = db.rawQuery("select idnombre, idpregunta, idarchivo from evaluaciones where idevaluacion = ?  and idcontesta = \"ND\"  order by idpregunta asc limit 0,1", params);
		return c;
	}
	
	public Cursor todasEvaluaciones(String evaluacion)
	{
		Cursor c = db.rawQuery("select idpregunta, idrespuesta, idcontesta from evaluaciones", null);
		return c;
	}

	public Cursor resultadosEvaluaciones() {
		Cursor c = db.rawQuery("select _id, idnombre, idpregunta, idrespuesta, idcontesta from evaluaciones order by idevaluacion asc", null);
		return c;
	}

	public Cursor imagenEvaluacion(String clickSelected) {
		String[] params = new String[]{clickSelected};
		Cursor c = db.rawQuery("select idarchivo from evaluaciones where _id = ?", params);
		return c;
	}

	public Cursor verificarEvaluacion(String evaluacionac) {
		String[] params = new String[]{evaluacionac};
		Cursor c = db.rawQuery("select count(*) from evaluaciones where idevaluacion = ?", params);
		return c;
	}
	
	//_id, idevaluacion, idnombre, idpregunta, idrespuesta, idarchivo, idcontesta
	public Cursor resultadosEvaluacionesByEvaluacion(String idevaluacion) {
		String[] params = new String[]{idevaluacion};
		Cursor c = db.rawQuery("select _id, idnombre, idpregunta, idrespuesta, idcontesta from evaluaciones  where idevaluacion = ? order by idevaluacion asc", params);
		return c;
	}

	public Cursor getRankByEvaluacion(String idevaluacion) {
		String[] params = new String[]{ idevaluacion };
		Cursor c = db.rawQuery("select count(*) as contador from evaluaciones where idcontesta = idrespuesta  and idevaluacion = ?", params);
		return c;
	}

	public String obtenerDeviceName() {
		Cursor querydb = db.rawQuery("select macaddress from configuracion where _id = 1", null);
		String devicename = null;
		
		if(querydb.moveToFirst()){
			devicename = querydb.getString(0);
		}
		
		return devicename;
	}

	//_id , idPc, txtitle , ubicacion, fecha
	public boolean visitarplan(String idcat) {
		
		String dtnow = null;
		Cursor dt = db.rawQuery("SELECT date('now')",null);
		if(dt.moveToFirst())
		{
			dtnow = dt.getString(0);
		}

		String[] params = new String[]{idcat};		
		ContentValues args = new ContentValues();
		args.put("fecha", dtnow);
		return db.update("planes", args, "_id = ?", params) > 0;		
	}

	//Borrado generico de las tablas
	public void borrarTabla(String string) {
		db.execSQL("delete from "+string+" ");
		Log.d("delmx", "borrado => " + string);
	}

	public Cursor reactivosEvaluacion(String idevaluacion) {
		String[] params = new String[]{idevaluacion};
		Cursor c = db.rawQuery("select idpregunta, idrespuesta, idcontesta from evaluaciones where idevaluacion = ? ", params);
		return c;
	}

	public Cursor EvaluacionesFormativasCompletadas() {
		return db.rawQuery("select idnombre, idevaluacion from evaluaciones where visitado = 'SI' group by idevaluacion ", null);
	}

	public boolean reportarplan(String plandeclase, String modoplan) {
		String[] params = new String[]{plandeclase};
		ContentValues args = new ContentValues();
		args.put("syncronizado", modoplan);
		return db.update("planes", args, "idPc = ?", params) > 0;
	}

	public Cursor planesdb() {
		return db.rawQuery("select idpc from planes where syncronizado = 'DB' ", null);
	}

	public boolean reportarevaluacion(String idcat) {
		
		String[] params = new String[]{idcat};
		ContentValues args = new ContentValues();
		args.put("visitado", "SI");
		args.put("syncronizado", "DB");
		return db.update("evaluaciones", args, "idevaluacion = ?", params) > 0;
	}

	public Cursor obtenerPlanesConsultados() {
		return db.rawQuery("select idPc, txtitle, syncronizado, aprovado from planes where syncronizado = 'WB' ",null);
	}

	public boolean preguntarPlan(String idplan) {
		
		Cursor c =  db.rawQuery("select count(*) from planes where idPc = " + idplan,null);
		c.moveToFirst();
		String resp  = c.getString(0);
		
		if(Integer.parseInt(resp) > 0){
			return true;
		}
		
		return false;
	}

	public boolean actualizarExistenciaPlan(String idplan) {
		String[] params = new String[] { idplan };
		ContentValues args = new ContentValues();
		args.put("syncronizado", "");
		return db.update("planes", args, "idPc = ?", params) > 0;

	}

	//Solicitudes registro, actualizacion etc
	public Cursor obtenerSolicitudes()
	{
		return db.rawQuery("select idpc, nombre, status, codigo from solicitudes where idpc not in ( select idPc from planes)", null);
	}
	public String consultarSolicitud(String idpc){
		String[] args = new String[]{idpc};
		Cursor c = db.rawQuery("select count(*) as contador from solicitudes where idpc=?", args);
		c.moveToFirst();
		String estado = null;
		estado = c.getString(0);
		return estado;
	}
	public long registrarSolicitud(String idpc, String nombre)
	{
		ContentValues contenido = new ContentValues();
		contenido.put("idpc", idpc);
		contenido.put("nombre", nombre);
		contenido.put("status", "R");
		contenido.put("codigo", "0");
		return db.insert("solicitudes", null, contenido);
	}
	public boolean actualizarSolicitud(String idpc, String idw)
	{
		String[] params = new String[]{idpc};
		ContentValues args = new ContentValues();
		
		args.put("codigo", idw);
		args.put("status", "S");
		
		return db.update("solicitudes", args, "idpc = ?",  params) > 0;
		//db.update(table, values, whereClause, whereArgs)
	}	
	
	//Evaluaciones solicitud registro, actualizacion etc
		public Cursor obtenerSolicitudesEv()
		{
			return db.rawQuery("select idoa, nombre, status, codigo from solicitudesev", null);
		}
		public String consultarSolicitudEv(String idpc){
			String[] args = new String[]{idpc};
			Cursor c = db.rawQuery("select count(*) as contador from solicitudesev where idoa=?", args);
			c.moveToFirst();
			String estado = null;
			estado = c.getString(0);
			return estado;
		}
		public long registrarSolicitudEv(String idpc, String nombre)
		{
			ContentValues contenido = new ContentValues();
			contenido.put("idoa", idpc);
			contenido.put("nombre", nombre);
			contenido.put("status", "R");
			contenido.put("codigo", "0");
			return db.insert("solicitudesev", null, contenido);
		}
		public boolean actualizarSolicitudEv(String idpc, String idw)
		{
			String[] params = new String[]{idpc};
			ContentValues args = new ContentValues();
			args.put("codigo", idw);
			args.put("status", "S");
			
			return db.update("solicitudesev", args, "idoa = ?",  params) > 0;
			//db.update(table, values, whereClause, whereArgs)
		}

		public boolean actualizarEstadoSolicitud(String idpc) {
			String[] params = new String[]{idpc};
			ContentValues args = new ContentValues();
			args.put("status", "R");
			args.put("codigo", "0");
			return db.update("solicitudes", args,  "idpc = ?", params ) > 0;
		}

		public Cursor EvaluacionesFormativasSyncronizadas() {
			return db.rawQuery("select idnombre, idevaluacion, syncronizado from evaluaciones where syncronizado = 'DB' group by idevaluacion ", null);
		}

		public boolean confirmarEvaluacionSync(String string) {
			String[] params = new String[]{string};
			ContentValues args = new ContentValues();
			args.put("syncronizado", "OK");
			return db.update("evaluaciones", args,  "idevaluacion = ?", params ) > 0;
		}

		public boolean ReportarPlanOffline(String string) {
			String[] params = new String[]{string};
			ContentValues args = new ContentValues();
			args.put("syncronizado", "DB");
			return db.update("planes", args,  "idPc = ?", params ) > 0;
		}

		public String obtenerTemporizador() {
			Cursor query = db.rawQuery("select temporizador from configuracion where _id = 1",null);
			String usrnm = null;
			if(query.moveToFirst())
			{
				usrnm = query.getString(0);
			}
			else
			{
				usrnm = "";
			}
			
			return usrnm;
		}
}