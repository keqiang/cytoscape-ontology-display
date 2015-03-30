package edu.umich.med.mbni.lkq.cyontology.service;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import edu.umich.med.mbni.lkq.cyontology.util.PlugInObjectManager;
import edu.umich.med.mbni.lkq.cyontology.util.PlugInUtilities;

/**
 * This method is used as a mediator to call Reactome RESTful API.
 * @author gwu
 *
 */
public class ReactomeRESTfulService {
    private String restfulAPIUrl;
    private static ReactomeRESTfulService service = new ReactomeRESTfulService();
    
    /**
     * Default constructor
     */
    private ReactomeRESTfulService() {
        restfulAPIUrl = PlugInObjectManager.getManager().getProperties().getProperty("ReactomeRESTfulAPI");
    }
    
    public static ReactomeRESTfulService getService() {
        return service;
    }
    
    /**
     * Get the FrontPageItems for human pathways.
     * @return
     */
    public Element frontPageItems() throws Exception {
        String url = restfulAPIUrl + "frontPageItems/Homo+sapiens";
        Element root = PlugInUtilities.callHttpInXML(url,
                                                     PlugInUtilities.HTTP_GET,
                                                     null);
        return root;
    }
    
    public String pathwayHierarchy() throws Exception {
        String url = restfulAPIUrl + "pathwayHierarchy/Homo+sapiens";
        String text = PlugInUtilities.callHttpInText(url, PlugInUtilities.HTTP_GET, null);
        return text;
    }
    
    /**
     * Get the PathwayDiagram in XML for a pathway specified by its DB_ID.
     */
    public String pathwayDiagram(Long pathwayId) throws Exception {
        String url = restfulAPIUrl + "pathwayDiagram/" + pathwayId + "/xml";
        String text = PlugInUtilities.callHttpInText(url, PlugInUtilities.HTTP_GET, "");
        return text;
    }
    
    /**
     * Get contained event ids for a pathway specified by its DB_ID.
     * @param pathwayId
     * @return
     * @throws Exception
     */
    public List<Long> getContainedEventIds(Long pathwayId) throws Exception {
        String url = restfulAPIUrl + "getContainedEventIds/" + pathwayId;
        String text = PlugInUtilities.callHttpInText(url, PlugInUtilities.HTTP_GET, "");
        String[] tokens = text.split(",");
        List<Long> rtn = new ArrayList<Long>();
        for (String token : tokens)
            rtn.add(new Long(token));
        return rtn;
    }
    
    /**
     * Query an instance based on its DB_ID and ClassName.
     * @param id
     * @param clsName
     * @return
     * @throws Exception
     */
    public Element queryById(Long id, String clsName) throws Exception {
        String url = restfulAPIUrl + "queryById/" + clsName + "/" + id;
        Element root = PlugInUtilities.callHttpInXML(url, 
                                                     PlugInUtilities.HTTP_GET, 
                                                     null);
        return root;
    }
}

