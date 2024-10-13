package kr.co.seoulit.logistics.prodcsvc.production.controller;

import java.lang.reflect.Type;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import kr.co.seoulit.logistics.prodcsvc.production.to.MpsTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import org.springframework.kafka.core.KafkaTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import kr.co.seoulit.logistics.prodcsvc.production.service.ProductionService;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpGatheringTO;
import kr.co.seoulit.logistics.prodcsvc.production.to.MrpTO;

@RestController
@RequestMapping("/production/*")
public class MrpController {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	private static final String TOPIC = "kafka-react-logi-acc1"; // 사용할 카프카 토픽 이름

	@Autowired
	private ProductionService productionService;

	ModelMap map = null;

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	@RequestMapping(value="/mrp/list", method=RequestMethod.GET)
	public ModelMap getMrpList(HttpServletRequest request, HttpServletResponse response) {
		String mrpGatheringStatusCondition = request.getParameter("mrpGatheringStatusCondition");
		String dateSearchCondition = request.getParameter("dateSearchCondition");
		String mrpStartDate = request.getParameter("mrpStartDate");
		String mrpEndDate = request.getParameter("mrpEndDate");
		String mrpGatheringNo = request.getParameter("mrpGatheringNo");
		System.out.println("mrpGatheringStatusCondition = " + mrpGatheringStatusCondition);
		map = new ModelMap();
		try {
			ArrayList<MrpTO> mrpList = null;

			if(mrpGatheringStatusCondition != null ) {
				mrpList = productionService.searchMrpList(mrpGatheringStatusCondition);
			} else if (dateSearchCondition != null) {
				mrpList = productionService.selectMrpListAsDate(dateSearchCondition, mrpStartDate, mrpEndDate);  // MRP - MPS 조회 버튼 클릭시
			} else if (mrpGatheringNo != null) {
				mrpList = productionService.searchMrpListAsMrpGatheringNo(mrpGatheringNo);
			}
			map.put("gridRowJson", mrpList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

	//Mrp - Mrp 모의전개
	@RequestMapping(value="/openMrp", method=RequestMethod.GET)
	public ModelMap openMrp(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("request.getParameter(\"mpsNoListStr\") = " + request.getParameter("mpsNoListStr"));
		String mpsNoListStr = "[\"" + request.getParameter("mpsNoListStr") + "\", \"anotherValue\"]";
		map = new ModelMap();
		try {
			ArrayList<String> mpsNoArr = gson.fromJson(mpsNoListStr,
					new TypeToken<ArrayList<String>>() { }.getType());
			HashMap<String, Object> mrpMap = productionService.openMrp(mpsNoArr);

			System.out.println("mrpMap = " + mrpMap);
			map.put("gridRowJson", mrpMap.get("result"));
			map.put("errorCode", mrpMap.get("errorCode"));
			map.put("errorMsg", mrpMap.get("errorMsg"));
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

//	@RequestMapping(value="/mrp/open", method=RequestMethod.GET)
//	public ModelMap openMrp(HttpServletRequest request, HttpServletResponse response) {
//		String mpsNoListStr = request.getParameter("mpsNoList");
//		map = new ModelMap();
//		try {
//			ArrayList<String> mpsNoArr = gson.fromJson(mpsNoListStr,
//					new TypeToken<ArrayList<String>>() { }.getType());
//			HashMap<String, Object> mrpMap = productionService.openMrp(mpsNoArr);
//
//			map.put("gridRowJson", mrpMap.get("result"));
//			map.put("errorCode", mrpMap.get("errorCode"));
//			map.put("errorMsg", mrpMap.get("errorMsg"));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			map.put("errorCode", -1);
//			map.put("errorMsg", e1.getMessage());
//		}
//		return map;
//	}

	//정식 메서드임. 카프카 되면 이거 열어라
    //	MRP 등록되면 여기로 넘어온다!! 가격정보까지 다넘어온다!! 20241008
	@RequestMapping(value = "/mrp", method = RequestMethod.POST)
	public ModelMap registerMrp(HttpServletRequest request, HttpServletResponse response) {
		String batchList = request.getParameter("batchList");
		String mrpRegisterDate = request.getParameter("mrpRegisterDate");
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
		ArrayList<HashMap<String, Object>> dataList = gson.fromJson(batchList, type);

		ArrayList<String> mpsList = new ArrayList<>();
		for (HashMap<String, Object> item : dataList) {
			mpsList.add(gson.toJson(item));
		}
		System.out.println("mpsList = " + mpsList);

		ModelMap map = new ModelMap();
		try {
			// MRP 등록 처리
			HashMap<String, Object> resultMap = productionService.registerMrp(mrpRegisterDate, mpsList);

			// 카프카로 mpsList 전송
			String mpsListJson = gson.toJson(mpsList); // mpsList를 JSON 문자열로 변환
			kafkaTemplate.send(TOPIC, mpsListJson); // 카프카에 메시지 전송
			System.out.println("Sent to Kafka: " + mpsListJson);

			map.put("result", resultMap.get("result"));
			map.put("errorCode", resultMap.get("errorCode"));
			map.put("errorMsg", resultMap.get("errorMsg"));
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

//	// 카프카 테스트용
//	@RequestMapping(value = "/mrp", method = RequestMethod.POST)
//	public ModelMap registerMrp(HttpServletRequest request, HttpServletResponse response) {
//		String batchList = request.getParameter("batchList");
//		Gson gson = new Gson();
//		Type type = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
//		ArrayList<HashMap<String, Object>> dataList = gson.fromJson(batchList, type);
//
//		// mpsList를 직접 JSON 배열로 생성
//		String mpsListJson = gson.toJson(dataList); // 데이터 리스트를 JSON 배열로 변환
//		System.out.println("mpsList = " + mpsListJson); // JSON 배열 출력
//
//		ModelMap map = new ModelMap();
//		try {
//			// 카프카로 mpsListJson 전송
//			kafkaTemplate.send(TOPIC, mpsListJson); // 카프카에 메시지 전송
//			System.out.println("Sent to Kafka: " + mpsListJson);
//
//			map.put("result", "Success");
//			map.put("message", "Data sent to Kafka successfully.");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			map.put("errorCode", -1);
//			map.put("errorMsg", e1.getMessage());
//		}
//		return map;
//	}

	@RequestMapping(value="/mrp/gathering-list", method=RequestMethod.GET)
	public ModelMap getMrpGatheringList(HttpServletRequest request, HttpServletResponse response) {
		String mrpNoList = request.getParameter("mrpNoList");
		System.out.println("mrpNoList = " + mrpNoList);
		String[] mrpNoArray = mrpNoList.split(",");
		ArrayList<String> mrpNoArrayList = new ArrayList<>(Arrays.asList(mrpNoArray));
		map = new ModelMap();
		try {
			ArrayList<MrpGatheringTO> mrpGatheringList = productionService.getMrpGathering(mrpNoArrayList);

			map.put("gridRowJson", mrpGatheringList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}



	@RequestMapping(value="/mrp/gathering", method=RequestMethod.POST)
	public ModelMap registerMrpGathering(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
		String mrpGatheringRegisterDate = request.getParameter("mrpGatheringRegisterDate");
		String mrpNoList = request.getParameter("mrpNoList");
		String mrpNoAndItemCodeList = request.getParameter("mrpNoAndItemCodeList");

		// mrpNoList를 ArrayList<String>으로 변환
		String[] mrpNoArray = mrpNoList.split(",");
		ArrayList<String> mrpNoArrayList = new ArrayList<>(Arrays.asList(mrpNoArray));

		for (String mrpNo : mrpNoArray) {
			mrpNoArrayList.add(mrpNo.trim().replaceAll("^\"|\"$", ""));
		}

		// ObjectMapper 객체 생성
		ObjectMapper objectMapper = new ObjectMapper();

		// mrpNoAndItemCodeList를 HashMap<String, String>으로 변환
		List<HashMap<String, String>> mrpNoAndItemCodeMapList = objectMapper.readValue(mrpNoAndItemCodeList, List.class);

		HashMap<String, String> mrpNoAndItemCodeHashMap = new HashMap<>();
		for (HashMap<String, String> map : mrpNoAndItemCodeMapList) {
			String mrpNo = map.get("mrpNo");
			String itemCode = map.get("itemCode");
			mrpNoAndItemCodeHashMap.put(mrpNo, itemCode);
		}

		System.out.println("mrpGatheringRegisterDate = " + mrpGatheringRegisterDate);
		System.out.println("mrpNoArrayList = " + mrpNoArrayList);
		System.out.println("mrpNoAndItemCodeHashMap = " + mrpNoAndItemCodeHashMap);

		map = new ModelMap();
		try {
			HashMap<String, Object> resultMap  = productionService.registerMrpGathering(mrpGatheringRegisterDate, mrpNoArrayList, mrpNoAndItemCodeHashMap);

			map.put("result", resultMap);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

//	@RequestMapping(value="/mrp/gathering", method=RequestMethod.POST)
//	public ModelMap registerMrpGathering(HttpServletRequest request, HttpServletResponse response) {
//		String mrpGatheringRegisterDate = request.getParameter("mrpGatheringRegisterDate");
//		String mrpNoList = request.getParameter("mrpNoList");
//		String mrpNoAndItemCodeList = request.getParameter("mrpNoAndItemCodeList");
//		map = new ModelMap();
//		try {
//			ArrayList<String> mrpNoArr = gson.fromJson(mrpNoList,
//					new TypeToken<ArrayList<String>>() { }.getType());
//			HashMap<String, String> mrpNoAndItemCodeMap =  gson.fromJson(mrpNoAndItemCodeList, //mprNO : ItemCode
//					new TypeToken<HashMap<String, String>>() { }.getType());
//			HashMap<String, Object> resultMap  = productionService.registerMrpGathering(mrpGatheringRegisterDate, mrpNoArr,mrpNoAndItemCodeMap);
//
//			map.put("result", resultMap);
//			map.put("errorCode", 1);
//			map.put("errorMsg", "성공");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			map.put("errorCode", -1);
//			map.put("errorMsg", e1.getMessage());
//		}
//		return map;
//	}

	@RequestMapping(value="/mrp/mrpgathering/list", method=RequestMethod.GET)
	public ModelMap searchMrpGathering(HttpServletRequest request, HttpServletResponse response) {
		String searchDateCondition = request.getParameter("searchDateCondition");
		String startDate = request.getParameter("mrpGatheringStartDate");
		String endDate = request.getParameter("mrpGatheringEndDate");
		map = new ModelMap();
		try {
			ArrayList<MrpGatheringTO> mrpGatheringList =
					productionService.searchMrpGatheringList(searchDateCondition, startDate, endDate);

			map.put("gridRowJson", mrpGatheringList);
			map.put("errorCode", 1);
			map.put("errorMsg", "성공");
		} catch (Exception e1) {
			e1.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e1.getMessage());
		}
		return map;
	}

}
