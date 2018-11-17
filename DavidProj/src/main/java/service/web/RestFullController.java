package service.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import service.impl.MainFuntions;

/**
 * Class used to map request methods.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
@RestController
public class RestFullController {
	
	/**
	 * Use this for test only
	 */
	@RequestMapping (value = "/test", method = {RequestMethod.GET})
    public String test() throws Exception {
		return "Hello world!";
	}

	/**
	 * Main methods called by alexa
	 * @param payload		Alexa post message
	 * @return result		The application result
	 * @throws Exception
	 */
	@RequestMapping (value = "/alexa", method = {RequestMethod.POST})
    public String runAlexa(@RequestBody String payload) throws Exception {
		return MainFuntions.runAlexa(payload);
    }

	/**
	 * Main methods called by telegram
	 * @param payload		Telegram post message
	 * @return result		The application result
	 * @throws Exception
	 */
	@RequestMapping (value = "/telegram", method = {RequestMethod.POST})
    public String runTelegram(@RequestBody String payload) throws Exception {
		return MainFuntions.runTelegram(payload);
	}
}