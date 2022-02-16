// Implementación de la interfaz de servidor que define los métodos remotos
// para iniciar la carga y descarga de ficheros
package afs;
import java.io.FileNotFoundException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViceImpl extends UnicastRemoteObject implements Vice {
	
	///////////////////////////////
	//readLock().lock(): solicitud de un bloqueo de lectura.
	//readLock().unlock(): eliminación de un bloqueo de lectura.
	//writeLock().lock(): solicitud de un bloqueo de escritura.
	//writeLock().unlock(): eliminación de un bloqueo de escritura.
	//////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Map, porque necesitamos el nombre de ficheros relacionado con la lista de callbacks
	private Map<String, LinkedList<VenusCB>>	structCB;
	private LockManager lockMan;
	
    public ViceImpl() throws RemoteException {
		structCB= new HashMap<String, LinkedList<VenusCB>>();
		lockMan= new LockManager();
		
    }
    
    /**
     * descarga()
     * Fase 3, se añade la referencia del callback que se enviara al servidor, seguimos las instrucciones 
     * Usamos synchronized
     */
    public synchronized ViceReader download(String fileName, String operacion, VenusCB callback /* añada los parámetros que requiera */)
          throws RemoteException, FileNotFoundException {
    	
    	/** Creamos el mutex de lectura/escritura **/
    	ReentrantReadWriteLock mutex= lockMan.bind(fileName);//Devuelve un mutex si ya existe, solo lo devuelve; en caso contrario, lo crea previamente. 
    	
    	/** Primer paso: se crea una instancia de ViceReaderImpl **/
        ViceReader instanciaViceR= new ViceReaderImpl(mutex, fileName, operacion, this);
       
        /** solicitud de un bloqueo de lectura **/
        mutex.readLock().lock(); 
        aniadeNameCallb(fileName, callback);
        
        /** Segundo paso: devuelvo el resultado del método **/
        return instanciaViceR;	
    }
    
    /**
     * carga()
     * Fase 3, se añade la referencia del callback que se enviara al servidor
     */
    public ViceWriter upload(String fileName, String operacion, VenusCB callback /* añada los parámetros que requiera */)
          throws RemoteException, FileNotFoundException {
    	/** Creamos el mutex de lectura/escritura **/
    	ReentrantReadWriteLock mutex= lockMan.bind(fileName);//Devuelve un mutex si ya existe, solo lo devuelve; en caso contrario, lo crea previamente. 

    	/** Primer paso: se crea una instancia de VicewriteImpl **/
    	ViceWriter instanciaViceUpload= new ViceWriterImpl(callback, fileName, operacion, mutex, this);
    	
        /** solicitud de un bloqueo de lectura **/
        mutex.writeLock().lock(); 
    	
    	/** Segundo paso: devuelvo el resultado del método **/
        return instanciaViceUpload;
    }
    
    
    public LockManager getLock() {
		return lockMan;
	}
    
    //tendría que añadir métodos para la gestión de la estructura:
    //......................
    public void aniadeNameCallb (String nombre, VenusCB callback) 
    {
    	if(structCB.get(nombre) == null) {
    		structCB.put(nombre, new LinkedList<>());
    	}
		    	
    	structCB.get(nombre).add(callback);

    }
    
    public void close(String nombre,VenusCB callback) throws RemoteException {
		if(!structCB.containsKey(nombre)) return;
		
		LinkedList<VenusCB> entry = structCB.get(nombre);
		Iterator<VenusCB> it = entry.iterator();
		while (it.hasNext())
		{
			VenusCB venusCB = (VenusCB) it.next();
			if(!venusCB.equals(callback))
			{
				venusCB.invalidate(nombre);
				it.remove();
			}
		}
		
    }
    
    
    
    
    
}