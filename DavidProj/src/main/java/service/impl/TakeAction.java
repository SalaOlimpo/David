package service.impl;

import org.json.JSONObject;

import service.magic.LdapTool;
import service.magic.MachineTest;
import service.magic.MikrotikAsk;
import service.magic.MyCupsClient;
import service.magic.MyHttpRequest;
import service.magic.MySqlImpl;
import service.magic.NiceSSH;
import service.magic.PaperClient;
import service.utils.Constants;
import service.utils.Constants.GraphList;
import service.utils.Graph;

/**
 * Main class used to decide the action to take.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class TakeAction {

	/**
	 * Main program method.
	 * It performs a simple choose based on the intent returned.
	 * @param intent	The first intent only
	 * @param response	The entire response
	 */
	public static String pickUp(String intent, JSONObject context, int lang) throws Exception {
		if(intent.equals("CheckMachine")) {
			if(context.has("VirtualMachine")) {
				String vm = context.getString("VirtualMachine");
				clearContext(context);
				
				switch(lang) {
					case Constants.Language.ITALIAN:
						return vm + (MachineTest.isReachable(vm) ?
								" è funzionante!" : " sembra avere un problema"
							);
					default:
						return vm + (
								MachineTest.isReachable(vm) ?
								" is up and healty!" : " seems to have a problem"
							);
				}
			}
			return MachineTest.areReachable(lang);
		}
		if(intent.equals("SetupChannels"))
			return MikrotikAsk.adjustChannels(lang);

		if(intent.equals("NetworkBalance"))
			return MikrotikAsk.getLoadTotal();

		if(intent.equals("PeopleConnected"))
			return MikrotikAsk.getNumberOfUsers(lang);

		if(intent.equals("RebootMikrotik"))
			return MikrotikAsk.rebootMik(lang);

		if(intent.equals("DropLeases"))
			return MikrotikAsk.dropLeases(lang);
	
		if(intent.equals("ClearHistory")) {
			MySqlImpl mysql = new MySqlImpl(Constants.MySQL.DB_DAVID);
			mysql.clearQuery();
			mysql.closeDB();
			
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "Cronologia cancellata";
				default:
					return "History cleared";
			}
		}
		if(intent.equals("TurnDownQueue"))
			return MikrotikAsk.queueLimit(true, lang);

		if(intent.equals("TurnUpQueue"))
			return MikrotikAsk.queueLimit(false, lang);

		if(intent.equals("UPSOnline"))
			return MyHttpRequest.getUpsStatus(lang);
		
		if(intent.equals("StopPrinter"))
			return MyCupsClient.cancelJobs(lang);

		if(intent.equals("RenewCertbot")) {
			NiceSSH ssh = new NiceSSH("10.0.10.16");
				ssh.execCmd(Constants.General.CERTBOT, true);
				ssh.execCmd(Constants.General.REBOOT, false);
			ssh.closeSSH();
			
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "Certificati rinnovati";
				default:
					return "Certificates Renewed!";
			}
		}
		
		return null;
	}

	/**
	 * Second main program method.
	 * It performs a simple choose based on the intent suspended and now concluded.
	 * @param intent	The concluded intent
	 * @param response	The entire response
	 */
	public static String pickCompleted(String intent, JSONObject context, int lang) throws Exception {
		if(intent.equals("ChargeEuro") || intent.equals("DetractEuro")) {
			String userRoom	= context.getString("roomNumber");
			Double credit	= context.getDouble("credit");

			LdapTool ldap	= new LdapTool();
			String ldapUser = ldap.findUserByRoom(userRoom);
			if(ldapUser == null) {
				switch(lang) {
					case Constants.Language.ITALIAN:
						return "Nessun utente: " + userRoom;
					default:
						return "No such user: " + userRoom;
				}
			}
			ldap.Close();

			clearContext(context);
			return PaperClient.adjustCredit(ldapUser, (intent.equals("ChargeEuro") ? credit : -credit), lang);
		}
		if(intent.equals("EnableUser") || intent.equals("DisableUser")) {
			String userRoom	= context.getString("roomNumber");

			LdapTool ldap	= new LdapTool();
			String ldapUser = ldap.findUserByRoom(userRoom);
			if(ldapUser == null) {
				switch(lang) {
					case Constants.Language.ITALIAN:
						return "Nessun utente: " + userRoom;
					default:
						return "No such user: " + userRoom;
				}
			}
			
			String res = ldap.toggleUser(userRoom, ldapUser, intent.equals("EnableUser"), lang);
			ldap.Close();

			clearContext(context);
			return res;
		}
		if(intent.equals("DisplayGraph")) {
			Graph	graph	= GraphList.graphs.get(context.getString("Graph"));
			String	res		= MyHttpRequest.downloadGraphs(graph);
								
			clearContext(context);
			return res;
		}
		return null;
	}
	

	/**
	 * Function used to clear the context
	 *
	 * @param context The actual context
	 */
	public static void clearContext(JSONObject context) {
		context.remove("VirtualMachine");
		context.remove("roomNumber");
		context.remove("credit");
	}
}
