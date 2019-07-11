import org.apache.commons.io.FileUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import util.BaseSteps;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

class FeedValidator extends BaseSteps {

    private Schema schema;
    private String fileNameDate = month.format(date);

    /**попытка удалить файл xml, созданный при предыдущем выполнении**/
    private void delete(String fileName){
        try {
            File file = new File(fileName);
            if (file.delete()){
                System.out.println("["+fileName+" успешно удален]");
            }
        } catch (Exception e){System.err.println(""+fileName+" не удален: " + e + "\n");}
    }

    /**загрузка файла по ссылке**/
    void download(String url, String fileName){
        delete(fileName);

//        InputStream in = URI.create(url).toURL().openStream();
//        Files.copy(in, Paths.get(fileName));

        CloseableHttpClient httpClient = null;
        HttpGet httpGet;
        CloseableHttpResponse httpResponse = null;

        try {
            for (int i=0;i<4;i++) {
                try {
                    httpClient = HttpClients.createDefault();
                    httpGet = new HttpGet(url);
                    httpResponse = httpClient.execute(httpGet);
                    HttpEntity fileEntity = httpResponse.getEntity();

                    if (fileEntity != null) {
                        FileUtils.copyInputStreamToFile(fileEntity.getContent(), new File(fileName));
                        logger.info("<a href="+url+">"+url+"</a>");
                    }
                    httpClient.close();
                    i=10;
                } catch (ConnectionClosedException e){
                    System.err.println("Возникло исключение при загрузке файла: " + e + "\n");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored){}
                }
            } //i попыток загрузить файл
        } catch (Exception e){
            logger.info("<a href="+url+">"+url+"</a>");
            logger.info("<span class='colortext'>Не удалось загрузить "+fileName+":<br>" + e + "</span>");
            state = "exception";
        }
    }

    /**проверяем загруженный файл на соответствие xsd схеме**/
    void validate(String fileName,String schemaName){

        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Source xsdFile = new StreamSource(new File(schemaName));
        try {
            schema = factory.newSchema(xsdFile);
        } catch (Exception e){
            logger.info("<br><span class='colortext'>Возникло исключение при загрузке схемы:<br>" + e + "</span>");
            state = "exception";
        }

        Validator validator = schema.newValidator();
        Source xmlFile = new StreamSource(new File(fileName));

        try {
            validator.validate(xmlFile);
            logger.info("<br>соответствует схеме");

        } catch (Exception e){
            logger.info("<br><span class='colortext'>не соответствует схеме:<br>" + e + "</span>");
            try {
                File source = new File(fileName);
                File target = new File("fail/feed");
                if (!target.exists()){
                    System.out.println(target.mkdir());
                }
                target = new File("fail/",fileName.replaceAll("(.xml)","_"+fileNameDate+".xml"));

                Files.copy(source.toPath(), target.toPath());

            } catch (Exception e1) {
                e1.printStackTrace();
                logger.info("<br><span class='colortext'>Возникло исключение при копировании "+fileName+":<br>" + e1 + "</span>");
            }
            state = "exception";
        }
    }
}