// Interfaz de servidor que define los métodos remotos
// para completar la carga de un fichero
package afs;
import java.io.IOException;
import java.rmi.*;

public interface ViceWriter extends Remote {
	/**
	 * 
	 * @param b
	 * @throws RemoteException
	 * @throws IOException: al usar RandomAccesFile
	 */
	public void write(byte [] b) throws RemoteException, IOException;
	
	/**
	 * 
	 * @throws RemoteException
	 * @throws IOException: al usar RandomAccesFile
	 * 
	 */
    public void close() throws RemoteException, IOException;
    /* añada los métodos remotos que requiera */
    
    
////////////////////// Clases añadidas por mi ////////////////////////////////////////
    
    /**
     * 
     * @param longitud
     * @throws IOException
     * @see Se necesita  algo para actualizar la longitud del fichero en caso de que hubiera cambiado de tamaño
     */
    public void setLongitud(long longitud) throws RemoteException, IOException;
    
}       

