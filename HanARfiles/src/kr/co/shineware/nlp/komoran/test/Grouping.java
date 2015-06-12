package kr.co.shineware.nlp.komoran.test;

import java.io.File;
import java.io.FileOutputStream;
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

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.nlp.komoran.modeler.builder.ModelBuilder;
import kr.co.shineware.util.common.model.Pair;

//import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
//import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;
//import kr.ac.kaist.swrc.jhannanum.demo.WorkflowHmmPosTagger;



public class Grouping{
	public static void Grouping_method() {
		try {
			
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(new File("C:\\Users\\Public\\ClassifiedList.xml"));
			doc.getDocumentElement().normalize();
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
			//ArrayList<String> rwList = new ArrayList<String>();
			//ArrayList<String> sbList = new ArrayList<String>();
			ArrayList<String> realList = new ArrayList<String>();
			//Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_HMM_POS_TAGGER);
			/* Activate the work flow in the thread mode */
			//workflow.activateWorkflow(true);
			
			//--------------------------
			
			ModelBuilder builder = new ModelBuilder();
			builder.buildPath("corpus_build");
			builder.save("models");
			
			
			//생성된 models를 이용하여 객체 생성
			Komoran komoran = new Komoran("models");
			//사용자 사전 추가
			komoran.setUserDic("user_data/dic.user");
			
			//String [] word;
			//String realWord = "";
			ArrayList<String> tempList = new ArrayList<String>();
			String couReview[] = new String[3000];
			//----------------------
			
			int t=0;
			int tempInt = 0;
			for (int r = 0; r < reviewDt.size(); r++) {
				HashMap<String, Integer> revMap = new HashMap<String, Integer>();
				/* Analysis using the work flow */
				
				String in = reviewDt.get(r);
				List<List<Pair<String, String>>> analyzeResultList = komoran.analyze(in);
				for (List<Pair<String, String>> wordResultList : analyzeResultList) {
					for(int i=0;i<wordResultList.size();i++){
						Pair<String, String> pair = wordResultList.get(i);
						
						String tempFirst = (String)pair.getFirst();
						String tempSecond = (String)pair.getSecond();
						if(tempSecond.matches(".*NNG.*")||tempSecond.matches(".*NA.*")||tempSecond.matches(".*NNP.*")||tempSecond.matches(".*NNB.*")||tempSecond.matches(".*NP.*")||tempSecond.matches(".*NG.*")||tempSecond.matches(".*NB.*")){
							
				
							//tempList.add(tempFirst);
							
							/*
							for(int e = 0; e < tempList.size(); e++){
								String temp2 = (String)tempList.get(e);
								if(temp2 == tempFirst){
									tempInt = tempInt + 1;
								}
							}
							if(tempInt == 1)
							{
								if(tempFirst.length()>1)
								{
									realList.add(tempFirst);
								}
							}
							*/
							if(revMap.containsKey(tempFirst)){
								
							} else {
								revMap.put(tempFirst,1);
							}
							
						
					}
							tempInt = 0;
				}
			}
					
			
				//tempList.clear();
				Iterator<String> a = revMap.keySet().iterator();
				while( a.hasNext() ) {
					String tempKey = a.next();
					realList.add(tempKey);
					for(t = 0; t < realList.size(); t++){
						String k = realList.get(t);
						if(k.contentEquals(tempKey)){
							if(couReview[t] == null)
							{
							couReview[t] = (Integer.toString(r));
							}
							else
							{
							couReview[t] = (couReview[t]+","+Integer.toString(r));
							}
						}
					}
				}
				t=0;
			}
					/*
				
				
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
				
				
				
				
			}
			*/
			//workflow.close();
			//String example = "[한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 처리(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 예시(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]";
			//String example2 = "[한국어(Noun: 0, 3), 를(Josa: 3, 1),  (Space: 4, 1), 한국어(Noun: 5, 2), 하는(Verb: 7, 2),  (Space: 9, 1), 성공(Noun: 10, 2), 입니(Adjective: 12, 2), 다(Eomi: 14, 1), ㅋㅋ(KoreanParticle: 15, 2),  (Space: 17, 1), #한국어(Hashtag: 18, 4)]";
			//rwList.add(example);
			//rwList.add(example2);

			

			
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

			/*
			String [] word;
			for(int x = 0; x<realList.size();x++) {
				System.out.print("키워드 : \'"+realList.get(x)+"\'");
				subject.add(realList.get(x));
				String spList = couReview[x];
				word = spList.split(",");
				System.out.println("의 개수는 : "+word.length);
				subCount.add(word.length);
				word = null;
			}

			*/
			
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
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Public\\KeywordList.xml");
			StreamResult target = new StreamResult(fos);
			transformer.transform(source, target);
			fos.flush();
			fos.close();
					
			//StreamResult result = new StreamResult(new File("C:\\Users\\Public\\KeywordList.xml"));//share\\
			//StreamResult console = new StreamResult(System.out);
			//transformer.transform(source, console);
			//transformer.transform(source, result);
			
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
			String [] word2;
			for(int rank = countList.length-1; rank > countList.length-11; rank--) {
				System.out.println("-------------------------------------------------");
				System.out.print((countList.length-rank)+"순위의 주제어는 "+subjectList[rank]);
				System.out.println("이고 "+countList[rank]+"번 등장했습니다.");
				System.out.println("해당 리뷰들");
				
				for(int x = 0; x<realList.size();x++) {
					if(realList.get(x).contentEquals(subjectList[rank])){
						String spList = couReview[x];
						word2 = spList.split(",");
					
						for(int k=0;k<word2.length;k++)
						{
							System.out.println((k+1)+". "+(Integer.parseInt(word2[k])+1)+"번째 리뷰 ,"+reviewDt.get(Integer.parseInt(word2[k])));
						}
						break;
					}
					word2 = null;
				}
				System.out.println("-------------------------------------------------");
			}
				
				
			

				
			
		}catch (Exception e) {
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

