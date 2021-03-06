package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class in the utility packgage is used to manage some plug-in scope objects that are used across
 * the whole Reactome FI plug-in for all features. Some of features in this class are refactored from the 
 * original PlugInScopeObjectManager. This is a singleton as with the original PlugInScopeObjectManager.
 * @author gwu
 *
 */
public class PlugInObjectManager {
    private final Logger logger = LoggerFactory.getLogger(PlugInObjectManager.class);
    // BundleContext for the whole Reactome FI plug-in.
    private BundleContext context;
    // System-wide properties
    private Properties properties;
    private static PlugInObjectManager manager = new PlugInObjectManager();
    // Record cached ServiceReference so that they can be unget when this bundle (aka) is stopped
    private List<ServiceReference> serviceReferences;

    // Cache the CySwingApplication to be used multiple times
    private CySwingApplication cyApplication;
    // Cache TaskManager since it will be used multiple times
    @SuppressWarnings("rawtypes")
    private TaskManager taskManager;
    // Currently selected FI network version
    private String fiNetworkVersion;
    
    /**
     * Default constructor. This is a private constructor so that the single instance should be used.
     */
    private PlugInObjectManager() {
        serviceReferences = new ArrayList<ServiceReference>();
    }
    
    public static PlugInObjectManager getManager() {
        return manager;
    }

    /**
     * Check if Reactome pathways are loaded.
     * @return
     */
    public boolean isPathwaysLoaded() {
        CySwingApplication app = getCySwingApplication();
        CytoPanel westPane = app.getCytoPanel(CytoPanelName.WEST);
        for (int i = 0; i < westPane.getCytoPanelComponentCount(); i++) {
            Component comp = westPane.getComponentAt(i);
            if (comp instanceof CytoPanelComponent) {
                String title = ((CytoPanelComponent)comp).getTitle();
                if (title.equals("Reactome"))
                    return true;
            }
        }
        return false;
    }
    
    public String getFiNetworkVersion()
    {
        if (this.fiNetworkVersion != null)
            return this.fiNetworkVersion;
        else
            return getDefaultFINeworkVersion();
    }

    /**
     * Get the default version of the FI network if it is set. Otherwise,
     * the first one listed will be returned.
     * @return
     */
    public String getDefaultFINeworkVersion() {
        Properties prop = PlugInObjectManager.getManager().getProperties();
        String fiVersions = prop.getProperty("FINetworkVersions");
        String[] tokens = fiVersions.split(",");
        for (String token : tokens)
        {
            token = token.trim();
            if (token.toLowerCase().contains("default")) return token;
        }
        // There is no default set. Choose the first one.
        return tokens[0];
    }
    
    /**
     * Get the latest version of the FI network listed in the configuration.
     * @return
     */
    public String getLatestFINetworkVersion() {
        Properties prop = PlugInObjectManager.getManager().getProperties();
        String fiVersions = prop.getProperty("FINetworkVersions");
        String[] tokens = fiVersions.split(",");
        Map<Integer, String> yearToVersion = new HashMap<Integer, String>();
        for (String token : tokens) {
            token = token.trim();
            int index = token.indexOf("(");
            if (index > 0)
                yearToVersion.put(new Integer(token.substring(0, index).trim()),
                                  token);
            else
                yearToVersion.put(new Integer(token), token);
        }
        List<Integer> years = new ArrayList<Integer>(yearToVersion.keySet());
        Collections.sort(years);
        return yearToVersion.get(years.get(years.size() - 1));
    }

    public void setFiNetworkVersion(String fiNetworkVersion) {
        this.fiNetworkVersion = fiNetworkVersion;
    }
    
    public void setBundleContext(final BundleContext context) {
        this.context = context;
        context.addBundleListener(new SynchronousBundleListener() {
            
            @Override
            public void bundleChanged(BundleEvent event) {
                if (event.getType() == BundleEvent.STOPPING) {
//                    System.out.println("Bundle is stopping!");
                    if (serviceReferences.size() > 0) {
                        for (ServiceReference reference : serviceReferences) {
                            if (reference != null)
                                context.ungetService(reference);
                        }
                    }
                }
            }
            
        });
    }
    
    public BundleContext getBundleContext() {
        return this.context;
    }
    
    /**
     * Get the system-wide TaskManager that is registered as a service
     */
    @SuppressWarnings("rawtypes")
    public TaskManager getTaskManager() {
        if (taskManager != null)
            return taskManager;
        ServiceReference ref = context.getServiceReference(TaskManager.class.getName());
        if (ref == null)
            return null;
        taskManager = (TaskManager) context.getService(ref);
        if (taskManager != null)
            serviceReferences.add(ref);
        return taskManager;
    }
    
    /**
     * Get the system-wide CySwingApplication that is registered as a service.
     * @return
     */
    public CySwingApplication getCySwingApplication() {
        if (cyApplication == null) {
            ServiceReference ref = context.getServiceReference(CySwingApplication.class.getName());
            if (ref == null)
                return null;
            cyApplication = (CySwingApplication) context.getService(ref);
            if (cyApplication != null)
                serviceReferences.add(ref);
        }
        return cyApplication;
    }
    
    public void selectCytoPane(Component panel, CytoPanelName direction) {
        CySwingApplication desktopApp = getCySwingApplication();
        CytoPanel tableBrowserPane = desktopApp.getCytoPanel(direction);
        int index = tableBrowserPane.indexOfComponent(panel);
        if (index >= 0)
            tableBrowserPane.setSelectedIndex(index);
    }
    
    /**
     * Get the JFrame used to hold the whole application.
     * @return
     */
    public JFrame getCytoscapeDesktop() {
        return getCySwingApplication().getJFrame();
    }
    
    /**
     * Get the preset properties.
     * @return
     */
    public Properties getProperties() {
        if (properties == null)
        {
            try
            {
                properties = new Properties();
                InputStream is = getClass().getResourceAsStream("Config.prop");
                properties.load(is);
            }
            catch (IOException e)
            {
                logger.error("Cannot find Config.prop: "
                        + e.getMessage(), e);
            }
        }
        return this.properties;
    }
    
    /**
     * Get the RESTful URL
     * 
     * @param fiVersion
     * @return
     */
    public String getRestfulURL(String fiVersion)
    {
        fiVersion = fiVersion.replaceAll(" ", "_");
        String key = fiVersion + "_restfulURL";
        Properties prop = getProperties();
        return prop.getProperty(key);
    }

    public String getRestfulURL() {
        return getRestfulURL(getFiNetworkVersion());
    }
    
    public String getHostURL() {
        String serviceUrl = PlugInObjectManager.getManager().getRestfulURL();
     // Get the host URL name
        int index = serviceUrl.lastIndexOf("/", serviceUrl.length() - 2);
        return serviceUrl.substring(0, index + 1);
    }
    
}
