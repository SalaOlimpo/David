package service.magic;

import java.util.List;

import de.spqrinfo.cups4j.CupsClient;
import de.spqrinfo.cups4j.CupsPrinter;
import de.spqrinfo.cups4j.PrintJobAttributes;
import de.spqrinfo.cups4j.WhichJobsEnum;
import service.utils.Constants;

/**
 * Class used to wrap calls to CUPS service
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class MyCupsClient {

	private static CupsClient			cups	 = null;
	private static List<CupsPrinter>	printers = null;
	
	static {
		try {
			cups = new CupsClient(Constants.Cups.HOST, Constants.Cups.PORT, "root");
			printers = cups.getPrinters();
		} catch (Exception e) {
			// who cares?
		}
	}
	
	public static String cancelJobs(int lang) throws Exception {
		String res;
		switch(lang) {
			case Constants.Language.ITALIAN:
				res = "Nessun job in corso";
				break;
			default:
				res = "No running job";
		}

		for(CupsPrinter printer : printers)
			for(PrintJobAttributes job : printer.getJobs(WhichJobsEnum.NOT_COMPLETED, "root", false)) {
				cups.cancelJob(job.getJobID());
				switch(lang) {
					case Constants.Language.ITALIAN:
						res = "Ok! Jobs cancellati";
						break;
					default:
						res = "Ok! Jobs deleted";
				}
			}

		return res;
	}
}
