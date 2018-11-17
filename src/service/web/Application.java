package service.web;

import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import service.utils.Constants.GraphList;
import service.utils.Graph;

/**
 * Class auto-generated by Spring.
 * Please don't touch.
 * 
 * @author	SalaOlimpo
 * @since	24.08.2018
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    private static Class<Application> applicationClass = Application.class;
	
    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }
    
    
    // Initializer
    static {
    	GraphList.graphs = new HashMap<>();
    	
		Graph g;
		g = new Graph("network-backbone", "Y2wHZfFmz");
		g.panelId.add("18"); g.panelId.add("20"); 
		GraphList.graphs.put("Isp", g);
		
		g = new Graph("network-backbone", "Y2wHZfFmz");
		g.panelId.add("6"); g.panelId.add("8"); 
		GraphList.graphs.put("Vlan load", g);
		
		g = new Graph("network-backbone", "Y2wHZfFmz");
		g.panelId.add("10"); g.panelId.add("12"); 
		GraphList.graphs.put("Switch load", g);
		
		g = new Graph("network-backbone", "Y2wHZfFmz");
		g.panelId.add("14"); g.panelId.add("16"); 
		GraphList.graphs.put("Net Resources", g);
		
	// -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.
		
		g = new Graph("system-health", "YRMRY7Omk");
		g.panelId.add("2");
		GraphList.graphs.put("Temperature", g);
		
		g = new Graph("system-health", "YRMRY7Omk");
		g.panelId.add("12"); g.panelId.add("14"); 
		GraphList.graphs.put("System resources", g);
		
		g = new Graph("system-health", "YRMRY7Omk");
		g.panelId.add("18");
		GraphList.graphs.put("Mikrotik load", g);

	// -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.
    }
}