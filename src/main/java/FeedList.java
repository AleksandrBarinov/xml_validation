import entities.FeedObject;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import util.BaseSteps;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

class FeedList extends BaseSteps {

    private String responseBody;

    private List <FeedObject> feedsList;

    void request(){
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet get = new HttpGet("https://api.url/xml/list/");
            CloseableHttpResponse getResponse = httpClient.execute(get);
            responseBody = EntityUtils.toString(getResponse.getEntity());
            httpClient.close();
        } catch (Exception e){
            logger.info("<p style='color:Red'>Возникло исключение во время выполнения запроса для получения списка:<br>" + e + "</p>");
            state="exception";
        }
        checkExceptions(state);
    }

    List <FeedObject> getFeedsList(){
        try {
            Type feedObjectType = new TypeToken<Collection<FeedObject>>() {}.getType();
            feedsList = gson.fromJson(responseBody,feedObjectType);
        } catch (Exception e){
            logger.info("<p style='color:Red'>Возникло исключение во время получения списка:<br>" + e + "</p>");
            state="exception";
        }
        checkExceptions(state);
        return feedsList;
    }
}