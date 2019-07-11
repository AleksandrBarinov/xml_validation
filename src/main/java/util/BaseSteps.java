package util;

import com.google.gson.*;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BaseSteps {
    protected static DateFormat month = new SimpleDateFormat("yyyy-MM-dd_HHmm");
    protected static Date date = new Date();

    protected static Logger logger = Logger.getLogger("logger");
    protected static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected static String state = "done";

    protected void checkExceptions(String state){
        try {
            Assert.assertEquals(state,"done","checkExceptions");
        } catch (Exception e){
            logger.info(e);
        }
    }

    protected static void htmlBegin(){
        logger.info("<html>");
        logger.info("<head>");
        logger.info("<meta charset=Windows-1251>");
        logger.info("<style>.colortext{color: red;}</style>");
        logger.info("<body>");
    }
    protected static void htmlEnd() {
        logger.info("</body>");
        logger.info("</head>");
        logger.info("</html>");
    }

    private static Writer writer;

    protected static void textToFileCreateWriter(String fileName){
        try {
            String fileNameDate = month.format(date);
            writer = new BufferedWriter(new FileWriter ( ""+fileName+"bad_ids_"+fileNameDate+".txt" ));
            addToBadIds(""+fileName+"bad_ids_"+fileNameDate+".txt");
            System.out.println("\n" + writer.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected static void textToFileWriterCall(String textToWrite){
        try {
            writer.write("\n" + textToWrite);
        } catch (IOException e) {
            System.err.println("Возникло исключение при попытке записи в текстовый файл: " + e);
        }
    }
    protected static void textToFileWriterClose(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> badIds = new LinkedList<>();

    private static void addToBadIds(String fileName){
        badIds.add(fileName);
    }
    private static void deleteZipFile(){//remaining from previous execution
        try {
            File file = new File("bad_ids.zip");
            if (file.delete()){
                System.out.println("bad_url_ids.zip успешно удален]");
            }
        } catch (Exception e){System.err.println("bad_url_ids.zip не удален: " + e + "\n");}
    }
    protected static void PackInZip(){
        deleteZipFile();
        try {
            FileOutputStream outputStream = new FileOutputStream("bad_ids.zip");
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            for (String fileName: badIds) {
                File fileToZip = new File(fileName);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOutputStream.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOutputStream.close();
            outputStream.close();

        } catch (Exception e){
            System.err.println("Возникло исключение при попытке записи файлов в архив: " + e);
        }
    }
    protected static void deleteFilesToZip(){//already packed files
        for (String fileName: badIds) {
            try {
                File fileToDelete = new File(fileName);
                if (fileToDelete.delete()){
                    System.out.println(""+fileName+" успешно удален]");
                }
            } catch (Exception e){System.err.println(""+fileName+" не удален: " + e + "\n");}
        }
    }

    /**counter**/
    private static int badIdsCount;
    protected static void addBadId() {
        BaseSteps.badIdsCount = badIdsCount++;
    }
    protected static int getBadIdsCount() {
        return badIdsCount;
    }
    protected static void resetBadIdsCount() {
        BaseSteps.badIdsCount = 0;
    }
}