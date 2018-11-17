package service.impl;

import static service.impl.WatsonImpl.getWatsonResponse;
import static service.utils.JsonUtils.buildAlexaResponse;
import static service.utils.JsonUtils.buildWatsonData;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import service.magic.MySqlImpl;
import service.utils.Constants;
import service.utils.Constants.General;
import service.utils.Constants.MySQL;

/**
 * Class containing the main used functions
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MainFuntions {
	
	public static String runGTT(String payload) throws Exception {
		JSONObject	args		= new JSONObject(payload);

		
		//=== If LaunchRequest, it doesn't contain intents ===
		if (args.getJSONObject("request").getString("type").equals("LaunchRequest"))
			return buildAlexaResponse("Ciao, dimmi che pullman e a che fermata", false) .toString();

		//=== If StopIntent, close david ===
		else if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.StopIntent"))
			return buildAlexaResponse("Grazie per avermi usato!", true).toString();

		//=== If HelpIntent, handle with Assistant ===
		if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.HelpIntent"))
			return buildAlexaResponse("Ciao, dimmi che pullman e a che fermata", false) .toString();
		else {
			String fermata	= args.getJSONObject("request").getJSONObject("intent")
					.getJSONObject("slots").getJSONObject("Fermata").getString("value");
			String linea	= args.getJSONObject("request").getJSONObject("intent")
					.getJSONObject("slots").getJSONObject("Linea").getString("value");
			
			
			URL url = new URL("https://gpa.madbob.org/query.php?stop=" + fermata);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			String res = IOUtils.toString(conn.getInputStream());
			JSONArray arr = new JSONArray(res);
			
			for(int i=0; i<arr.length(); i++) {
				JSONObject item = arr.getJSONObject(i);
				if(item.getString("line").equals(linea))
					return buildAlexaResponse("Il prossimo " + linea + " passerà alle " + item.getString("hour"), false) .toString();
			}
			return buildAlexaResponse("Non trovo il pullman richiesto in quella fermata", false) .toString();
		}
	}
//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-		

/**
 * Method called for the Alexa service
 * 
 */
public static String runAlexa(String payload) throws Exception {
	JSONObject	args		= new JSONObject(payload);
	JSONObject	watsonBody	= null;
	String		requestText;
	int			lang;
	
	if(args.getJSONObject("request").getString("locale").startsWith("it"))
		lang = Constants.Language.ITALIAN;
	else
		lang = Constants.Language.ENGLISH;
	
	//=== Retrieving session ID ===
	MySqlImpl link	= new MySqlImpl(MySQL.DB_DAVID);
	String sessID	= args.getJSONObject("session").getString("sessionId");

	//=== If LaunchRequest, it doesn't contain intents ===
	if (args.getJSONObject("request").getString("type").equals("LaunchRequest")) {
		watsonBody = buildWatsonData("");
		
		//--- Fire the Watson call for this specific case ---
		JSONObject	watsonResponse	= getWatsonResponse(watsonBody, lang);
		String		textResponse	= watsonResponse.getJSONObject("output").getJSONArray("text").getString(0);
		
		link.setContext(sessID, watsonResponse.getJSONObject("context").toString(), General.ALEXA);
		link.closeDB();
		return buildAlexaResponse(textResponse, false) .toString();


	//=== If StopIntent, close david ===
	} else if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.StopIntent")) {
			link.delAlexaContext();
			link.closeDB();
			switch(lang) {
				case Constants.Language.ITALIAN:
					return buildAlexaResponse("Chiusura di David", true).toString();
				default:
					return buildAlexaResponse("Closing david", true).toString();
			}
	}

	//=== If HelpIntent, handle with Assistant ===
	if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.HelpIntent")) {
		requestText = "Help";
	} else {
		requestText = args.getJSONObject("request").getJSONObject("intent")
						.getJSONObject("slots").getJSONObject("EveryThingSlot").getString("value");
	}

//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-

	//=== Retrieving the contect and logging the query ===
	String watsonID	= link.getContext(sessID, General.ALEXA);
	link.logQuery(requestText, General.ALEXA);


	//=== Building the body for Watson call ===
	if(watsonID == null)
		watsonBody = buildWatsonData(requestText);
	else
		watsonBody = buildWatsonData(requestText, watsonID);

	
	//=== Fire the Watson call ===
	JSONObject	watsonResponse = getWatsonResponse(watsonBody, lang);
	String		textResponse;

	if(watsonResponse.getJSONObject("output").getJSONArray("text").length() > 0)
		textResponse = watsonResponse.getJSONObject("output").getJSONArray("text").getString(0);
	else {
		switch(lang) {
			case Constants.Language.ITALIAN:
				textResponse = "Nessuna risposta per questa azione";
				break;
			default:
				textResponse = "No response for this action";
		}
	}
	link.setContext(sessID, watsonResponse.getJSONObject("context").toString(), General.ALEXA);

	//=== Execute the relative action ===
	try {
		String actionResponse = null;
		if(watsonResponse.getJSONArray("intents").length() > 0)
			actionResponse = TakeAction.pickUp (
				watsonResponse.getJSONArray("intents").getJSONObject(0).getString("intent"),
				watsonResponse.getJSONObject("context"),
				lang
			);
		if(actionResponse != null) textResponse = actionResponse;
		
		if(watsonResponse.getJSONObject("output").has("completedIntent"))
			actionResponse = TakeAction.pickCompleted(
				watsonResponse.getJSONObject("output").getString("completedIntent"),
				watsonResponse.getJSONObject("context"),
				lang
			);
		if(actionResponse != null) textResponse = actionResponse;

	} catch (Exception ex) {
		return buildAlexaResponse(ex.getMessage(), true) .toString();
	}

	return buildAlexaResponse(textResponse, false) .toString();
}
	
//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-

	/**
	 * Method called for the Telegram service
	 * 
	 */
	public static String runTelegram(String payload) throws Exception {
		JSONObject args		= new JSONObject(payload);
		String requestText	= args.getJSONObject("input").getString("text");
		int lang			= Constants.Language.ENGLISH;
		
		//=== Logging query on Database ===
		MySqlImpl link	= new MySqlImpl(MySQL.DB_DAVID);
		link.logQuery(requestText.replace("'", ""), General.TELEGRAM);
		link.closeDB();

		//=== Retrieving Watson Text ===
		String textResponse;
		if(args.getJSONObject("output").getJSONArray("text").length() > 0)
			textResponse = args.getJSONObject("output").getJSONArray("text").getString(0);
		else
			textResponse = "No response for this action";
		
		//=== Execute the relative action ===
		try {
			String actionResponse = null;
			if(args.getJSONArray("intents").length() > 0)
				actionResponse = TakeAction.pickUp (
					args.getJSONArray("intents").getJSONObject(0).getString("intent"),
					args.getJSONObject("context"),
					lang
				);
			if(actionResponse != null) textResponse = actionResponse;
			
			if(args.getJSONObject("output").has("completedIntent"))
				actionResponse = TakeAction.pickCompleted(
					args.getJSONObject("output").getString("completedIntent"),
					args.getJSONObject("context"),
					lang
				);
			if(actionResponse != null) textResponse = actionResponse;
	
		} catch (Exception ex) {
			return args.put("content", ex.getMessage()).toString();
		}

		return args.put("content", textResponse).toString();
	}
}
