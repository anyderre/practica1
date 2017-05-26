

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;


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
        System.out.println("Number of form[Post]-> "+tagsLength("form", document)[0]+"\n"+"Number of form[Get]-> "+tagsLength("form", document)[1]);
        //pregunta e) mostrar los inputs con sus respectivos tipos
        if(inputTags(document)!=null){
            System.out.print("{");
            for(String type:inputTags(document)){
                System.out.print("input-> type="+type+",\n");
            }
            System.out.print("}");
        }else{
            System.out.printf("No hay input tags");
        }


        System.out.println(document);

    }
    private static boolean isValidURL(String inputURL){
        try {
            URL url = new URL(inputURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            return true;
        } catch (MalformedURLException e) {
            System.out.println("Invalid Url,");
        }catch (IOException ex){
            System.out.print(" try with a new one!!!");
        }catch (IllegalArgumentException exc){
            System.out.println("Illegal argument");
        }
        return false;
    }
    private  static int numberOfLines(Document document) {
        return document.select("*").size();

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
        if (document.getElementsByTag("form").hasText()) {
            Elements forms = document.getElementsByTag("form");
            for(Element form: forms){
                Elements inputs = form.getElementsByTag("input");
                for(Element input : inputs){
                   types.add(input.attr("type"));
                }
                count+=1;
            }
        }
        return types;
    }

}
