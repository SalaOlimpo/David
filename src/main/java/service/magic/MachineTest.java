package service.magic;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import service.utils.Constants;

/**
 * Class used to wrap test to Virtual Machines
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MachineTest {

	public static final Map<String, String> allVM;
	static {
		allVM = new HashMap<>();
		allVM.put("VillaDC",		"10.0.10.10");
		allVM.put("VillaDNS",		"10.0.10.11");
		allVM.put("VillaRadius", 	"10.0.10.12");
		allVM.put("PaperCut",		"10.0.10.13");
		allVM.put("VillaDB",		"10.0.10.14");
		allVM.put("CloudMail",		"10.0.10.15");
		allVM.put("VillaProxy",		"10.0.10.16");
		allVM.put("Rhea",			"10.0.10.17");
		allVM.put("RielloBox",		"10.0.10.18");
		allVM.put("VillaWeb",		"10.0.10.19");
		allVM.put("VillaSambino",	"10.0.10.20");
	} 


	/**
	 * Test if a single machine is reachable
	 * 
	 * @param machine
	 */
	public static boolean isReachable(String machine) throws IOException {
		 InetAddress address = InetAddress.getByName(allVM.get(machine));
         return address.isReachable(500);
	}


	/**
	 * Test if all machines are reachable
	 * @param lang 
	 * 
	 * @param machine
	 */
	public static String areReachable(int lang) throws IOException {
		boolean allReachable = true;
		String	unreachable;
		switch(lang) {
			case Constants.Language.ITALIAN:
				unreachable = "Macchine non raggiungibili: ";
				break;
			default:
				unreachable = "Unreachable machines: ";
		}
		
		for(String item : allVM.keySet())
			if(!item.equals("VillaDC") && !isReachable(item)) {
				allReachable = false;
				unreachable += item + ", ";
			}
		
		if(allReachable) {
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "Tutte le macchine sono raggiungibili.";
				default:
					return "All machines are reachable.";
			}
		}
		else return unreachable;
	}
}
