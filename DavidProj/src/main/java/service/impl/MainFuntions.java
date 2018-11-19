package service.impl;

import static service.impl.WatsonImpl.getWatsonResponse;
import static service.utils.JsonUtils.buildAlexaResponse;
import static service.utils.JsonUtils.buildWatsonData;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import service.magic.MySqlImpl;
import service.utils.Constants;
import service.utils.Constants.General;
import service.utils.Constants.Language;
import service.utils.Constants.MySQL;

/**
 * Class containing the main used functions
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MainFuntions {
	
	private static final boolean LOG = false;

	private static Map<String, String> alexaContext = new HashMap<>();

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


		if(LOG) {
			MySqlImpl link = new MySqlImpl(MySQL.DB_DAVID);
			link.logQuery(requestText, General.ALEXA);
			link.closeDB();
		}
		
		//=== Retrieving session ID ===
		String sessID	= args.getJSONObject("session").getString("sessionId");


		//=== If LaunchRequest, it doesn't contain intents ===
		if (args.getJSONObject("request").getString("type").equals("LaunchRequest")) {
			watsonBody = buildWatsonData("");
			
			//--- Fire the Watson call for this specific case ---
			JSONObject	watsonResponse	= getWatsonResponse(watsonBody, lang);
			String		textResponse	= watsonResponse.getJSONObject("output").getJSONArray("text").getString(0);
			
			alexaContext.put(sessID, watsonResponse.getJSONObject("context").toString());
			return buildAlexaResponse(textResponse, false) .toString();

		//=== If Timeout, close david ===
		} else if (args.getJSONObject("request").getString("type").equals("SessionEndedRequest")) {
			alexaContext.clear();

			return buildAlexaResponse("", true) .toString();

		//=== If StopIntent, close david ===
		} else if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.StopIntent")) {
				alexaContext.clear();
	
				switch(lang) {
					case Constants.Language.ITALIAN:
						return buildAlexaResponse("Chiusura di David", true).toString();
					default:
						return buildAlexaResponse("Closing david", true).toString();
				}

		
		} 
	
		//=== If HelpIntent, handle with Assistant ===
		if (args.getJSONObject("request").getJSONObject("intent").getString("name").equals("AMAZON.HelpIntent")) {
			switch(lang) {
				case Constants.Language.ITALIAN:
					requestText = "Guidami";
					break;
				default:
					requestText = "Help";
			}
		} else {
			requestText = args.getJSONObject("request").getJSONObject("intent")
							.getJSONObject("slots").getJSONObject("EveryThingSlot").getString("value");
		}
	
	//-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-
	
		//=== Retrieving the contect and logging the query ===
		String watsonID	= alexaContext.getOrDefault(sessID, "{}");
		
	
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
		alexaContext.put(sessID, watsonResponse.getJSONObject("context").toString());
	
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

//==========================================================================================================

	/**
	 * Method called for the Telegram service
	 * 
	 */
	public static String runTelegram(String payload) throws Exception {
		JSONObject args		= new JSONObject(payload);
		String requestText	= args.getJSONObject("input").getString("text");

		int lang			= (args.getString("lang").equals("en") ?
				Language.ENGLISH :  Language.ITALIAN);


		//=== Logging query on Database ===
		if(LOG) {
			MySqlImpl link	= new MySqlImpl(MySQL.DB_DAVID);
			link.logQuery(requestText.replace("'", ""), General.TELEGRAM);
			link.closeDB();
		}


		//=== Retrieving Watson Text ===
		String textResponse;
		if(args.getJSONObject("output").getJSONArray("text").length() > 0)
			textResponse = args.getJSONObject("output").getJSONArray("text").getString(0);
		else
			textResponse = (lang == Language.ENGLISH ? "No response for this action" : "Nessuna risposta per questa azione");


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

	public static String runGoogle(String payload) throws Exception {
		MySqlImpl link = new MySqlImpl(MySQL.DB_DAVID);
		link.logQuery(payload, General.ALEXA);
		link.closeDB();
		return null;
	}
}
