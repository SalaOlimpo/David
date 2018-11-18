package service.utils;

import java.util.Map;

/**
 * Class used to contain User / Password and other parameter which can change.
 *  You should really love it.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class Constants {

	/**
	 * Used by
	 * 	(1) TakeAction
	 */
	public class General {
		public static final String CERTBOT	= "/etc/init.d/nginx stop;"
				+ "certbot --authenticator standalone --installer nginx -d www.salaolimpo.cloud -d www.salaolimpo.eu -d alexa.salaolimpo.cloud --noninteractive;";
		public static final String REBOOT	= "reboot now";
		public static final String SHA_PASS = "820c1b9cab99d79933aeeb0c925b52adcaac58dcc0ba235642e5fe0ed642b4a4";
		
		public static final int ALEXA		= 1;
		public static final int TELEGRAM	= 2;
	}
	
	/**
	 * Used by
	 * 	(1) TakeAction
	 */
	public class Language {
		public static final int ITALIAN		= 1;
		public static final int ENGLISH	= 2;
	}
	
	/**
	 * Used by
	 * 	(1) TakeAction
	 */
	public class VMware {
		public static final String URL	= "https://10.0.5.10/sdk";
		public static final String USER	= "root";
		public static final String PASS = "kerber0s";
		
		public static final int	OP_REBOOT	= 1;
		public static final int	OP_TURNON	= 2;
		public static final int	OP_TURNOFF	= 3;
	}	
	
	/**
	 * Used by
	 * 	(1) WatsonImpl
	 */
	public class Assistant {
		public static final String WS_IT = "345f36e1-a825-48d4-8400-82f6da88424d";
		public static final String WS_EN = "c107f10b-d833-4157-84e1-13b8795ec444";
	
		public static final String BASE_URL	= "https://gateway.watsonplatform.net/assistant/api/v1/workspaces/";
		//public static final String WORKSPACE= WS_EN;
		
		public static final String USERNAME	= "fa11a158-365a-4740-8370-083de8b8bb9d";
		public static final String PASSWORD	= "7NWoJRdY1WT8";
		public static final String VERSION	= "2018-09-20";
	}
	
	/**
	 * Used by
	 * 	(1) MySqlImpl
	 */
	public class MySQL {
		public static final String HOST		= "VillaDB.olimpo.vsg";
		public static final String USER		= "root";
		public static final String PASS		= "kerber0s";
		
		public static final String DB_DAVID	= "david";
		public static final String DB_RHEA	= "rhea";
	}

	/**
	 * Used by
	 * 	(1) NiceSSH
	 */
	public class SSH {
		public static final String USER	= "root";
		public static final String PASS	= "kerber0s";
		public static final int    PORT	= 22;
	}

	/**
	 * Used by
	 * 	(1) MikrotikAsk
	 */
	public class Mik {
		public static final String ADDRESS	= "10.0.10.1";
		public static final String USERNAME	= "admin";
		public static final String PASSWORD	= "kerber0s";
		
		public static final String INTERFACE_PREFIX = "eth[0-9][0-9]?[ ]-[ ].*";
		public static final String PCQ_DOWN	= "PCQ_Download";
		public static final String PCQ_UP	= "PCQ_Upload";

		public static final String HIG_DOWN	= "10M";
		public static final String LOW_DOWN	= "4M";
		public static final String HIG_UP	= "2M";
		public static final String LOW_UP	= "512k";
	}

	/**
	 * Used by
	 * 	(1) PaperClient
	 */
	public class PaperCut {
		public static final String ADDRESS	= "http://papercut.salaolimpo.eu/rpc/api/xmlrpc";
		public static final String USERNAME	= "admin";
		public static final String PASSWORD	= "kerber0s";
	}
	
	/**
	 * Used by
	 * 	(1) CupsClient
	 */
	public class Cups {
		public static final String HOST		= "10.0.10.13";
		public static final int    PORT		= 631;
		public static final String USERNAME	= "root";
		public static final String PASSWORD	= "kerber0s";
	}

	/**
	 * Used by
	 * 	(1) LdapTool
	 */
	public class LDAP {
		public static final String HOST		= "VillaDC.olimpo.vsg";
		public static final String PORT		= "389";
		public static final String USERNAME	= "OLIMPO\\Administrator";
		public static final String PASSWORD	= "Kerber0s";
		
		public static final String BASE_OU		= "OU=Villici";
		public static final String BASE_DN		= "DC=olimpo,DC=vsg";

		public static final String FIELD_DN		= "dn";
		public static final String FIELD_USER	= "sAMAccountName";
		public static final String FIELD_CONT	= "useraccountcontrol";
		public static final String FIELD_ROOM	= "physicaldeliveryofficename";
	}
	
	/**
	 * Used by
	 * 	(1) MyHttpRequest
	 */
	public static class GraphList {
		public static Map<String, Graph> graphs;
	}
	
	/**
	 * Used by
	 * 	(1) MikrotikAsk
	 */
	public static class ChannelList {
		public static Map<String, String> channels;
		
		public static final String CH_1  = "2.4 - ch1";
		public static final String CH_6  = "2.4 - ch6";
		public static final String CH_11 = "2.4 - ch11";
	}
	
}
