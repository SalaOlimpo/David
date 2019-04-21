package service.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to format JSON for Watson and Alexa methods.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class JsonUtils {
	
	/**
	 * Creates response for alexa.
	 * 
	 * @param text			The output for alexa
	 * @param endSession	Set false to make alexa wait for next input
	 */
	public static JSONObject buildAlexaResponse(String text, boolean endSession) throws JSONException {
		JSONObject speech = new JSONObject();
		speech.put("type",	"PlainText");
		speech.put("text",	text);
		
		JSONObject response = new JSONObject();
		response.put("shouldEndSession",	endSession);
		response.put("outputSpeech",		speech);
		
		JSONObject jo = new JSONObject();
		jo.put("version",	"1.0");
		jo.put("response",	response);
		
		return jo;
	}
	
// -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-
	/**
	 * Creates input for Watson
	 * 
	 * @param text	The input for Watson
	 */
	public static JSONObject buildWatsonData(String text) throws JSONException {
		JSONObject input = new JSONObject();
		input.put("text", text);

		JSONObject jo = new JSONObject();
		jo.put("input", input);
		
		return jo;
	}

// -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-
	/**
	 * Creates input for Watson with Context (to keep history)
	 * 
	 * @param text		The input for Watson
	 * @param context	The JSONObject of the context
	 */
	public static JSONObject buildWatsonData(String text, String context) throws JSONException {
		JSONObject jo = buildWatsonData(text);
		jo.put("context", new JSONObject(context));
		
		return jo;
	}
}
