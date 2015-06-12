package kr.co.shineware.nlp.komoran.test;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class Classify {
	public static void Classify_method() {
		try {
			//리뷰 XML 파일 불러오기
			//File file = new File("C:\\tools\\ReviewList.xml");
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			StringBuilder xmlStringBuilder = new StringBuilder();
			ByteArrayInputStream input =  new ByteArrayInputStream(
			   xmlStringBuilder.toString().getBytes("UTF-8"));
			Document doc = docBuild.parse(new File("C:\\Users\\Public\\ReviewList.xml"));
			//Document doc = docBuild.parse(new File("C:\\Users\\Public\\ReviewList.xml"));
			doc.getDocumentElement().normalize();
			//System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
			//리뷰 배열
			ArrayList<String> numData = new ArrayList<String>();
			ArrayList<String> reviewData = new ArrayList<String>();
			ArrayList<String> boolData = new ArrayList<String>();
			
			//XML 리뷰 리스트
			NodeList xmlList = doc.getElementsByTagName("doc");
			
			//XML 리뷰 리스트에서 배열로 할당
			for (int i = 0; i < xmlList.getLength(); i++) {
 
				//System.out.println("---------- personNode "+ i + "번째 ------------------");

				Node xmlNode = xmlList.item(i);
 
				if (xmlNode.getNodeType() == Node.ELEMENT_NODE) {
					
					// 각각의 리뷰 리스트 요소
					Element xmlElmnt = (Element) xmlNode;
 
					// 실제 리뷰
					NodeList reviewList= xmlElmnt.getElementsByTagName("review");
					Element reviewElmnt = (Element) reviewList.item(0);
					Node review = reviewElmnt.getFirstChild();
					//System.out.println("name    : " + review.getNodeValue());
					//System.out.println(review.getNodeValue());
					//여기서 실제 리뷰를 배열로 할당하고 배열 완성
					reviewData.add(review.getNodeValue());
					numData.add(String.valueOf(i));
					boolData.add("false");
				}
 
			}
			///여기가안뜸

			//기준 XML파일 불러오기
			File file2 = new File("C:\\Users\\Public\\StandardList.xml"); //share\\
			DocumentBuilderFactory docBuildFact2 = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild2 = docBuildFact2.newDocumentBuilder();
			Document doc2 = docBuild2.parse(file2);
			doc2.getDocumentElement().normalize();
			
			//XML 기준 리스트
			NodeList setList = doc2.getElementsByTagName("review");
			
			//기준 배열
			ArrayList<String> stdData = new ArrayList<String>();
			
			//기준 리스트를 배열로 할당
			for (int j = 0; j < setList.getLength(); j++) {
				
				Node setNode = setList.item(j);
				
				// 리스트 요소
				Element setElmnt = (Element) setNode;
	 
				// 실제 기준
				NodeList stdList = setElmnt.getElementsByTagName("standard");
				Element stdElmnt = (Element) stdList.item(0);
				Node std = stdElmnt.getFirstChild();
				
				//배열로 할당
				stdData.add(std.getNodeValue());
					
			}
			
			for (int k = 0; k < reviewData.size(); k++) {
				for (int m = 0; m < stdData.size(); m++){
					String temreview = reviewData.get(k);
					String temstd = stdData.get(m);
					if(temreview.matches(".*"+temstd+".*"))
					{
						boolData.set(k,"true");
					}
				}
			}
			
			for (int m = 0; m < reviewData.size(); m++) {
				String temBool = boolData.get(m);
				if(temBool=="true") {
					System.out.println(numData.get(m)+"번째 리뷰는 "+boolData.get(m));
					System.out.println(reviewData.get(m));
				}
			}
			
			DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder icBuilder;
			icBuilder = icFactory.newDocumentBuilder();
		     
			Document doc3 = icBuilder.newDocument();
			Element mainRootElement = doc3.createElementNS(null, "ClassifiedList");
			doc3.appendChild(mainRootElement);
			
			// append child elements to root element
			for (int n = 0; n < reviewData.size(); n++) {
				String temBool = boolData.get(n);
				String temNum = numData.get(n);
				String temReview = reviewData.get(n);
				if(temBool=="true") {
					mainRootElement.appendChild(getCompany(doc3, temNum, temReview));
				}
			}
			
			// output DOM XML to console 
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			//TransformerFactory factory = TransformerFactory.newInstance();
			//Transformer transformer = factory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource source = new DOMSource(doc3);
			
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Public\\ClassifiedList.xml");
			StreamResult target = new StreamResult(fos);
			transformer.transform(source, target);
			
			fos.flush();
			fos.close();
			
			
			//StreamResult result = new StreamResult(new File("C:\\Users\\Public\\ClassifiedList.xml"));//share\\
			//StreamResult console = new StreamResult(System.out);
			//transformer.transform(source, console);
			//transformer.transform(source, result);
			
			System.out.println("-------------------------------");
			System.out.println("ClassifiedList.xml 파일 저장 완료");
			Grouping.Grouping_method();
		
		} catch (Exception e) {
			e.getMessage();
			e.toString();
			e.printStackTrace();
		}
	}
		
	private static Node getCompany(Document doc, String num, String content) {
		Element company = doc.createElement("Doc");
		//company.setAttribute("id", id);
		company.appendChild(getCompanyElements(doc, company, "Num", num));
		company.appendChild(getCompanyElements(doc, company, "Review", content));
		return company;
	}

	// utility method to create text node
	private static Node getCompanyElements(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
}
