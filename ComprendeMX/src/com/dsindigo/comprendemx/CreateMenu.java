package com.dsindigo.comprendemx;


public class CreateMenu{
	
	public static String direccionMac = "";
	public static String currusername="";
	public static String servernames = null;
	public static String uidd = null;
	public static String appEnlinea = "0";
	public static String usertempo = "";
	
	public String[] MenuITems()
	{	
		String[] MenuStrings = new String[] {"Página Principal", "Lecciones", "Evaluaciones","Resultados","Lecciones Consultadas","Historial Evaluaciones","Liberar Espacio","Configuración","Versión 0.14"};
		return MenuStrings;
	}
	
	public String[] MenuEvents()
	{
		String[] MenuEvents = new String[] {"MainActivity", "PlanClases", "EvaluacionesFormativas","Resultados","Configuracion","LiberarEspacio","PlanesConsultados","HistorialEvaluaciones",""};
		return MenuEvents;
	}
	
	public String[] MenuItemsHome()
	{
		String[] MenuStrings = new String[] {"Página Principal", "Lecciones", "Evaluaciones","Resultados","Lecciones Consultadas", "Historial Evaluaciones","Sincronizar","Configuración","Liberar Espacio","Versión 0.14"};
		return MenuStrings;
	}
	
	public String[] MenuEventsHome()
	{
		String[] MenuEvents = new String[] {"MainActivity", "PlanClases", "EvaluacionesFormativas","Resultados","PlanesConsultados","HistorialEvaluaciones","Sincronizar","Configuracion", "LiberarEspacio",""};
		return MenuEvents;
	}
	//Set & Get para la mac address
	public String getDireccionMac()
	{
		return direccionMac;
	}
	
	public void setDireccionMac(String direccionMac)
	{
		CreateMenu.direccionMac = direccionMac;
	}
}