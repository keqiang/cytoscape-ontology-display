package edu.umich.med.mbni.lkq.cyontology.service;

import java.io.IOException;
import java.util.Set;

/**
 * An interface that is used to do FI related activities.
 * @author gwu
 *
 */
public interface FINetworkService
{

    public Integer getNetworkBuildSizeCutoff() throws Exception;

    public Set<String> buildFINetwork(Set<String> selectedGenes,
            boolean useLinkers) throws Exception;

    public Set<String> queryAllFIs() throws IOException;
}
