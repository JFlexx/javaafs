// Implementación de la interfaz de cliente que define los métodos remotos
// para gestionar callbacks
package afs;

import java.io.File;
import java.rmi.*;
import java.rmi.server.*;

public class VenusCBImpl extends UnicastRemoteObject implements VenusCB {
	
	//creamos la variable que contendrá la direccion de la caché
	public static final String direccionCache= "/Cache";
    public VenusCBImpl() throws RemoteException {
    	
    }
    
    public void invalidate(String fileName /* añada los parámetros que requiera */)
        
    {
    	try{
            File nombreCache = new File("Cache");

            for(File fileN: nombreCache.listFiles()){

                if(fileN.getName().equals(fileName)){

                    fileN.delete();
                    break;
                }
            }               
        }catch(Exception e){
            
            e.printStackTrace();
        }

    }
    
    
}

