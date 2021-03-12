package com.fajar.arabicclub.service.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fajar.arabicclub.dto.AttachmentInfo;
import com.fajar.arabicclub.service.ProgressService;
import com.fajar.arabicclub.service.config.WebConfigService;
import com.fajar.arabicclub.util.IconWriter;
import com.fajar.arabicclub.util.StringUtil;
import com.fajar.arabicclub.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {

	private static final String ICON_BASE64_PREFIX = "data:image/ico;base64,";
	@Autowired
	private WebConfigService webAppConfiguration;
	@Autowired
	private FtpResourceService ftpResourceService;
	@Value("${app.resources.uploadType}")
	private String uploadType;
	@Value("${app.resources.file.api.upload.image}")
	private String apiUploadImageEndpoint;
	@Value("${app.resources.file.api.upload.document}")
	private String apiUploadDocumentEndpoint;
	@Autowired
	private ProgressService progressService;

	RestTemplate restTemplate = new RestTemplate();

	@PostConstruct
	public void init() {
	}

	int counter = 0;

	public int getCounter() {
		return counter;
	}

	private void addCounter() {
		counter++;
	}

	public String writeIcon(String code, String data, @Nullable HttpServletRequest httpServletRequest)
			throws Exception {
		System.out.println("Writing Icon with code: " + code);
		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {

			System.out.println("Invalid icon image string: " + imageData);
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];
		BufferedImage image = IconWriter.getImageFromBase64String(imageString);

		String iconName;
		if ("api".equals(uploadType)) {
			String iconBase64String = IconWriter.getIconBase64String(image);
			iconName = writeImageApi("ICON_" + code, ICON_BASE64_PREFIX + iconBase64String, httpServletRequest);
		} else {
			iconName = "ICO_" + code + "_" + StringUtil.generateRandomNumber(10) + ".ico";
			IconWriter.writeIcon(image, getPath() + "/ICON", iconName);
		}

		return iconName;

	}

	public synchronized String writeImage(String code, String data) throws IOException {
		return writeImage(code, data, null);
	}

	public synchronized String writeImage(String code, String data, HttpServletRequest httpServletRequest)
			throws IOException {
		log.info("#uploadType: {}", uploadType);
		if ("ftp".equals(uploadType)) {
			return writeImageFtp(code, data);
		}
		if ("api".equals(uploadType)) {
			return writeImageApi(code, data, httpServletRequest);
		}

		return writeImageToDisk(code, data);
	}

	private String writeImageApi(String code, final String data, HttpServletRequest httpServletRequest) {

		System.out.println("writeImageApi with code: " + code);
		if (data.split(";base64,").length < 2) {
			System.out.println("Invalid image string: " + data);
			return null;
		}
		progressService.sendProgress(10, httpServletRequest);
		// extract image name
		String imageType = getImageType(data);

		progressService.sendProgress(10, httpServletRequest);
		String imageFileName = code + "_" + getRandomId() + "." + imageType;
		addCounter();

		uploadBase64Strings(data, imageFileName, apiUploadImageEndpoint, httpServletRequest);
		return imageFileName;
	}

	private static String getImageType(String fullBase64String) {
		String imageIdentity = fullBase64String.split(";base64,")[0];
		return imageIdentity.replace("data:image/", "");
	}

	public String writeDocumentApi(String code, final AttachmentInfo data, HttpServletRequest httpServletRequest) {

		progressService.sendProgress(10, httpServletRequest);
		String fileName = code + "_" + getRandomId() + "_" + data.getName();
		addCounter();
		progressService.sendProgress(10, httpServletRequest);
		uploadBase64Strings(data.getUrl(), fileName, apiUploadDocumentEndpoint, httpServletRequest);
		return fileName;
	}

	private void uploadBase64Strings(String data, String name, String URL, HttpServletRequest httpServletRequest) {
		try {
			List<AttachmentInfo> attachments = AttachmentInfo.extractAttachmentInfos(data, name);
			for (int i = 0; i < attachments.size(); i++) {
				String response = uploadViaAPIv2(attachments.get(i), URL);
				System.out.println("response: " + i + " => " + response);
				progressService.sendProgress(1, attachments.size(), 80, httpServletRequest);
			}
			progressService.sendComplete(httpServletRequest);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRandomId() {

		return String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_" + getCounter();
	}

	public static void main(String[] args) throws Exception {
		String base64 = "data:image/png;base64,skddjfdkfd";
		System.out.println(getImageType(base64));
	}

	public String uploadViaAPIv2(AttachmentInfo request, String URL) {

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String response;

		try {
			map.add("partialData", request.getData());
			map.add("order", request.getOrder());
			map.add("total", request.getTotal());
			map.add("name", request.getName());
			map.add("extension", request.getExtension());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(URL, requestEntity, String.class);
			log.info("code: {}", responseEntity.getStatusCode());
			response = responseEntity.getBody();

		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			response = e.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
		}
		return response;
	}

	public synchronized String writeImageFtp(String code, String data) throws IOException {

		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}

		String imageString = imageData[1];

		// extract image name
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		addCounter();
		ThreadUtil.run(() -> {
			ftpResourceService.storeFtp(imageString, imageFileName);
		});
		return imageFileName;
	}

	public synchronized String writeImageToDisk(String code, String data) throws IOException {

		String[] imageDataSplitted = data.split(",");
		if (imageDataSplitted == null || imageDataSplitted.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageDataSplitted[1];
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();

		// write the image to a file
		String imageIdentity = imageDataSplitted[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String randomId = String.valueOf(new Date().getTime()) + StringUtil.generateRandomNumber(5) + "_"
				+ getCounter();

		String imageFileName = code + "_" + randomId + "." + imageType;
		File outputfile = new File(getPath() + "/" + imageFileName);
		ImageIO.write(image, imageType, outputfile);

		System.out.println("==output file: " + outputfile.getAbsolutePath());

		addCounter();

		return imageFileName;
	}

	private String getPath() {
		return webAppConfiguration.getUploadedImageRealPath();
	}

}
