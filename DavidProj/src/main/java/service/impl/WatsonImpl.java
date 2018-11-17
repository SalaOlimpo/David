package service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import service.utils.Constants;
import service.utils.Constants.Assistant;

/**
 * Class used by Watson services
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class WatsonImpl {

	/**
	 * Fires Watson call
	 * 
	 * @param joWatson	the JSONObject to send to Watson
	 */
	public static JSONObject getWatsonResponse(JSONObject joWatson, int lang) throws Exception {
		HttpsURLConnection conn = (HttpsURLConnection) getUrl(lang).openConnection();
		String encoded = Base64.getEncoder().encodeToString(
				(Assistant.USERNAME + ":" + Assistant.PASSWORD).getBytes(StandardCharsets.UTF_8)
		);
		conn.setRequestProperty("Authorization", "Basic " + encoded);
		conn.setRequestProperty("Content-Type",	 "application/json");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		IOUtils.write(joWatson.toString(), conn.getOutputStream(), StandardCharsets.UTF_8);

		return new JSONObject(
			IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8)
		);
	}

	/**
	 * Calculates Watson URL
	 * 
	 */
	private static URL getUrl(int lang) throws MalformedURLException {
		String url = Assistant.BASE_URL;
		switch(lang) {
			case Constants.Language.ITALIAN:
				url += Constants.Assistant.WS_IT;
				break;
			default:
				url += Constants.Assistant.WS_EN;
		}
		return new URL(url + "/message?version=" + Assistant.VERSION);
	}
}
