package lambda_functions;

import java.util.*;
import org.json.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import com.amazonaws.services.lambda.runtime.*;

public class StringProcessingTask implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            
            String text = ((Map<String, String>) input.get("body")).get("text").trim();
            if (text.isEmpty()) throw new Exception("Input text cannot be empty");
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("original : ", text);
            result.put("Number of characters : ", text.length());
            result.put("Number of words : ", text.split("\\s+").length);
            result.put("String reversed : ", new StringBuilder(text).reverse().toString());
            result.put("Words reversed : ", reverseAllWords(text));
            result.put("All uppercase : ", text.toUpperCase());
            result.put("All lowercase : ", text.toLowerCase());
            result.put("Crazy case : ", convertToCrazyCase(text));
            result.put("Camel case : ", convertToCamelCase(text));
            result.put("Kebab case : ", text.toLowerCase().replaceAll("\\s+", "-"));
            result.put("Snake case : ", text.toLowerCase().replaceAll("\\s+", "_"));
            result.put("String with vowels all removed : ", text.replaceAll("[aeiouAEIOU]", ""));
            result.put("Count the occurrence of every character in the String (returned as JSON object/array) : ", countAllCharacters(text));
            
            boolean userWantsXMLFormat = input.get("headers") != null && 
                             ((Map<String, String>) input.get("headers"))
                             .get("Accept").contains("xml");
            
            if (userWantsXMLFormat) {
                response.put("statusCode", 200);
                response.put("headers", Map.of("Content-Type", "application/xml"));
                response.put("body", toXml(result));
            } else {
                response.put("statusCode", 200);
                response.put("headers", Map.of("Content-Type", "application/json"));
                response.put("body", new JSONObject(result).toString());
            }
            
        } catch (Exception e) {
            response.put("statusCode", 400);
            response.put("body", "Error: " + e.getMessage());
        }
        
        return response;
    }

    //all transformations below

    private String reverseAllWords(String text) {
        String[] words = text.split(" ");
        Collections.reverse(Arrays.asList(words));
        return String.join(" ", words);
    }

    private String convertToCrazyCase(String text) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : text.toCharArray()) {
            sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
            if (Character.isLetter(c)) upper = !upper;
        }
        return sb.toString();
    }

    private String convertToCamelCase(String text) {
        String[] words = text.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++) {
            sb.append(Character.toUpperCase(words[i].charAt(0)))
              .append(words[i].substring(1));
        }
        return sb.toString();
    }

    private Map<Character, Integer> countAllCharacters(String text) {
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : text.toCharArray()) {
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return counts;
    }

    private String toXml(Map<String, Object> data) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("stringAnalysis");
        doc.appendChild(root);
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Element el = doc.createElement(entry.getKey());
            if (entry.getValue() instanceof Map) {
                Map<Character, Integer> counts = (Map<Character, Integer>) entry.getValue();
                for (Map.Entry<Character, Integer> e : counts.entrySet()) {
                    Element charEl = doc.createElement("char");
                    charEl.setAttribute("value", e.getKey().toString());
                    charEl.setTextContent(e.getValue().toString());
                    el.appendChild(charEl);
                }
            } else {
                el.setTextContent(entry.getValue().toString());
            }
            root.appendChild(el);
        }
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}