package com.dsindigo.comprendemx;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;
 
public class Descomprime { 
  private String _zipFile; 
  private String _location; 
 
  public Descomprime(String file, String location) { 
    _zipFile = file; 
    _location = location; 
 
    _dirChecker(""); 
  } 
 
  public void unzip() { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      
      
      while ((ze = zin.getNextEntry()) != null) {
    	  
        Log.v("Decompress", "Unzipping " + ze.getName());
        
      }
      
      zin.close(); 
    } catch(Exception e) { 
      Log.e("Decompress", "unzip", e); 
    } 
 
  } 
 
  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 
 
    
    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
} 
