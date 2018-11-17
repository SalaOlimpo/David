package service.magic;

import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import service.utils.Constants;

/**
 * Class used to implement PaperCut functions.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class PaperClient {

	/**
	 * Used to increase or decrease an user credit.
	 * @param username	The username taken from LDAP
	 * @param credit	The amount to charge
	 * @param lang 
	 */
	public static String adjustCredit(String username, double credit, int lang) throws Exception {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(Constants.PaperCut.ADDRESS));

		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		Vector<Object> params = new Vector<>();
        params.add(Constants.PaperCut.PASSWORD);
        params.add(username);
        params.add(credit);
        params.add("");
        client.execute("api.adjustUserAccountBalance", params).toString();
        
        MySqlImpl mysql = new MySqlImpl(Constants.MySQL.DB_RHEA);
        mysql.setCreditAction(username, credit);
        mysql.closeDB();
        
        switch(lang) {
			case Constants.Language.ITALIAN:
				return "Caricati " + credit + " euro a " + username;
			default:
				return "Charged " + credit + " euro to " + username;
		}
	}
}
