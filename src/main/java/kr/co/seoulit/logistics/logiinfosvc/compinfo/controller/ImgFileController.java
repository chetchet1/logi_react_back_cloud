package kr.co.seoulit.logistics.logiinfosvc.compinfo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/compinfo/*")
public class ImgFileController {

	private static String serverUploadFolderPath = "ImgServer\\empPhoto\\";
	private static String workspaceUploadFolderPath = "C:\\물류프로젝트\\Logistics71_spring1\\src\\main\\webapp\\ImgServer\\empPhoto";

	@PostMapping("/imgfileupload")
	public ModelMap imgFileUpload(MultipartHttpServletRequest request, HttpServletResponse response) {

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json; charset=UTF-8");

		ModelMap map = new ModelMap();

		// 업로드 경로 설정
		String root = request.getSession().getServletContext().getRealPath("/");
		String uploadPath = root + serverUploadFolderPath;

		// 파일 업로드 처리
		try {
			for (MultipartFile file : request.getFileMap().values()) {
				if (!file.isEmpty()) {
					// 저장할 파일명
					String storedFileName = file.getOriginalFilename();

					// 저장 경로
					Path from = Paths.get(uploadPath + storedFileName);
					Path to = Paths.get(workspaceUploadFolderPath + storedFileName);

					// 파일을 서버에 저장
					file.transferTo(from.toFile());

					// 파일을 워크스페이스의 폴더로 복사
					Files.copy(from, to);

					map.put("ImgUrl", "/" + serverUploadFolderPath + storedFileName);
					map.put("errorCode", 1);
					map.put("errorMsg", "성공");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			map.put("errorCode", -1);
			map.put("errorMsg", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("errorCode", -2);
			map.put("errorMsg", e.getMessage());
		}

		return map;
	}
}
