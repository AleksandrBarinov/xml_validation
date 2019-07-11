import entities.FeedObject;
import org.testng.annotations.Test;
import util.BaseSteps;

public class TestXMLValidation extends BaseSteps {

    @Test
    public void test(){
        htmlBegin();

        FeedList list = new FeedList();

        for (int i=0;i<4;i++) {
            try {
                list.request();
                System.out.println("[Список получен]");
                i=10;
            } catch (Exception e){
                System.err.println("Возникло исключение при попытке получения списка: " + e + "\n");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored){}
            }
        } //i попыток получить список фидов

        FeedValidator validator = new FeedValidator();
        FieldsValidator fieldsValidator = new FieldsValidator();

        for (FeedObject feedObject: list.getFeedsList()){
            try {
                logger.info("<p>");

                String feedName = feedObject.getName().replaceAll("[А-Яа-я ]", "");
                feedName = feedName.replaceAll(".xml","") + ".xml";

                switch (feedObject.getSchema()){
                    case "yandex":
                        validator.download(feedObject.getUrl(),"feed/"+ feedName +"");
                        validator.validate("feed/"+ feedName +"","schema/YML.xsd");
                        fieldsValidator.yandex("feed/"+ feedName +"");
                        logger.info("<br><small>список id("+ getBadIdsCount()+"), которые не прошли валидацию, во вложении </small>");
                        resetBadIdsCount();
                        break;
                    case "google":
                        validator.download(feedObject.getUrl(),"feed/"+ feedName +"");
                        validator.validate("feed/"+ feedName +"","schema/google.xsd");
                        fieldsValidator.google("feed/"+ feedName +"");
                        logger.info("<br><small>список id("+ getBadIdsCount()+"), которые не прошли валидацию, во вложении </small>");
                        resetBadIdsCount();
                        break;
                }

                logger.info("<br><small>***</small></p>");

            } catch (Exception e){
                System.err.println("Возникло исключение в цикле: " + e);
                state="exception";
            }
            logger.info("<hr />");
        }

        PackInZip();
        deleteFilesToZip();

        htmlEnd();
        checkExceptions(state);
    }
}