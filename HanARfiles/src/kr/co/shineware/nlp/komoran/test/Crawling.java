package kr.co.shineware.nlp.komoran.test;//revise if you need other package..

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL; 
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class Crawling {
	private static final String USER_AGENT = "Safari"; // Type of browser can be changed!!!
	private static final String url = "https://play.google.com/store/getreviews";
	private static final int MAX_REVIEW_COUNT = 50; // The number of reviews can be changed
	private static final String TEMPLE_RUN_2 = "com.imangi.templerun2"; // Type of application can be changed // the game name 1
	private static final String DESPICABLE_ME = "com.gameloft.android.ANMP.GloftDMHM"; //the game name 2
	private static final String SUBWAY_SURF = "com.kiloo.subwaysurf"; //the game name 3
	private static final String Korail = "com.hanainfo.glorystation&hl=ko";  // Application for Korean reviews // Korail
	private static final String CLASH_OF_CLAN = "com.supercell.clashofclans&hl=ko"; 
	private static String Application_name;
	private String applicationId = "";
	private int pageNo = 0;
	private int review_count = 1;
	private static boolean end = false;
	private static File file2;
	
	public Crawling(String applicationId){ //save file as xml 
		this.applicationId = applicationId;
		 file2 = new File("C:\\Users\\Public", "ReviewList.xml");  //share\\        
	}

	
	public static void main(String[] args) {        //main function!
		
	    	//we pick the id for application 
	    String message;
	    Scanner scan = new Scanner(System.in);      // Get application link (just copy from GPS)
	    
	    System.out.println("어플 URL을 입력해주세요. // Type App. URL!");
	    //https://play.google.com/store/apps/details?id=com.supercell.clashofclans&hl=ko
	    // ->클래시 오브 클랜 예시 (example : clash of clan ) 
	    //https://play.google.com/store/apps/details?id=com.imangi.templerun2
	    message = scan.nextLine(); 
	    Application_name  = message.substring(message.indexOf("id=")+3,message.length()); 
	    
		Crawling http = new Crawling(Application_name);  // Application.java -> make itself as 
																								//here uses class variable  DESPICABLE_ME
		System.setProperty("file.encoding", "UTF-8");
		System.out.println("file encoding : " + System.getProperty("file.encoding"));
		try{
			System.out.println("Send Http POST request");   // 
			System.out.println("Start crawling your app.reviews");
			file2.createNewFile();
			FileOutputStream fw2 = new FileOutputStream(file2.getAbsoluteFile());
			OutputStreamWriter fw = new OutputStreamWriter(fw2, "UTF-8");
			fw.write(("<?xml version=\"1.0\" encoding=\""+System.getProperty("file.encoding")+"\"?>\n"));//+System.getProperty("file.encoding")
			fw.write("<ReviewList>\n"); //writes things in () to fw. 
																					//http.applicationld is done for DESPICABLE_ME
																					//the file name is  DESPICABLE_ME.xml in this case.

			fw.close();															
			while(!end){	
				http.sendPost();
			}
			Classify.Classify_method();

			
		}catch(Exception e){
			System.out.println("We got Exception! na!");
			System.out.println(e);

		}

	}
	// HTTP POST request
	private void sendPost() throws Exception {	 			
		
 
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection(); //closed with disconnect
		//URLEncoder.encode("UTF-8");
		
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Charset","UTF-8");
		con.setRequestProperty("Accept-Language", "utf-8,en;q=0.5");
		//con.setCharacterEncoding("UTF-8");
		con.setDoOutput(true);
 
		String urlParameters = "reviewType=0&pageNum="+pageNo+"&id="+applicationId+"&reviewSortOrder=2&xhr=1";
 
		// Send post request		
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();
		in.readLine();
		in.readLine();
		while ((inputLine = in.readLine()) != null) {			
			response.append(inputLine);
		}
		
		//print result
		JSONParser parser=new JSONParser();
		Object o = parser.parse(response.toString());
		JSONArray arr = (JSONArray)o;
		JSONArray o2 = (JSONArray) arr.get(0);
		
		in.close();

		System.out.println("By MinSeok");
		if(o2.size()!=2 && review_count < MAX_REVIEW_COUNT){
			processResult((String)o2.get(2));
			pageNo++;
		}else{
			try{
				end = true;

				FileOutputStream fw2 = new FileOutputStream(file2.getAbsoluteFile(), true);
				OutputStreamWriter fw = new OutputStreamWriter(fw2, "UTF-8");
				fw.write("</ReviewList>\n");
					//For the name....
				fw.close();
			}catch(Exception e){
				e.toString();
			}
		}
	
		con.disconnect();
		
	}
	
	private void processResult(String body){
		String xmlToWrite = "";
		Document doc = Jsoup.parseBodyFragment(body);
		//System.out.println(doc.html());
		
		Elements reviews = doc.getElementsByClass("single-review");
			
		for (Element review : reviews){
			String ratingCss = review.getElementsByClass("current-rating").first().attr("style");
			ratingCss = ratingCss.substring(7,ratingCss.length()-4);
			//int rating = Integer.parseInt(ratingCss)/20;
			//xmlToWrite += "<doc id=\""+review_count+"\">\n";
			xmlToWrite += "<doc>\n";
			xmlToWrite += "<name>"+review.select("a[title~=.*]").first().text() +"</name>\n";
			xmlToWrite += "<date>"+review.getElementsByClass("review-date").first().text()+"</date>\n";			
			//xmlToWrite += "<rating>"+rating+"</rating>\n";
			xmlToWrite += "<title>"+review.getElementsByClass("review-title").first().text()+"</title>\n";
			xmlToWrite += "<review>"+review.getElementsByClass("review-body").first().childNode(2)+"</review>\n";
			xmlToWrite += "</doc>\n";
			System.out.println("This is " + review_count + " th review");
			System.out.println(review.getElementsByClass("review-body").first().childNode(2));
			review_count++;
		}
		try{


			FileOutputStream fw2 = new FileOutputStream(file2.getAbsoluteFile(), true);
			OutputStreamWriter fw = new OutputStreamWriter(fw2, "UTF-8");
			fw.write(xmlToWrite); // write all the reviews to xml file
				//For the name....
			fw.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
}





