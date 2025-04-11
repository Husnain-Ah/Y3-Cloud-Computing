package lambda_functions;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class HelloWorld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		
		APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
		// TODO: do some awesome stuff here
		String method = request.getHttpMethod();
		Map<String, String> params = request.getQueryStringParameters();

		responseEvent.setStatusCode(200);
		responseEvent.setBody("Hi, " + params.get("name"));

		return responseEvent;
	}

}
