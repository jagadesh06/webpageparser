import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
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
			// Word pair frequency
			LinkedList<String> al = new LinkedList<>();
			AtomicReference<String> prevWord = new AtomicReference<>();
			wordList.forEach(val -> {
				String[] strArr = val.split(" ");
				if (strArr.length > 0) {
					prevWord.set(strArr[0]);
					if (strArr.length == 1)
						al.add(strArr[0]);
					for (int i = 1; i < strArr.length; i++) {
						al.add(prevWord + " " + strArr[i]);
						prevWord.set(strArr[i]);
					}
				}
			});
			al.stream().collect(Collectors.toMap(val -> val.toString(), val -> 1, Integer::sum)).entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10).forEach(action -> {
						System.out.println("Word : " + action.getKey() + " Frequency : " + action.getValue());
					});
			// Word Frequency
			System.out.println();
			wordList.stream().map(val -> val.split(" ")).flatMap(Arrays::stream).collect(Collectors.toList()).stream()
					.collect(Collectors.toMap(val -> val.toString(), val -> 1, Integer::sum)).entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10).forEach(action -> {
						System.out.println("Word : " + action.getKey() + " Frequency : " + action.getValue());
					});
		} catch (Exception e) {
			System.out.println("Error while parsing " + e);
		}
	}

	public static void parsePage(String url, LinkedList<String> processedURL, String hostName,
			HashSet<String> outputList) {
		try {
			if (isURLValid(url, hostName) && !processedURL.contains(url)) {
				// System.out.println(url);
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
						outputList.add(value.toString().trim());
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
