// Implementación de la interfaz de servidor que define los métodos remotos
// para completar la carga de un fichero
package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViceWriterImpl extends UnicastRemoteObject implements ViceWriter {
    private static final String AFSDir = "AFSDir/";
    private String filename; 
    private RandomAccessFile ficheroAcces; 
    VenusCB callback;
    private ReentrantReadWriteLock leeCerrojo;
    private ViceImpl referencia;

    /**
     * 
     * @param fileName
     * @param operacion
     * @throws RemoteException
     * @throws FileNotFoundException 
     * @see Es similar a "ViceReader"
     */
    public ViceWriterImpl(VenusCB callback, String fileName, String operacion, ReentrantReadWriteLock leeCerrojo, ViceImpl referencia /* añada los parámetros que requiera */)
		    throws RemoteException, FileNotFoundException {
    	this.filename= fileName;
    	ficheroAcces= new RandomAccessFile(AFSDir+fileName, operacion);
    	this.callback= callback;
    	this.leeCerrojo= leeCerrojo;
    	this.referencia= referencia;
    	
    }
    
    /************* métodos remotos **************/
    
    /**
     * Al ser un método void, no habrá problemas con la variable pasada por referencia y javaRMI
     * Trabajamos  con  randomAccesFile
     */
    public void write(byte [] b) throws RemoteException, IOException {
    	ficheroAcces.write(b);

    }
    
    /**
     * Simple close() usando el fichero abierto por randomAccesFile
     */
    public void close() throws RemoteException, IOException {
    	referencia.close(filename, callback);
    	leeCerrojo.writeLock().unlock();
    	referencia.getLock().unbind(filename);
        ficheroAcces.close();
    }
    
/******************* Funciones añadidas por mi *******************************************/
	/**
	 * actualiza longitud
	 * Función añadida por mi
	 */
	public void setLongitud(long longitud) throws RemoteException, IOException {	
		ficheroAcces.setLength(longitud);
	}


	public void seek(long seek) throws RemoteException, IOException {
		ficheroAcces.seek(seek);
	}
    
   
}       

