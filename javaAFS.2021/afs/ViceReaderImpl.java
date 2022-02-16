// Implementación de la interfaz de servidor que define los métodos remotos
// para completar la descarga de un fichero
package afs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViceReaderImpl extends UnicastRemoteObject implements ViceReader {
    private static final String AFSDir = "AFSDir/";
    private ReentrantReadWriteLock leeCerrojo;
    private RandomAccessFile instanciaRandomAF;
    private String fileName;
    private ViceImpl referencia;
    
   /**
    * 1º Añado el parametro para operación ya que se instancia en la clase ViceImpl,
    * 	propagamos también la excepción FileNotFoundException proveniente de la clase dicha
    * 2º Instanciamos en el constructor un objeto de la clase RandomAccessFile
    * 3º Se instancia la clase randomAccesFile en los métodos remotos también
    */
    public ViceReaderImpl(ReentrantReadWriteLock leeCerrojo,String fileName, String operacion, ViceImpl referencia /* añada los parámetros que requiera */)
		    throws RemoteException, FileNotFoundException {
    	//los ficheros según la guía están en el directorio AFSdir
    	this.fileName= fileName;
    	instanciaRandomAF= new RandomAccessFile(AFSDir + fileName, operacion);
    	this.referencia= referencia;
    	this.leeCerrojo= leeCerrojo;
    }
    
    /** Métodos remotos 

    
    /**
     *	read()
     */
    public byte[] read(int tam) throws RemoteException, IOException {
    	
    
    	//Para el evitar el problema del paso por referencia, se creará una variable para recoger el valor del fichero
    	byte [] bytesFichero= new byte[tam];// tam es el tamaño de los bloques que iremos leyendo
    	int leido;
    	
    	if((leido= instanciaRandomAF.read(bytesFichero))==-1) {
    		leeCerrojo.readLock().unlock();
    		return null;
    	}
    	
    	/** Puede ocurrir 2 casos
    	 * --> 1. Que el tamaño de lo que quiero leer(bloque) es mayor al tam del fichero no hay problema
    	 * --> 2. Pero si el tamaño del bloque sea mayor que el del fichero, entonces hay problema porque puede sobreescribir
    	 **/
    	//1.
    	else if(leido >= tam) {
    		return bytesFichero;
    	}
    	
    	//2. Simplemente creo variable del tamanio del fichero leido y copio
    	else {// habrá que copiar los bytes del fichero para no escribir de más
    		byte[] newBytesFichero= new byte[leido];
    		int indiceFichero=0;
    		do {
    			newBytesFichero[indiceFichero]= bytesFichero[indiceFichero];//Solo copiará los necesario del tamn del fichero
    			indiceFichero++;
    			
    		}while(indiceFichero < leido);
    		return newBytesFichero;
    	}	
    	
    }
    
    
    /**
     *	close()
     */
    public void close() throws RemoteException,IOException {
    	referencia.getLock().unbind(fileName);
		instanciaRandomAF.close();
    }
    
}       
