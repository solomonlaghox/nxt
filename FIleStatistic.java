/**
 * class FileStatistic 
 * prints statistics for files in specifics folder
 * 
*/
//IMPORTS
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Scanner;

// PDFBox: pdf parser 
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class FIleStatistic{
    // OUT :: PrintStream System.out 
    private static PrintStream OUT = System.out;
    
    // SETPATH 
    //  initial as null. 
    //  :: argument 
    //  :: sets paths from the console argument.
    private static String SETPATH = null;
    private static String PROCESSED = null;
    
    // Method Initialize
    // set setpath
    private static void initialize(String src){
        SETPATH = src;
        PROCESSED = SETPATH+"processed";
        File processed = new File(PROCESSED);
        if(!processed.exists() || !processed.isDirectory()) processed.mkdirs();
    }
    // Move file to 'processed' after extracts
    private static void moveFile(File file,String name){
        file.renameTo(new File(PROCESSED+"/"+name));
        OUT.println(PROCESSED+"/"+name);
        OUT.println("'"+ file.getName() +"' moved to processed folder");
    }

    // set statistics of files found in input folder
    private static void setStatistics(BufferedReader reader,String loc,File file,String name){
        String line = null;
        int words = 0;
        int lines = 0;
        int characters = 0;

        try {
            while((line = reader.readLine())!=null){
                String curline = line;
                int len = line.length();
                Boolean division = true;
                Boolean foundChar = false;
                for(int i=0; i<len; i++){
                    if(curline.charAt(i) == ' '){
                        division = true;
                        foundChar = false;
                    }else {
                        foundChar = true;
                        characters++;
                    }
                    if(foundChar&&division){
                        words++;
                        division = false;
                    }
                }                
                lines++;
            }
            OUT.println("\nFile: "+loc);
            OUT.println("Number of words: "+ words);
            OUT.println("Number of characters: "+ characters);
            OUT.println("Number of lines: "+ lines);
            FIleStatistic.moveFile(file,name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // - ParseString 
    // - parses strings extracted from pdf && doc/docx into BufferedReader
    // - then to setStatistic()
    private static void parseStrings(String str, String loc,File file,String name){
        BufferedReader reader = new BufferedReader(new StringReader(str));
        FIleStatistic.setStatistics(reader,loc,file,name);
    }
    // Extracts supported files from folder to be processed
    // procesed with getFile
    private static void listFileForFolder(final File folder){
        for(final File fileEntry: folder.listFiles()){
            if(!fileEntry.isDirectory()){
                FIleStatistic.getFile(folder+"/"+fileEntry.getName(),fileEntry.getName());
           }
        }
    }
    // Extracts txt file
    private static void getText(String fileLocation,String name){
        try {
            File file = new File(fileLocation);
            BufferedReader reader =  new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
            FIleStatistic.setStatistics(reader,fileLocation,file,name);    
        } catch (Exception e) {
            
        }
    }
    // Extracts texts from doc/docx from file
    private static void getDocx(String fileLocation,String name){

    }
    // Extracts texts from pdf from file
    private static void getPdf(String fileLocation,String name){
        try {
            File pdfFile = new File(fileLocation);
            PDDocument doc = Loader.loadPDF(pdfFile);
            String text = new PDFTextStripper().getText(doc);
            FIleStatistic.parseStrings(text,fileLocation,pdfFile,name);    
        } catch (IOException e) {
           
        }        
    }
    // get default files [pdf,doc,docx,txt]
    private static void getFile(String fileLocation,String name){
       String[] val = fileLocation.split("\\.");
       int len = val.length;
       String type = val[len-1];
       if(type.equals("txt")) FIleStatistic.getText(fileLocation,name);
       else if(type.equals("pdf")) FIleStatistic.getPdf(fileLocation,name);
       else if(type.equals("docx")) FIleStatistic.getDocx(fileLocation,name); 
    }
    public static void main(String[] args) throws IOException{
        Console console = System.console();
        if(console!=null){

            Scanner scanner  =  new Scanner(console.reader());
            OUT.println("Insert a valid location to  process your files.\nNote, file supported format: .pdf, .doc, .txt and .docx respectively!");
            String src = scanner.nextLine();
            File path = new File(src);
            if(path.exists() && path.isDirectory()){
                FIleStatistic.initialize(src);
                FIleStatistic.listFileForFolder(path);  
            }
            else System.err.println("Ooops!\nSource provided is invalid");  

        }else OUT.println("this is a console program");
        
    }
}
