package com.kotudyprj;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.kotudyprj.dao.IRegisterDao;
import com.kotudyprj.dao.IVocabularyNoteDao;
import com.kotudyprj.dao.IWordsDao;
import com.kotudyprj.dto.RegisterDto;
import com.kotudyprj.dto.VocabularyNoteDto;
import com.kotudyprj.dto.WordItemDto;
import com.kotudyprj.dto.WordSenseDto;
import com.kotudyprj.dto.WordsDto;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MainController {

	@Autowired // dao 빈에 등록
	IRegisterDao iRegisterDao;

	@Autowired
	IWordsDao iWordsDao;

	@Autowired
	IVocabularyNoteDao iVocabularyNoteDao;

	@RequestMapping("/")
	public String root() throws Exception {

		return "";
	}

	HttpSession loginId;

	@PostMapping("/register")
	public String register(@RequestBody RegisterDto registerDto) {
		iRegisterDao.registerDao(registerDto.getUserName(), registerDto.getBirth(), registerDto.getWork(),
				registerDto.getUserId(), registerDto.getUserPassword());
		return registerDto.toString();

	}

	@PostMapping("/login")
	public String login(@RequestBody RegisterDto loginDto, HttpServletRequest req) {
		String check = iRegisterDao.loginDao(loginDto.getUserId(), loginDto.getUserPassword());
		if (check == null) {
			System.out.println("로그인 실패");
		} else {
			System.out.println("로그인성공");
			loginId = req.getSession();
			loginId.setAttribute("userId", loginDto.getUserId());

		}
		return loginDto.toString();
	}

	@PostMapping("/logout")
	public Object logout(HttpServletRequest req) {

		loginId.setAttribute("userId", null);

		Object a = loginId.getAttribute("userId");
		return a;
	}
	

	@GetMapping("/checkUser")
	public Object getInfo() {

		Object a = loginId.getAttribute("userId");

		if (a == null) {

			org.apache.tomcat.jni.Error.osError();
		}
		return a;
	}

	@GetMapping("/dailyWords")
	public List<WordsDto> dailyWords(WordsDto wordsDto) {
		List<WordsDto> list = new ArrayList<>();
		list = iWordsDao.selectWordsDao();
		return list;

	}

	@PostMapping("/paraphraseCheck")
	public void test(@RequestBody String argument) {
		String openApiURL = "http://localhost:8080/paraphraseCheck";
		String accessKey = "2c349c2b-b687-40ae-bf44-6683c48031f4"; // 발급받은 API Key

		// String type = "안녕하세요"; // 분석할 문단 데이터
		// String question = "안녕하세요";

		Gson gson = new Gson();

		Map<String, Object> request = new HashMap<>();
		// Map<String, String> argument = new HashMap<>();

		/*
		 * argument.put("question", question);
		 */
		// argument.put("setence1", setence1);

		request.put("access_key", accessKey);
		request.put("argument", argument);
		System.out.println("request" + request);

		// System.out.println(argument);
		System.out.println(argument);

		URL url;
		Integer responseCode = null;
		String responBody = null;

		try {
			url = new URL(openApiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			// con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			InputStream is = con.getInputStream();
			byte[] buffer = new byte[is.available()];
			int byteRead = is.read(buffer);
			responBody = new String(buffer);

			System.out.println("[responseCode] " + responseCode);
			System.out.println("[responBody]");
			System.out.println(responBody);
			System.out.print("성공");
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			System.out.println("실패1");
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("실패2");
		}
	}

	@GetMapping("/oneWord")
	public List<WordItemDto> oneWord(@RequestParam String q) {

		List<WordItemDto> wordItemDtos = new ArrayList<>();

		BufferedReader brWord = null;
		// DocumentBuilderFactory 생성
		DocumentBuilderFactory factoryWord = DocumentBuilderFactory.newInstance();
		factoryWord.setNamespaceAware(true);
		DocumentBuilder builderWord;
		Document docWord = null;
		try {
			// OpenApi호출
			String word = null; // example 검색을 위한 word
			String urlStrWord = "https://krdict.korean.go.kr/api/search?" + "key=7D322367B7327AAC82C9103718069DB4" // 인증키
					+ "&q=" + q; // 검색 키워드
			URL urlWord = new URL(urlStrWord);
			HttpURLConnection urlconnectionWord = (HttpURLConnection) urlWord.openConnection();

			// 응답 읽기
			brWord = new BufferedReader(new InputStreamReader(urlconnectionWord.getInputStream(), "UTF-8"));
			String resultWord = "";
			String lineWord;
			while ((lineWord = brWord.readLine()) != null) {
				resultWord = resultWord + lineWord.trim();// result = URL로 XML을 읽은 값
				// System.out.println(line);
			}

			// xml 파싱하기
			InputSource isWord = new InputSource(new StringReader(resultWord)); // 받아온 api 결과넣어줌
			builderWord = factoryWord.newDocumentBuilder();
			docWord = builderWord.parse(isWord);
			XPathFactory xpathFactoryWord = XPathFactory.newInstance();
			XPath xpathWord = xpathFactoryWord.newXPath();
			XPathExpression exprWord = xpathWord.compile("/channel/item"); // xpath의 문법대로 가져온다
			NodeList nodeListWord = (NodeList) exprWord.evaluate(docWord, XPathConstants.NODESET);
			for (int i = 0; i < nodeListWord.getLength(); i++) {
				NodeList childWord = nodeListWord.item(i).getChildNodes();
				WordItemDto wordItemDto = new WordItemDto();
				List<WordSenseDto> wordSenseDtos = new ArrayList<>();
				for (int j = 0; j < childWord.getLength(); j++) {
					Node nodeWord = childWord.item(j);
					if (nodeWord.getNodeName() == "target_code") {
						String target_codeString = nodeWord.getTextContent().toString();
						int target_codeInt = Integer.parseInt(target_codeString);
						wordItemDto.setTarget_code(target_codeInt);
					} else if (nodeWord.getNodeName() == "word") {
						wordItemDto.setWord(nodeWord.getTextContent());
						word = nodeWord.getTextContent();
					} else if (nodeWord.getNodeName() == "pronunciation") {
						wordItemDto.setPronunciation(nodeWord.getTextContent());
					} else if (nodeWord.getNodeName() == "pos") {
						wordItemDto.setPos(nodeWord.getTextContent());
					} else if (nodeWord.getNodeName() == "sense") {
						WordSenseDto wordSenseDto = new WordSenseDto();
						StringBuilder definition = new StringBuilder();

						for (int h = 1; h < nodeWord.getTextContent().length(); h++) {
							definition.append(nodeWord.getTextContent().charAt(h));
						}
						/***********************************************************************************************/

						/***********************************************************************************************/
						wordSenseDto.setSense_order(nodeWord.getTextContent().charAt(0) - 48); // Char to Integer ->
																								// ASCII 48 빼준다
						wordSenseDto.setDefinition(definition.toString());
						wordSenseDtos.add(wordSenseDto);
						wordItemDto.setSense(wordSenseDtos);
					}
				}
				wordItemDtos.add(wordItemDto);
			}
			return wordItemDtos;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return wordItemDtos;

	}
	
	
	
	@GetMapping("/myPage")
	public List<String> myPage() {
		List<String> vocabularyList= new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		vocabularyList = iVocabularyNoteDao.showWord(userId);
		
		return vocabularyList;
	}
	
	@GetMapping("/addToNote")
	public void addToNote(@RequestParam String q) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		
		iVocabularyNoteDao.addWord(userId, q);
	}
	
	@GetMapping("/deleteFromNote")
	public List<String> deleteFromNote(@RequestParam String word) {
		List<String> vocabularyList= new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		
		iVocabularyNoteDao.deleteWord(userId, word);

		vocabularyList = iVocabularyNoteDao.showWord(userId);

		return vocabularyList;
	}
}
