package hanar;

import java.io.File;
import java.util.*;

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

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;
//import kr.ac.kaist.swrc.jhannanum.demo.WorkflowHmmPosTagger;

public class Grouping{

	public static void Grouping_method() {
		try {
			System.out.println("Starting Grouping");
			File file = new File("C:\\Users\\Public\\ClassifiedList.xml"); //share\\
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Starting Grouping2");
			NodeList xmlList = doc.getElementsByTagName("Doc");
			
			ArrayList<String> reviewDt = new ArrayList<String>();
			
			for (int i = 0; i < xmlList.getLength(); i++) {
				Node xmlNode = xmlList.item(i);
 
				if (xmlNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element xmlElmnt = (Element) xmlNode;
 
					NodeList reviewList= xmlElmnt.getElementsByTagName("Review");
					Element reviewElmnt = (Element) reviewList.item(0);
					Node review = reviewElmnt.getFirstChild();
					
					reviewDt.add(review.getNodeValue());

				}
 
			}
			System.out.println("Starting Grouping3");
			ArrayList<String> rwList = new ArrayList<String>();
			ArrayList<String> sbList = new ArrayList<String>();
			ArrayList<String> realList = new ArrayList<String>();
			System.out.println("Starting Grouping4");
			Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_HMM_POS_TAGGER);
			System.out.println("Starting 5");
			/* Activate the work flow in the thread mode */
			workflow.activateWorkflow(true);
			System.out.println("Starting sdsdsdsd");
			for (int r = 0; r < reviewDt.size(); r++) {
								
				/* Analysis using the work flow */
				String document = reviewDt.get(r);
				workflow.analyze(document);
				String document2 = workflow.getResultOfDocument();
				
				rwList.add(document2);
				
			}
			workflow.close();
			//String example = "[한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]";
			//String example2 = "[한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 한국어(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 성공(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]";
			//rwList.add(example);
			//rwList.add(example2);
			System.out.println("Starting sdsdsdsd");
			String [] word;
			String realWord = "";
			ArrayList<String> tempList = new ArrayList<String>();
			
			for(int i=0;i<rwList.size(); i++)
			{
				String [] spWord;
				spWord = rwList.get(i).split("\n");
				for(int k=0;k<spWord.length;k++) {
					if(k%3 == 1){
						tempList.add(spWord[k]);
					}
				}
				
				for(int q=0;q<tempList.size();q++)
				{
					String spList = tempList.get(q);
					word = spList.split("\\+");
					for(int k=0;k<word.length;k++)
					{
						if(word[k].matches(".*ncn.*") || word[k].matches(".*ncpa.*"))
						{
							word[k] = word[k].replaceAll("\\/", ""); 
							word[k] = word[k].replaceAll("ncn", "");
							word[k] = word[k].replaceAll("\\+", "");
							word[k] = word[k].replaceAll("ncpa", "");
							word[k] = word[k].replaceAll("\\,", "");
							word[k] = word[k].replaceAll("\\s", "");
							realWord += word[k];
						}
					}
					if(realWord != "") {
						boolean overlap = false;
						for(int t = 0;t<sbList.size(); t++)
						{
							if(sbList.get(t).matches(".*"+realWord+".*")) {
								overlap = true;								
							}
						}
						if(overlap == false) {
						sbList.add(realWord);
						}
					} realWord = "";
				}
				for(int r=0; r<sbList.size();r++)
				{
				realList.add(sbList.get(r));
				}
				spWord = null;
				sbList.clear();
				tempList.clear();

			}
			
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			
			//for(int z=0;z<sbList.size(); z++) {
			//	String [] tempWord = sbList.get(z);
			//	for(int x=0; x<tempWord.length; x++) {
			for(int x = 0; x<realList.size();x++) {
				String tempWord = realList.get(x);
					//if(tempWord[x]==null) {
					//} else 
				if(map.containsKey(tempWord)){
						int a = map.get(tempWord);
						a++;
						map.put(tempWord,a);
					} else {
						map.put(tempWord,1);
					}
				}
			//}
			
			ArrayList<String> subject = new ArrayList<String>();
			ArrayList<Integer> subCount = new ArrayList<Integer>();
			Iterator<String> i = map.keySet().iterator(); // Iterator 선언 
			String tempKey;
			
			for(; i.hasNext(); ) {
				tempKey = i.next();
				subject.add(tempKey);
				subCount.add(map.get(tempKey));
				System.out.print("키워드 : \'"+tempKey+"\'");
				System.out.println("의 개수는 : "+map.get(tempKey));
			}
						
			DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder icBuilder;
			icBuilder = icFactory.newDocumentBuilder();
		     
			Document doc3 = icBuilder.newDocument();
			Element mainRootElement = doc3.createElementNS(null, "KeywordList");
			doc3.appendChild(mainRootElement);
			
			for (int n = 0; n < subject.size(); n++) {
				String temSub = subject.get(n);
				String temCou = String.valueOf(subCount.get(n));
				mainRootElement.appendChild(getCompany(doc3, temCou, temSub));
			}

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource source = new DOMSource(doc3);
			StreamResult result = new StreamResult(new File("C:\\Users\\Public\\KeywordList.xml"));//share\\
			StreamResult console = new StreamResult(System.out);
			transformer.transform(source, console);
			transformer.transform(source, result);
			
			System.out.println("-------------------------------");
			System.out.println("KeywordList.xml 파일 저장 완료");
			
			String[] subjectList = new String[subject.size()]; 
			subject.toArray(subjectList);
			
			Integer[] countList = new Integer[subCount.size()]; 
			subCount.toArray(countList);

			for(int index = 1; index < countList.length; index++){
				int temp = countList[index];
				String tempString = subjectList[index];
				int aux = index - 1;
				
				while( (aux >= 0) && (countList[aux] > temp) ) {
					countList[aux+1] = countList[aux];
					subjectList[aux+1] = subjectList[aux];
					aux--;
			       	}
			       	countList[aux + 1] = temp;
			       	subjectList[aux + 1] = tempString;
			}
			
			String[] reviewList = new String[reviewDt.size()]; 
			reviewDt.toArray(reviewList);
			
			for(int rank = countList.length-1; rank > countList.length-11; rank--) {
				System.out.print((countList.length-rank)+"순위의 주제어는 "+subjectList[rank]);
				System.out.println("이고 "+countList[rank]+"번 등장했습니다.");
				System.out.println("해당 리뷰 내용 :");
				for (int s = 0; s < reviewList.length; s++) {
					if(reviewList[s].matches(".*"+subjectList[rank]+".*")) {
						System.out.println(reviewList[s]+", "+s+"번째 리뷰");
					}
					
				}
			}

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	private static Node getCompany(Document doc, String num, String content) {
		Element company = doc.createElement("Keyword");
		//company.setAttribute("id", id);
		company.appendChild(getCompanyElements(doc, company, "Subject", content));
		company.appendChild(getCompanyElements(doc, company, "Counts", num));
		return company;
	}

	// utility method to create text node
	private static Node getCompanyElements(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
	
}

