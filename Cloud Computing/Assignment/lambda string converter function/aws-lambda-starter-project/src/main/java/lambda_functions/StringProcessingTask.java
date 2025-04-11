package lambda_functions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.*;

import org.json.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.io.StringWriter;

public class StringProcessingTask  implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();

        try {
        	//parse json input into string for transformation
            JSONObject inputJson = new JSONObject(request.getBody());
            String text = inputJson.optString("text", "").trim();

            //handle empty input
            if (text.isEmpty()) {
                throw new IllegalArgumentException("Please enter some json data with the value of 'text'.");
            }

            //process input
            JSONObject result = new JSONObject();
            result.put("Original : ", text);
            result.put("Number of characters : ", text.length());
            result.put("Number of words : ", text.split("\\s+").length);
            result.put("String reversed : ", new StringBuilder(text).reverse().toString());
            result.put("Words reversed : ", reverseAllWords(text));
            result.put("All uppercase : ", text.toUpperCase());
            result.put("All lowercase : ", text.toLowerCase());
            result.put("Crazy case : ", convertToCrazyCase(text));
            result.put("Camel case : ", convertToCamelCase(text));
            result.put("Kebab case : ", convertToKebabCase(text));
            result.put("Snake case : ", convertToSnakeCase(text));
            result.put("String with vowels all removed : ", text.replaceAll("[aeiou]", ""));
            result.put("Count the occurrence of every character in the string  : ", countAllCharacters(text));

            //check to see if user wants xml format     
            boolean userWantsXMLFormat = false;
            Map<String, String> headers = request.getHeaders();
            if (headers != null && headers.containsKey("Accept") && headers.get("Accept").contains("xml")) {
                userWantsXMLFormat = true;
            }
            
            if (userWantsXMLFormat) {
                responseEvent.setStatusCode(200);
                responseEvent.setBody(xmlOutput(result));
                responseEvent.setHeaders(getHeadersForXml());
            } else {
                responseEvent.setStatusCode(200);
                responseEvent.setBody(result.toString());
                responseEvent.setHeaders(getHeadersForJson());
            }

        } catch (Exception e) {
            responseEvent.setStatusCode(400);
            responseEvent.setBody("{\"error\": \"" + e.getMessage() + "\"}");
        }

        return responseEvent;
    }

    private String reverseAllWords(String text) {
        String[] words = text.split(" ");
        Collections.reverse(Arrays.asList(words));
        return String.join(" ", words);
    }

    private String convertToCrazyCase(String text) {
        StringBuilder crazy = new StringBuilder();
        boolean upper = true;
        for (char c : text.toCharArray()) {
            crazy.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
            if (Character.isLetter(c)) upper = !upper;
        }
        return crazy.toString();
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

    private String convertToKebabCase(String text) {
        return text.toLowerCase().replaceAll("\\s+", "-");
    }

    private String convertToSnakeCase(String text) {
        return text.toLowerCase().replaceAll("\\s+", "_");
    }

    private Map<Character, Integer> countAllCharacters(String text) {
        Map<Character, Integer> charCounts = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
            }
        }
        return charCounts;
    }
    
     //xml output instead of json
     private String xmlOutput(JSONObject data) throws Exception {
         Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
         Element root = doc.createElement("stringAnalysis");
         doc.appendChild(root);
       
         Iterator<String> keys = data.keys();
         while (keys.hasNext()) {
             String key = keys.next();
             Element el = doc.createElement(key.replaceAll("[^a-zA-Z0-9]", ""));
          
             if (data.get(key) instanceof JSONObject) {
                 JSONObject charCounts = data.getJSONObject(key);
                 Iterator<String> charKeys = charCounts.keys();
                 while (charKeys.hasNext()) {
                     String charKey = charKeys.next();
                     Element charEl = doc.createElement("char");
                     charEl.setAttribute("value", charKey);
                     charEl.setTextContent(charCounts.get(charKey).toString());
                     el.appendChild(charEl);
                 }
             } else {
                 el.setTextContent(data.get(key).toString());
             }
             root.appendChild(el);
         }
      
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(writer));
      return writer.toString();
     }

    private Map<String, String> getHeadersForJson() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
    
    private Map<String, String> getHeadersForXml() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml");
        return headers;
    }
    
}