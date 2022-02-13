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
import com.kotudyprj.dao.IUserRankingDao;
import com.kotudyprj.dao.IVocabularyNoteDao;
import com.kotudyprj.dao.IWordRankingDao;
import com.kotudyprj.dao.IWordsDao;
import com.kotudyprj.dto.KakaoDto;
import com.kotudyprj.dto.QuizTemplateDto;
import com.kotudyprj.dto.VocabularyNoteDto;
import com.kotudyprj.dto.WordItemDto;
import com.kotudyprj.dto.WordSenseDto;
import com.kotudyprj.dto.WordsDto;
import com.kotudyprj.service.KakaoAPI;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class MainController {

	@Autowired
	IWordsDao iWordsDao;

	@Autowired
	IVocabularyNoteDao iVocabularyNoteDao;

	@Autowired
	IKakaoDao iKakaoDao;

	@Autowired
	KakaoAPI kakaoAPI;

	@Autowired
	IUserRankingDao iUserRankingDao;

	@Autowired
	IWordRankingDao iWordRankingDao;

	HttpSession loginId;

	@RequestMapping("/")
	public String root() throws Exception {

		return "";
	}

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

	@GetMapping("/kakaoAuth")
	public Object kakaoLogin(@RequestParam String code, HttpServletRequest req, KakaoDto kakaoDto) {

		// 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
		HttpSession session = req.getSession(true);
		String access_Token = kakaoAPI.getAccessToken(code);
		HashMap<String, Object> userInfo = kakaoAPI.getUserInfo(access_Token);
		System.out.println("login Controller : " + userInfo);

		if (userInfo.get("email") != null) {

			kakaoDto.setUserId(userInfo.get("email"));
			kakaoDto.setNickName(userInfo.get("nickName"));
			kakaoDto.setImage(userInfo.get("image"));
			iKakaoDao.registerDao(kakaoDto.getUserId(), kakaoDto.getNickName(), kakaoDto.getImage());
			List check = iKakaoDao.loginDao(kakaoDto.getUserId());
			loginId = req.getSession();
			loginId.setAttribute("userId", kakaoDto.getUserId());

		}

		return userInfo;
	}

	@GetMapping("/getInfo")
	public Object getInfo() {

		Object a = loginId.getAttribute("userId");
		if (a == null) {

			org.apache.tomcat.jni.Error.osError();

		}

		return a;

	}

	@PostMapping("/kakaoLogout")
	public String logout() {

		loginId.removeAttribute("userId");
		return "index";
	}

	@GetMapping("/dailyWords")
	public List<WordsDto> dailyWords(WordsDto wordsDto) {
		List<WordsDto> list = new ArrayList<>();
		list = iWordsDao.selectWordsDao();
		return list;

	}

	@PostMapping("/searchWord")
	public List<String> paraphraseCheck2(@RequestBody Map<String, String> body) {

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
				return morpheme.type.equals("NNG") || morpheme.type.equals("NNB");
			}).limit(5).forEach(morpheme -> {

				System.out.println("[명사] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("명사");
				finalDtoList.add(morpheme.text);

				return;
			});
			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("NNP");
			}).limit(5).forEach(morpheme -> {

				System.out.println("[고유명사] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("고유명사");
				finalDtoList.add(morpheme.text);

				return;
			});

			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("NP");
			}).limit(5).forEach(morpheme -> {

				System.out.println("[대명사] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("대명사");
				finalDtoList.add(morpheme.text);

				return;
			});

			// 형태소들 중 동사들에 대해서 많이 노출된 순으로 출력 ( 최대 5개 )

			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("VV");
			}).limit(5).forEach(morpheme -> {
				System.out.println("[동사] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("동사");
				finalDtoList.add(morpheme.text);
				return;
			});

			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("MM") || morpheme.type.equals("MAG") || morpheme.type.equals("MAJ");
			}).limit(5).forEach(morpheme -> {
				System.out.println("[수식언] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("관형사");
				finalDtoList.add(morpheme.text);
				return;
			});

			morphemes.stream().filter(morpheme -> {
				return morpheme.type.equals("JKS") || morpheme.type.equals("JKC") || morpheme.type.equals("JKG")
						|| morpheme.type.equals("JKO") || morpheme.type.equals("JKB") || morpheme.type.equals("JKV")
						|| morpheme.type.equals("JKQ") || morpheme.type.equals("JX") || morpheme.type.equals("JC");
			}).limit(5).forEach(morpheme -> {
				System.out.println("[조사] " + morpheme.text + " (" + morpheme.count + ")");

				finalDtoList.add("조사");
				finalDtoList.add(morpheme.text);
				return;
			});

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

			System.out.println("======== 한국어 기초사전 API 호출 ========");

			String word = null; // example 검색을 위한 word
			String urlStrWord = "https://krdict.korean.go.kr/api/search?" + "key=FAFF5405FEE6910E824515B8B9A2BA08" // 인증키
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

	@GetMapping("/addToNote")
	public void addToNote(@RequestParam String q, @RequestParam String p) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();

		// 단어장에 단어가 이미 있는지 확인
		if (iVocabularyNoteDao.checkWord(userId, q) == 0) {
			iVocabularyNoteDao.addWord(userId, q, p);
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		} else {
			/* 나중에 단어장에 들어갔는지 안들어갔는지 중복값을 프론트에 전달하기 */
		}

		if (iWordRankingDao.wordRankingSelect(q) == 0) {
			iWordRankingDao.wordRankingInsert(q);
			iWordRankingDao.wordRankingUp(q);
		} else {
			iWordRankingDao.wordRankingUp(q);
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

		if (iWordRankingDao.wordRankingSelect(word) == 1) {
			iWordRankingDao.wordRankingDelete(word);
		} else {
			iWordRankingDao.wordRankingDown(word);
		}
		return vocabularyList;
	}

	@GetMapping("/wordQuiz")
	public List<QuizTemplateDto> wordQuiz() {
		List<QuizTemplateDto> quizTemplateList = new ArrayList<>();
		QuizTemplateDto quizTemplate = new QuizTemplateDto();

		// Object sessionId = loginId.getAttribute("userid");
		// String userId = sessionId.toString();

		List<VocabularyNoteDto> vocabularyNoteList = null;
		vocabularyNoteList = iVocabularyNoteDao.getVocabularynote();

		for (int n = 0; n < 40; n++) {
			// System.out.println("WORD " + n + " : " +
			// vocabularyNoteList.get(n).getWord());
			// System.out.println("MEAN " + n + " : " +
			// vocabularyNoteList.get(n).getMean());
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

	// 퀴즈 결과 user_ranking table에 저장하기
	@PostMapping("/getQuizResult")
	public void getQuizResult(@RequestBody Map<String, Integer> score) {
		Object sessionId = loginId.getAttribute("userId");
		String userId = sessionId.toString();
		int point = score.get("score");

		if (iUserRankingDao.selectQuizRanking(userId) == 0) {
			// iUserRankingDao.createRankingInfo(userId);
			iUserRankingDao.getQuizResult(userId, point);
		} else {
			iUserRankingDao.getQuizResult(userId, point);
		}
	}

	// 단어 추가횟수 랭킹
	@PostMapping("/wordRank")
	public List<List<Object>> wordRank() {
		List<List<Object>> wordRankingList = new ArrayList<>();
		List<Object> wordRankingWord = new ArrayList<>();
		List<Object> wordRankingPoint = new ArrayList<>();

		System.out.println("wordRank 호출");

		wordRankingWord.add(iWordRankingDao.wordRankingWord());
		wordRankingList.add(wordRankingWord);
		wordRankingPoint.add(iWordRankingDao.wordRankingPoint());
		wordRankingList.add(wordRankingPoint);

		return wordRankingList;
	}

	@PostMapping("/userRank")
	public List<List<Object>> userRank(KakaoDto kakaoDto) {
		iUserRankingDao.createRankingInfo(kakaoDto.getUserId(), kakaoDto.getImage());

		List<List<Object>> userRankingList = new ArrayList<>();
		List<Object> userRankingUserId = new ArrayList<>();
		List<Object> userRankingNickName = new ArrayList<>();
		List<Object> userRankingImage = new ArrayList<>();
		List<Object> userRankingPoint = new ArrayList<>();

		System.out.println("userRank 호출");

		userRankingUserId.add(iUserRankingDao.userRankingUserId());
		userRankingList.add(userRankingUserId);
		userRankingPoint.add(iUserRankingDao.userRankingPoint());
		userRankingList.add(userRankingPoint);
		System.out.println("point : " + userRankingPoint);
		userRankingImage.add(iUserRankingDao.userRankingImage());
		userRankingList.add(userRankingImage);

		// userRankingUserId.add(iUserRankingDao.userRankingPoint());
		// userRankingList.add(userRankingPoint);

		return userRankingList;
	}

}