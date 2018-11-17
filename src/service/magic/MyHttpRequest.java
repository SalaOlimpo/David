package service.magic;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import service.utils.Constants;
import service.utils.Graph;

/**
 * Class used to wrap HTTP calls
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MyHttpRequest {

	/**
	 * Get the UP status using the exposed API
	 * @param lang 
	 * @return
	 * @throws Exception
	 */
	public static String getUpsStatus(int lang) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) (new URL("http://10.0.10.18")).openConnection();
		conn.setRequestMethod("GET");
		conn.getResponseCode();

		String res = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);


		JSONObject jo = new JSONObject(res);
		String charge = "Battery charge: " + jo.getString("battery.charge") + "%";
		String status = jo.getString("ups.status");
				
		if(status.equals("OL")) {
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "UPS online. Non manca la corrente. " + charge; 
				default:
					return "The UPS is on line. No power outage. " + charge; 
			}
		}

		if(status.equals("OB")) {
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "L'UPS è in batteria!. è mancata la corrente. " + charge; 
				default:
					return "The UPS on battery!. A power outage occurred. " + charge; 
			}
		}

		if(status.equals("LB")) {
			switch(lang) {
				case Constants.Language.ITALIAN:
					return "Attenzione! Senza corrente! Poca batteria!" + charge; 
				default:
					return "Warning! Power outage! Low Battery!" + charge;  
			}
		}
		switch(lang) {
			case Constants.Language.ITALIAN:
				return "Stato sconosciuto";
			default:
				return "Unknown status";
		}
	}

	public static String downloadGraphs(Graph graph) {
		String res	= "";
		
		long to		= System.currentTimeMillis();
		long from	= to - 12*3600*1000;
		int i		= 0;
		
		for(String s : graph.panelId) {	
			try {
				URL obj = new URL("http://graph.salaolimpo.eu/render/d-solo/" + graph.dashId + "/" + graph.dashboard +
						"?" + "orgId=1&panelId=" + s + "&from=" + from + "&to=" + to + "&width=1000&height=500&tz=Europe%2FRome");

				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestProperty("Authorization", "Basic YWRtaW46a2VyYmVyMHM=");
				con.setRequestMethod("GET");
				con.setDoOutput(true);

				String filepath = "/srv/imgDavid/" + to + "_" + i++ + ".png";
				Files.copy(con.getInputStream(), Paths.get(filepath));
				
				File file = new File(filepath);
				file.setReadable(true, false);
				file.setExecutable(true, false);
				file.setWritable(true, false);
				
				res += filepath + ";;; ";
			} catch (Exception e) {
				// who cares?
			}
		}
		return res;
	}
}
