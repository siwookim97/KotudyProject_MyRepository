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
import com.kotudyprj.dao.IKakaoDao;
import com.kotudyprj.dao.IRegisterDao;
import com.kotudyprj.dao.IVocabularyNoteDao;
import com.kotudyprj.dao.IWordsDao;
import com.kotudyprj.dto.KakaoDto;
import com.kotudyprj.dto.QuizTemplateDto;
import com.kotudyprj.dto.RegisterDto;
import com.kotudyprj.dto.VocabularyNoteDto;
import com.kotudyprj.dto.WordItemDto;
import com.kotudyprj.dto.WordSenseDto;
import com.kotudyprj.dto.WordsDto;
import com.kotudyprj.service.KakaoAPI;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MainController {

	HttpSession loginId; // 로그인 세션 저장위한 변수

	@Autowired // dao 빈에 등록
	IRegisterDao iRegisterDao;

	@Autowired
	IWordsDao iWordsDao;

	@Autowired
	IVocabularyNoteDao iVocabularyNoteDao;

	@Autowired
	private KakaoAPI kakaoAPI;

	@Autowired
	IKakaoDao iKakaoDao;

	static public class Morpheme {
		final String text;
		final String type;
		Integer count;

		public Morpheme(String text, String type, Integer count) {
			this.text = text;
			this.type = type;
			this.count = count;
		}
	}

	static public class NameEntity {
		final String text;
		final String type;
		Integer count;

		public NameEntity(String text, String type, Integer count) {
			this.text = text;
			this.type = type;
			this.count = count;
		}
	}

	@RequestMapping("/")
	public String root() throws Exception {

		return "";
	}

	// 카카오 로그인
	@GetMapping("/kakaoAuth")
	public Object kakaoLogin(@RequestParam String code, HttpServletRequest req, KakaoDto kakaoDto) {

		// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
		HttpSession session = req.getSession(true);
		System.out.println("code " + code);
		String access_Token = kakaoAPI.getAccessToken(code);
		HashMap<String, Object> userInfo = kakaoAPI.getUserInfo(access_Token);
		System.out.println("login Controller : " + userInfo);

		if (userInfo.get("email") != null) {

			kakaoDto.setUserId(userInfo.get("email"));
			iKakaoDao.registerDao(kakaoDto.getUserId());
			List check = iKakaoDao.loginDao(kakaoDto.getUserId());
			System.out.println("카톡 아이디" + check);
			loginId = req.getSession();
			loginId.setAttribute("userId", kakaoDto.getUserId());
		}
		return userInfo;
	}

	// 카카오 로그아웃
	@PostMapping("/kakaoLogout")
	public String logout(HttpServletRequest req) {

		loginId.removeAttribute("userId");
		return "index";
	}

	// 로그인 세션 유지
	@GetMapping("/getInfo")
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

	/*
	 * @PostMapping("/paraphraseCheck") public void test(@RequestBody String
	 * argument) { String openApiURL = "http://localhost:8080/paraphraseCheck";
	 * String accessKey = "2c349c2b-b687-40ae-bf44-6683c48031f4"; // 발급받은 API Key
	 * 
	 * Gson gson = new Gson();
	 * 
	 * Map<String, Object> request = new HashMap<>();
	 * 
	 * request.put("access_key", accessKey); request.put("argument", argument);
	 * System.out.println("request" + request);
	 * 
	 * System.out.println(argument);
	 * 
	 * URL url; Integer responseCode = null; String responBody = null;
	 * 
	 * try { url = new URL(openApiURL); HttpURLConnection con = (HttpURLConnection)
	 * url.openConnection(); con.setDoOutput(true);
	 * 
	 * DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	 * wr.write(gson.toJson(request).getBytes("UTF-8")); wr.flush(); wr.close();
	 * 
	 * responseCode = con.getResponseCode(); InputStream is = con.getInputStream();
	 * byte[] buffer = new byte[is.available()]; int byteRead = is.read(buffer);
	 * responBody = new String(buffer);
	 * 
	 * System.out.println("[responseCode] " + responseCode);
	 * System.out.println("[responBody]"); System.out.println(responBody);
	 * System.out.print("성공"); } catch (MalformedURLException e) { //
	 * e.printStackTrace(); System.out.println("실패1"); } catch (IOException e) { //
	 * e.printStackTrace(); System.out.println("실패2"); } }
	 */

	// 단어검색
	@PostMapping("/searchWord")
	public List<String> paraphraseCheck(@RequestBody Map<String, String> body) {

		List<String> finalDtoList = new ArrayList<>();
		// FinalDto finalDto = null;
		String openApiURL = "http://aiopen.etri.re.kr:8000/WiseNLU";
		String accessKey = "16738d75-2241-45a6-8c0d-0b06580f2a65"; // 발급받은 API Key
		String analysisCode = "ner"; // 언어 분석 코드
		String text = ""; // 분석할 텍스트 데이터
		Gson gson = new Gson();

		Map<String, Object> request = new HashMap<>();
		Map<String, String> argument = new HashMap<>();

		argument.put("analysis_code", body.get("analysisCode"));
		argument.put("text", body.get("text"));

		request.put("access_key", accessKey);
		request.put("argument", argument);
		System.out.println("argument:" + argument);

		URL url;
		Integer responseCode = null;
		String responBodyJson = null;
		Map<String, Object> responeBody = null;

		try {
			url = new URL(openApiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			InputStream is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();

			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			responBodyJson = sb.toString();

			// http 요청 오류 시 처리
			if (responseCode != 200) {
				// 오류 내용 출력
				System.out.println("[error] " + responBodyJson);

			}

			responeBody = gson.fromJson(responBodyJson, Map.class);
			Integer result = ((Double) responeBody.get("result")).intValue();
			Map<String, Object> returnObject;
			List<Map> sentences;

			// 분석 요청 오류 시 처리
			if (result != 0) {

				// 오류 내용 출력
				System.out.println("[error] " + responeBody.get("result"));

			}

			// 분석 결과 활용
			returnObject = (Map<String, Object>) responeBody.get("return_object");
			sentences = (List<Map>) returnObject.get("sentence");

			Map<String, Morpheme> morphemesMap = new HashMap<String, Morpheme>();
			Map<String, NameEntity> nameEntitiesMap = new HashMap<String, NameEntity>();
			List<Morpheme> morphemes = null;
			List<NameEntity> nameEntities = null;

			for (Map<String, Object> sentence : sentences) {
				// 형태소 분석기 결과 수집 및 정렬
				List<Map<String, Object>> morphologicalAnalysisResult = (List<Map<String, Object>>) sentence
						.get("morp");

				for (Map<String, Object> morphemeInfo : morphologicalAnalysisResult) {
					String lemma = (String) morphemeInfo.get("lemma");
					Morpheme morpheme = morphemesMap.get(lemma);
					if (morpheme == null) {
						morpheme = new Morpheme(lemma, (String) morphemeInfo.get("type"), 1);
						morphemesMap.put(lemma, morpheme);
					} else {
						morpheme.count = morpheme.count + 1;
					}
				}

				// 개체명 분석 결과 수집 및 정렬
				List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence.get("NE");
				for (Map<String, Object> nameEntityInfo : nameEntityRecognitionResult) {
					String name = (String) nameEntityInfo.get("text");
					NameEntity nameEntity = nameEntitiesMap.get(name);
					if (nameEntity == null) {
						nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
						nameEntitiesMap.put(name, nameEntity);
					} else {
						nameEntity.count = nameEntity.count + 1;
					}
				}
			}

			if (0 < morphemesMap.size()) {
				morphemes = new ArrayList<Morpheme>(morphemesMap.values());
				morphemes.sort((morpheme1, morpheme2) -> {
					return morpheme2.count - morpheme1.count;
				});
			}

			if (0 < nameEntitiesMap.size()) {
				nameEntities = new ArrayList<NameEntity>(nameEntitiesMap.values());
				nameEntities.sort((nameEntity1, nameEntity2) -> {
					return nameEntity2.count - nameEntity1.count;
				});
			}

			// 형태소들 중 명사들에 대해서 많이 노출된 순으로 출력 ( 최대 5개 )
			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("NNG") || morpheme.type.equals("NNP") || morpheme.type.equals("NNB");
			}).limit(5).forEach(morpheme -> {

				System.out.println("[명사] " + morpheme.text + " (" + morpheme.count + ")");

				// String part = "동사";
				// finalDto.setPart(part);
				// finalDto.setWord(morpheme.text);
				finalDtoList.add("명사");
				finalDtoList.add(morpheme.text);

				return;
			});

			// 형태소들 중 동사들에 대해서 많이 노출된 순으로 출력 ( 최대 5개 )
			System.out.println("");
			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("VV");
			}).limit(5).forEach(morpheme -> {
				System.out.println("[동사] " + morpheme.text + " (" + morpheme.count + ")");
				// finalDto.setPart(inputLine("동사"));
				// finalDto.setWord(morpheme.text);
				// finalDtoList.add(finalDto);
				finalDtoList.add("동사");
				finalDtoList.add(morpheme.text);
				return;
			});

			// 인식된 개채명들 많이 노출된 순으로 출력 ( 최대 5개 )

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(finalDtoList);
		return finalDtoList;
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
		List<String> vocabularyList = new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		vocabularyList = iVocabularyNoteDao.showWord(userId);

		return vocabularyList;
	}

	// 단어장에 단어 추가
	@GetMapping("/addToNote")
	public void addToNote(@RequestParam String q, @RequestParam String p) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		System.out.println("userId : " + userId);
		System.out.println("q : " + q);
		System.out.println("mean : " + p);

		// 단어장에 단어가 이미 있는지 확인
		if (iVocabularyNoteDao.checkWord(userId, q) == 0) {
			iVocabularyNoteDao.addWord(userId, q, p);
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		} else {
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		}
	}

	// 단어장에서 단어 삭제
	@GetMapping("/deleteFromNote")
	public List<String> deleteFromNote(@RequestParam String word) {
		List<String> vocabularyList = new ArrayList<>();
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		iVocabularyNoteDao.deleteWord(userId, word);

		vocabularyList = iVocabularyNoteDao.showWord(userId);

		return vocabularyList;
	}

	// 퀴즈로 단어 10개 보내기
	@GetMapping("/wordQuiz")
	public List<QuizTemplateDto> wordQuiz() {
		List<QuizTemplateDto> quizTemplateList = new ArrayList<>();
		QuizTemplateDto quizTemplate = new QuizTemplateDto();

		// Object sessionId = loginId.getAttribute("userid");
		// String userId = sessionId.toString();

		List<VocabularyNoteDto> vocabularyNoteList = null;
		vocabularyNoteList = iVocabularyNoteDao.getVocabularynote();

		for (int n = 0; n < 40; n++) {
		//	System.out.println("WORD " + n + " : " + vocabularyNoteList.get(n).getWord());
		//	System.out.println("MEAN " + n + " : " + vocabularyNoteList.get(n).getMean());
			if (n % 4 == 0) {
				quizTemplate = new QuizTemplateDto();
				quizTemplate.setWord(vocabularyNoteList.get(n).getWord());
				quizTemplate.setWord_mean(vocabularyNoteList.get(n).getMean());
			} else if (n % 4 == 1) {
				quizTemplate.setWrong_answer1(vocabularyNoteList.get(n).getMean());
			} else if (n % 4 == 2) {
				quizTemplate.setWrong_answer2(vocabularyNoteList.get(n).getMean());
			} else {
				quizTemplate.setWrong_answer3(vocabularyNoteList.get(n).getMean());
				quizTemplateList.add(quizTemplate);
			}
			
		}
		
		System.out.println(quizTemplateList);

		return quizTemplateList;
	}

	// 단어 추가횟수 랭킹
	@PostMapping("/wordRank")
	public List<Object> wordRank() {
		List<Object> wordRankList = new ArrayList<>();
		System.out.println("wordRank 호출");

		wordRankList.add(iVocabularyNoteDao.wordRankWord());
		wordRankList.add(iVocabularyNoteDao.wordRankCount());

		return wordRankList;
	}

}