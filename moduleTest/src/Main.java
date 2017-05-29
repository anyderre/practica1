

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.validator.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.apache.commons.validator.routines.*;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Dany on 23/05/2017.
 */

public class Main {
    public static void main(String [] args){
        System.out.println("Enter a url");
        String validUrl="";
        Document document= null;
        Boolean validated=false;
        while (!validated){

            Scanner scanner  = new Scanner(System.in);
            String url = scanner.nextLine();
            if (isValidURL(url)){
                validated=true;
                validUrl=url;
            }else{
                System.out.println("Enter a new url, that one is invalid!!!");
                validated=false;
            }
        }

        try {
             document= Jsoup.connect(validUrl).get();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        //Pregunta a) Cantidades de lineas que retorna el Html
        System.out.println(numberOfLines(document));
        //Pregunta b) cantidades de parrafos que hay en el documento HTML
        System.out.println("Number of 'p'-> "+ tagsLength("p", document)[0]);
        //pregunta c) Cantidades de tags img que hay en el documento HTML
        System.out.println("Number of 'img'-> "+ tagsLength("img", document)[0]);
        //pregunta d)cantidad de form categorizado por GET o por POST
        int [] tagsLength = new int[]{};
        tagsLength=tagsLength("form", document);
        System.out.println("Number of form[Post]-> "+tagsLength[0]+"\n"+"Number of form[Get]-> "+tagsLength[1]);
        //pregunta e) mostrar los inputs con sus respectivos tipos
        if(inputTags(document)!=null){
            System.out.print("{");
            for(String type:inputTags(document)){
                System.out.print("input-> type="+type+",\n");
            }
            System.out.print("}\n\n");
        }else{
            System.out.printf("No hay input tags");
        }

        //Pregunta f)
        printParams(document, validUrl);

    }
    private static boolean isValidURL(String inputURL){
        try {
            UrlValidator url = new UrlValidator(new String[]{"http", "https"});
                if(url.isValid(inputURL)){
                    return true;
                }

        }catch (Exception ec){
            ec.printStackTrace();
        }
        return false;
    }
    private  static int numberOfLines(Document document) {

        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").append("\n");
        document.select("html").append("\n");
        document.select("body").append("\n");
        return document.html().split("\n").length;

    }
    //Returning the
    private  static int[] tagsLength(String tag, Document document){

        int [] tags = new int[2];
        switch (tag){
            case "p":
                tags[0]=document.getElementsByTag("p").size();
                return tags;

            case "img":
                tags[0]= document.select("p img").size();
                return tags;

            case "form":

                tags[0]=document.select("form[method=post]").size();// first index -> Post
                tags[1]=document.select("form[method=get]").size(); //second index -> Get
                return tags;

            default:
                return tags;

        }

    }

    private static ArrayList<String> inputTags(Document document){


       //String[] types = new String[]{};
        ArrayList<String> types = new ArrayList<>();
        int count=0;
           for (Element form :document.getElementsByTag("form")){
               for (Element input :form.getElementsByTag("input")){
                   types.add(input.getElementsByAttribute("type").attr("type"));
               }
           }


        return types;
    }

    private static void printParams(Document document, String validUrl){
        Connection.Response response=null;
        try {
            for (Element form: document.getElementsByTag("form")){
                //System.out.println(form);
                if (form.attr("method").equals("post") || form.attr("method").equals("POST")) {
                        if ( document.getElementsByTag("form[method=post]").attr("action").contains("http")){
                         response = Jsoup.connect(document.getElementsByTag("form[method=post]").attr("action")).data("asignatura", "practica1").userAgent("Chrome").method(Connection.Method.POST).execute();
                        if(response.statusCode()==200)
                            System.out.println(response.parse());
                    }else{
                        response = Jsoup.connect(validUrl+document.getElementsByTag("form[method=post]").attr("action")).data("asignatura","practica1").userAgent("Chrome").method(Connection.Method.POST).execute();
                        if(response.statusCode()==200)
                            System.out.println(response.parse());

                    }
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }

    }
}
