// Clase de cliente que inicia la interacción con el servicio de
// ficheros remotos
package afs;

import java.net.MalformedURLException;
import java.rmi.*; 



public class Venus {
	
	public static final String DirAFS = "/AFS";
	//Registry_host
	// Página donde se especifica como acceder a las variables de entorno en java+
	//--> http://chuwiki.chuidiang.org/index.php?title=Acceder_a_variables_de_entorno_desde_Java
	private String rHost;
	private int rPort;
	private int bSize;
	private Vice srv;
	
	/**Empezando fase 3**/
	//instancia VenusCBImpl, referencia a callback
	private VenusCB callback;
	
	/**
	 * CLiente: búsqueda del servicios a 3 variables localces:
	 * REGISTRY_HOST
	 * REGISTRY_PORT
	 * BLOCKSIZE
	 * -Y la operación lookup
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
    public Venus() throws  RemoteException {
    	this.rHost= System.getenv().get("REGISTRY_HOST");
		this.rPort = Integer.parseInt(System.getenv().get("REGISTRY_PORT"));
    	this.bSize= Integer.parseInt(System.getenv().get("BLOCKSIZE"));//necesito parsaro a entero
    	//lookup
    	//instanciando al servicio Vice
    	try {
			this.srv= (Vice)Naming.lookup("//"+ rHost+ ":" + rPort +  DirAFS);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//instancia a venuusCBImpl
    	callback= new VenusCBImpl();
    	
    }
    
    //En mi caso obtaré por usar getters()
    /**
     * 
     * @return getter de variable de entorno REGISTRY_HOST
     */
    public String getRHost() {
    	return this.rHost;
    }
    
    /**
     * 
     * @return getter de variable de entorno REGISTRY_PORT
     */
    public int getRPort() {
    	return this.rPort;
    }
    
    /**
     * 
     * @return getter de variable de entorno BLOCKSIZE
     */
    public int getBSize() {
    	return this.bSize;
    }
    
    /**
     * 
     * @return getter de la operación lookup
     */
    public Vice getVice() {
    	return this.srv;
    }
    
    public VenusCB getCallback() {
    	return callback;
    }
    
}
