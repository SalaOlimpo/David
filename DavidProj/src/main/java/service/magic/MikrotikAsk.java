package service.magic;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.ApiConnectionException;
import me.legrange.mikrotik.MikrotikApiException;
import service.utils.Constants;
import service.utils.Constants.Mik;

/**
 * Class used to wrap Sir Mikrotik call
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MikrotikAsk {

	private ApiConnection mik;
		
	public MikrotikAsk() throws MikrotikApiException {
		mik = ApiConnection.connect(Mik.ADDRESS);
		mik.login(Mik.USERNAME, Mik.PASSWORD);
	}
	public List<Map<String, String>> execCmd(String cmd) throws MikrotikApiException {
		return mik.execute(cmd);
	}
	public void closeMik() throws ApiConnectionException {
		mik.close();
	}


	/**
	 * This method returns an average over 5 seconds of the network balance
	 * @param lang 
	 *
	 */
	public static String getLoadTotal() throws Exception {
		MikrotikAsk mik = new MikrotikAsk();
		Map<String, Long> rx = new HashMap<>();
		Map<String, Long> tx = new HashMap<>();		

	//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-
		for(int i=0; i<2; i++) {
			List<Map<String, String>> res = mik.execCmd("/interface/print");

			for(Map<String, String> item : res)
				if(item.get("name").matches(Mik.INTERFACE_PREFIX)) {
					String name = item.get("name").substring(7);

					rx.put(name, new Long(item.get("rx-byte")) -rx.getOrDefault(name, 0L));
					tx.put(name, new Long(item.get("tx-byte")) -tx.getOrDefault(name, 0L));
				}
			if(i == 0) Thread.sleep(5000);
		}
		mik.closeMik();
	//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-
		
		String str = "";
		for(String key : rx.keySet())
			str += key + ": "
					+ String.format(Locale.US, "%.1f", new Double(rx.get(key)) /655360.0) + " / "
					+ String.format(Locale.US, "%.1f", new Double(tx.get(key)) /655360.0) + " Mbps. ";
		return str;
	}


	/**
	 * This method counts the number of user connected with radius
	 * @param lang 
	 *
	 */
	public static String getNumberOfUsers(int lang) throws MikrotikApiException {	
		MikrotikAsk mik = new MikrotikAsk();
			List<Map<String, String>> res = mik.execCmd("/caps-man/registration-table/print");
		mik.closeMik();

		switch(lang) {
			case Constants.Language.ITALIAN:
				return "Ci sono " + (new Integer(res.size())).toString() + " utenti online!";
			default:
				return "There are " + (new Integer(res.size())).toString() + " online users!";
		}
	}


	/**
	 * This method simply reboots the MikroTik
	 * @param lang 
	 * 
	 */
	public static String rebootMik(int lang) throws MikrotikApiException {	
		MikrotikAsk mik = new MikrotikAsk();
			mik.execCmd("/system/reboot");
		mik.closeMik();

		switch(lang) {
			case Constants.Language.ITALIAN:
				return "OK! Sto riavviando il Mikrotik...";
			default:
				return "OK! Rebooting the MikroTik...";
		}
		
	}


	/**
	 * This method drops all the leases
	 * @param lang 
	 */
	public static String dropLeases(int lang) throws MikrotikApiException {
		MikrotikAsk mik = new MikrotikAsk();
		List<Map<String, String>> res = mik.execCmd("/ip/dhcp-server/lease/print");
		
		for(Map<String, String> mappa : res)
			if(mappa.get("dynamic").equals("true"))
				mik.execCmd("/ip/dhcp-server/lease/remove .id=" + mappa.get(".id"));

		mik.closeMik();
		switch(lang) {
			case Constants.Language.ITALIAN:
				return "OK! Leases cancellati";
			default:
				return "OK! Leases dropped";
		}
	}
	
	
	/**
	 * This method changes the traffic limit of users
	 * @param down	If true the queues are reduced, otherwise they are increased
	 * @param lang 
	 */
	public static String queueLimit(boolean down, int lang) throws MikrotikApiException {
		MikrotikAsk mik = new MikrotikAsk();
		if(down) {
			mik.execCmd("/queue/type/set .id=" + Mik.PCQ_DOWN	+ " pcq-rate=" + Mik.LOW_DOWN);
			mik.execCmd("/queue/type/set .id=" + Mik.PCQ_UP		+ " pcq-rate=" + Mik.LOW_UP	 );
		} else {
			mik.execCmd("/queue/type/set .id=" + Mik.PCQ_DOWN	+ " pcq-rate=" + Mik.HIG_DOWN);
			mik.execCmd("/queue/type/set .id=" + Mik.PCQ_UP		+ " pcq-rate=" + Mik.HIG_UP	 );
		}

		mik.closeMik();
		switch(lang) {
			case Constants.Language.ITALIAN:
				return "OK! Code modificate";
			default:
				return "OK! Queues edited";
		}
	}
	
	/**
	 * This method changes the traffic limit of users
	 * @param down	If true the queues are reduced, otherwise they are increased
	 * @param lang 
	 */
	public static String adjustChannels(int lang) throws MikrotikApiException {
		MikrotikAsk mik = new MikrotikAsk();
		List<Map<String, String>> res = mik.execCmd("/caps-man/interface/print");
		
		for(Map<String, String> mappa : res) {
			//System.out.println(mappa.get("name") + " - " + mappa.get(".id"));
			//caps-man interface set channel="2.4 - ch6"
			//if(mappa.get("dynamic").equals("true"))
			mik.execCmd("/caps-man/interface/set channel=\"2.4 - ch11\" .id=" + mappa.get(".id"));
			break;
		}
		mik.closeMik();
		switch(lang) {
			case Constants.Language.ITALIAN:
				return "Canali correttamente settati";
			default:
				return "All channels are now set";
		}
	}
}
