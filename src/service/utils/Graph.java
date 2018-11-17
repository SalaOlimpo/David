package service.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent the relationship between graph, graphID and dashboards
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
public class Graph {
	public String		dashboard;
	public String		dashId;
	public List<String>	panelId;
	
	public Graph(String dashboard, String dashId) {
		this.dashboard	= dashboard;
		this.dashId		= dashId;
		this.panelId	= new ArrayList<>();
	}
}
