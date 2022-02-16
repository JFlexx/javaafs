// Interfaz de servidor que define los métodos remotos para iniciar
// la carga y descarga de ficheros
package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.*;

public interface Vice extends Remote {
	
	/**
	 * 
	 * @param fileName
	 * @param operacion: Lectura o Lectura/escritura
	 * 			mensaje descarga, cliente almacena el fichero en cache local
	 * @throws RemoteException
	 * @throws FileNotFoundException: Esto se añade según la documentación en download
	 * 			y se debe propagar a ViceReadeImpl
	 * 
	 *  En fase 3, añado la refencia de callabck que se enviará al servidor
	 */
    public ViceReader download(String fileName, String operacion, VenusCB callback /* añada los parámetros que requiera */)
          throws RemoteException, FileNotFoundException, IOException;
    
    /**
     * 
     * @param fileName
     * @param operacion: Lectura o Lectura/escritura
     * 		mensaje de carga, si se ha modificado, se envía el fichero completo al servidor
     * @throws RemoteException
     *  En fase 3, añado la refencia de callabck que se enviará al servidor
     */
    public ViceWriter upload(String fileName, String operacion, VenusCB callback /* añada los parámetros que requiera */)
          throws RemoteException, FileNotFoundException, IOException;

    /* añada los métodos remotos que requiera */
}
       
