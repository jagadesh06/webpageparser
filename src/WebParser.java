import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

/**
 * 
 */

/**
 * @author jagadesh
 *
 */
public class WebParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String url = br.readLine();
			HashSet<String> wordList = new HashSet<>();
			parsePage(url, new LinkedList<>(), new URL(url).getHost(), wordList);
			/*
			 * Map<Object, Integer> map = wordList.stream().map(val ->
			 * val.split(" ")).flatMap(Arrays::stream)
			 * .collect(Collectors.toList()).stream() .collect(Collectors.toMap(val ->
			 * val.toString(), val -> 1, Integer::sum)); map.forEach((k, v) -> {
			 * System.out.println("Key :" + k + " Value : " + v); });
			 */
		} catch (Exception e) {
			System.out.print("Error while parsing " + e);
		}
	}

	public static void parsePage(String url, LinkedList<String> processedURL, String hostName,
			HashSet<String> outputList) {
		try {
			if (isURLValid(url, hostName) && !processedURL.contains(url)) {
				//System.out.println(url);
				Document htmlDom = Jsoup.connect(url).get();
				processedURL.add(url);
				List<Node> nodes = htmlDom.body().childNodes();
				nodes.forEach(childNode -> {
					parseDom(childNode, processedURL, hostName, outputList);
				});
			}
		} catch (Exception e) {
			System.out.print("Error while parsing " + e);
		}
	}

	public static void parseDom(Node node, LinkedList<String> processedURL, String hostName,
			HashSet<String> outputList) {
		if (node.childNodeSize() > 0) {
			node.childNodes().stream().forEach(value -> {
				if (value.nodeName().equals("a")) {
					parsePage(value.attr("href"), processedURL, hostName, outputList);
				}
				if (value.childNodeSize() > 0) {
					parseDom(value, processedURL, hostName, outputList);

				} else {
					if (value.nodeName().startsWith("#text")) {
						outputList.add(value.toString());
					}
				}
			});
		}
	}

	/**
	 * Returns true if the given URL is valid one and belongs to the same host given
	 * in the input.
	 * 
	 * @param url
	 *            URL for which on which validation to be perfomed
	 * @param hostname
	 *            Hostname of the URL given in the input
	 * @return true if the url is valid one; false otherwise
	 */
	public static boolean isURLValid(String url, String hostname) {
		try {
			URL urlObj = new URL(url);
			urlObj.toURI();
			if (urlObj.getHost().equals(hostname)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
