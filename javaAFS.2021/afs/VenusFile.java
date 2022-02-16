// Clase de cliente que define la interfaz a las aplicaciones.
// Proporciona la misma API que RandomAccessFile.
package afs;

import java.rmi.*; 
import java.io.*; 

public class VenusFile {
    public static final String cacheDir = "Cache/";
    
    /** variables de fase 1 **/
    private Venus venus;
    private String operacion;
    private RandomAccessFile ficheroLocal;
    private String fileName;   
    private ViceReader ServRead;// variable que controla las lecturas del fichero del servidor(Fase 1)

    
    /** Variables de fase 2 **/
    private boolean ficheroModificado;
    private long tamOriginal;
    private ViceWriter ServWrite;//variable que controla las ecrituras del fichero del servidor(Fase2)
    
    /** variables de fase 3 **/
    

    /**
     *  CONSTRUCTOR
     * @param venus
     * @param fileName
     * @param mode
     * @throws RemoteException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws NotBoundException
     */
    public VenusFile(Venus venus, String fileName, String operacion) throws  IOException, RemoteException, FileNotFoundException{
    	this.venus= venus;
    	this.fileName= fileName;
    	this.operacion= operacion;
    	this.ficheroModificado= false;
    	this.tamOriginal=0;
    	
    	
    	
	    /**Constructor tiene que comprobar si el fichero existe en la cache**/
	    File ficheroEnCache= new File(cacheDir + fileName);//instroducimos el directorio donde esta y el fichero    	
    	
    	//Usaremos el paquete de JAVA.io.file
    	//--> Se adjunta la documentación: https://www.delftstack.com/es/howto/java/java-check-if-a-file-exists/
	    if(!ficheroEnCache.exists()) {
	    	/**
	    	 * Si es de lectura/escritura, hay que descargarlo del servidor
	    	 * En fase 3, ademas de enviar referencia del callback con download
	    	 * Tendré que modificar la clase Vice()
	    	 */
	    	ServRead= venus.getVice().download(fileName, operacion, venus.getCallback());//añado referencia al objeto de tipo callbak
	    	
	    	/** Se abre en un RandomAccesFile donde se pondrá el fichero que no estaba en caché**/
	    	RandomAccessFile ficheroAMover= new RandomAccessFile(cacheDir + fileName, "rw");//realemente es una copia del fichero
	    	
	    	/** usaremos los métodos remotos para mover el fichero a caché**/
	    	/** Solo quedará copiar el fichero descargado y leido a la direccion de la cache(se copiara en la dirección de "ficheroMover") **/
	    	byte [] bytesLeidos;
	    	while((bytesLeidos= ServRead.read(venus.getBSize()))!= null) {
	    		ficheroAMover.write(bytesLeidos);
	    	}
	    	
	    	/**Cerramos el randomAccesFile y el fichero descargado**/
	    	ficheroAMover.close();
	    	ServRead.close();
	    	
	    	/** Finalmente se abre el fichero en cache como un randomAccesFile **/
	    	ficheroLocal= new RandomAccessFile(cacheDir + fileName, operacion);//según la guía hay que dejarlo abierto
	    	tamOriginal= ficheroLocal.length();//Para después comprobar si el tam del fichero ha sido modificado  	
	    }
	    
	    else {// Ya existe el fichero en cache :D, por lo que no es necesario descargarlo, solo lo abro
	    	/** No es necesario descargarlo del servidor y solo necesito abrirlo **/
	    	ficheroLocal= new RandomAccessFile(cacheDir + fileName, operacion);//según la guía hay que dejarlo abierto
	    	tamOriginal= ficheroLocal.length();// Para comprobar si tam del fichero ha sido modificado
	    }
    	
    }
    
    
    /**
     * 
     * @param b
     * @see Pertenece a la FASE 1
     * @throws RemoteException
     * @throws IOException
     * Se trabaja directamente sobre el fichero local
     * 
     */
    public int read(byte[] b) throws RemoteException, IOException {
        return ficheroLocal.read(b);
    }
    
    /**
     * 
     * @param b
     * @see FASE 1 y FASE 2
     * @throws RemoteException
     * @throws IOException
     *  Se trabaja directamente sobre el fichero local
     *  Esto modifica el fichero local por tanto habrá que llamar después a close()(es como un upload)
     * * Variable "ficheroModificado" controloamos si el fichero ha sufrido alguna modificación
     */
    public void write(byte[] b) throws RemoteException, IOException {
        ficheroLocal.write(b);
        ficheroModificado= true;
    }
    
    /**
     * 
     * @param p
     * @see FASE 1
     * @throws RemoteException
     * @throws IOException
     *  Se trabaja directamente sobre el fichero local
     *  Reposiciona el puntero de lectura
     */
    public void seek(long p) throws RemoteException, IOException {
    	ficheroLocal.seek(p);
    }
    
    /**
     * 
     * @param l
     * @see FASE 1 y FASE 2
     * @throws RemoteException
     * @throws IOException
     *  Se trabaja directamente sobre el fichero local
     *  Actualizamos longitud
     *  obviamente es porque se ha modificado el fichero por tanto se llamará después a close
     * Variable "ficheroModificado" controloamos si el fichero ha sufrido alguna modificación
     */
    public void setLength(long l) throws RemoteException, IOException {
        ficheroLocal.setLength(l);
    }
    
    /**
     * @see FASE 1 y FASE 2
     * @throws RemoteException
     * @throws IOException
     *  Se trabaja directamente sobre el (fichero local)
     *  (SOLO SI SE HA MODIFICADO) se ha de enviar el fichero al servidor
     *  Es como un "CARGAR" solo cuando el fichero ha sido escrito o modificado su tamanio :D
     */
    public void close() throws RemoteException, IOException {
    	
    	
    	
    	/*********************** Comprobamos si el fichero local ha sido moficiado (fase 2)***************/ 	
    	if(ficheroModificado==true) {//entonces la modificación al servidor
    		
    		/** ponemos el puntero del fichero local(el fichero modificado) al inicio **/
    		ficheroLocal.seek(0);//solo queda leerlo para enviarlo bloque a bloque a traves del write
    		
    		
    		/** usaremos la referencia remota de Venus para inicar la carga al servidor **/
    		ServWrite= this.venus.getVice().upload(fileName, operacion, venus.getCallback());

    		
    		/** obtenemos los bytes que se enviaran, es decir, el tamaño del bloque **/
    		int tam= this.venus.getBSize();//caste a entero
    		
    		
    		byte [] tamanBloque= new byte[tam];
    		
    		/** realizamos lo mismo  que en ViceReaderImpl en el método read() **/
    		int leido;
       		while((leido= ficheroLocal.read(tamanBloque))>0) {
       			//si el tamanio del bloque es mayor que lo que se lee
    			if(leido < tam) {
    				byte [] newBytesFichero= new byte[leido];//del mismo tamaño que lo leido
    				int indiceFichero=0;
    				//solo queda copiar en NewBytesFichero
    				do {
    					newBytesFichero[indiceFichero]= tamanBloque[indiceFichero];
    					indiceFichero++;
    				}while(indiceFichero<leido);
    				
    				ServWrite.write(newBytesFichero);
    			}
    			
    			//si el tam del bloque es menor que lo que se lee
    			else {
    				ServWrite.write(tamanBloque);//si el tam bloque <= leido
    			}
    		}
    		
    		/** finalmente realizamos el close correspondiente**/ 
    		ServWrite.close();
    		
    	}/* fichero modificado*/
    	
    	/** compobación si el fichero ha cambiado solo el tamaño(fase 2) **/
    	if(ficheroLocal.length()!= tamOriginal) {
    		ServWrite= venus.getVice().upload(fileName, operacion, venus.getCallback());
    		ServWrite.setLongitud(ficheroLocal.length());//llamo a método remoto creado por mi, actualiza el fich servidor
    		ServWrite.close();
    	}
    	/******************************************************************************************/
    	
    	/** fase 1 **/
    	ficheroLocal.close();
    	
    	
    }
    
    
}
