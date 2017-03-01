package edu.umich.med.mbni.lkq.cyontology.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeneOntologyIdToSymbolMapping {

	private static final String GENE_TO_GO = "gene2go_out_trim";
	private static final String GENE_TO_SYMBOL = "gene_info_out_trim";

	private static Map<String, Set<Long>> goIdToGeneId;
	private static Map<Long, String> geneIdToSymbol;

	private static void initialize() {
		goIdToGeneId = new HashMap<String, Set<Long>>();
		geneIdToSymbol = new HashMap<Long, String>();

		readMappingFromFiles();
	}

	private static void readMappingFromFiles() {
		File file = new File(
				"/Users/keqiangli/Documents/workspace/cy-ontology-display/src/main/resources/files/"
						+ GENE_TO_GO);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			br.readLine(); // skip the header line
			while (true) {
				line = br.readLine();
				if (line == null)
					break;

				String[] splitFields = line.split("\t");

				String goId = splitFields[1];
				Long geneId = Long.parseLong(splitFields[0]);

				if (goIdToGeneId.containsKey(goId)) {
					goIdToGeneId.get(goId).add(geneId);
				} else {
					Set<Long> geneIds = new HashSet<Long>();
					geneIds.add(geneId);
					goIdToGeneId.put(goId, geneIds);
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		file = new File(
				"/Users/keqiangli/Documents/workspace/cy-ontology-display/src/main/resources/files/"
						+ GENE_TO_SYMBOL);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			br.readLine(); // skip the header line
			while (true) {
				line = br.readLine();
				if (line == null)
					break;

				String[] splitFields = line.split("\t");

				String symbol = splitFields[1];
				if (symbol.equals("-"))
					continue;

				Long geneId = Long.parseLong(splitFields[0]);
				geneIdToSymbol.put(geneId, symbol);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<String> MapGoIdToSymbol(List<String> goIds) {
		if (goIdToGeneId == null || geneIdToSymbol == null)
			initialize();

		Set<String> symbols = new HashSet<String>();

		for (String goID : goIds) {
			if (!goIdToGeneId.containsKey(goID)) continue;
			Set<Long> geneIds = goIdToGeneId.get(goID);
			for (Long geneID : geneIds) {
				if (geneIdToSymbol.containsKey(geneID)) {
					String symbol = geneIdToSymbol.get(geneID);
					symbols.add(symbol);
				}
			}
		}

		return symbols;
	}
}
