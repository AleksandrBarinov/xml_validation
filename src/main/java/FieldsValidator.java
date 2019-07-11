import org.apache.commons.validator.routines.UrlValidator;
import org.testng.Assert;
import util.BaseSteps;

class FieldsValidator extends BaseSteps {

    //переменные и методы

    /**валидация полей для схемы YML.xsd**/
    void yandex(String fileName){
        //
    }

    /**валидация полей для схемы google.xsd**/
    void google(String fileName){
        //
    }

    private void urlValidator(String url, int id){
        urlValidator(url,id,false);
    }
    private void urlValidator(String url, int id ,boolean isPictureUrl ){
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (!urlValidator.isValid(url)) {

            if (!isPictureUrl) {
                System.err.println("id " + id + " - URL is not valid (" + url + ")");
                textToFileWriterCall("id " + id + " - URL is not valid (" + url + ")");
                addBadId();
            } else {
                System.err.println("id " + id + " - PICTURE URL is not valid (" + url + ")");
                textToFileWriterCall("id " + id + " - PICTURE URL is not valid (" + url + ")");
                addBadId();
            }
        }
    }

    /**проверка, что url заканчивается на расширение графического файла**/
    private void pictureUrlValidator(String url, int id){
        urlValidator(url,id,true);
        String extension = url.substring(url.lastIndexOf("."));

        try{
            Assert.assertTrue((
                extension.equals(".JPG") ||
                    extension.equals(".jpg") ||
                        extension.equals(".JPEG") ||
                            extension.equals(".jpeg") ||
                                extension.equals(".PNG") ||
                                    extension.equals(".png")) ||
                                        extension.equals(".GIF") ||
                                            extension.equals(".gif") ||
                                                extension.equals(".TIF") ||
                                                    extension.equals(".tif") ||
                                                        extension.equals(".TIFF") ||
                                                            extension.equals(".tiff")
                                            ,"extension");
        } catch (AssertionError error) {
            System.err.println("id "+id+" - NOT EXPECTED EXTENSION: " + extension);
            textToFileWriterCall("id "+id+" - NOT EXPECTED EXTENSION: " + url);
            addBadId();
        }
    }
}