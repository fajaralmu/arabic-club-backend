package com.fajar.arabicclub.service.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fajar.arabicclub.dto.AttachmentInfo;
import com.fajar.arabicclub.dto.WebRequest;
import com.fajar.arabicclub.util.ThreadUtil;

/*
	*<p>
	* Trivial implementation of the {@link MultipartFile} interface to wrap a byte[] decoded
	* from a BASE64 encoded String
	*</p>
	*/
public class Base64DecodedToMultipart implements MultipartFile {
	private final byte[] imgContent;
	final AttachmentInfo attachmentInfo;

	public Base64DecodedToMultipart(AttachmentInfo attachmentInfo) throws  Exception {
		this.attachmentInfo = attachmentInfo;
		this.imgContent =   Base64.getDecoder().decode(attachmentInfo.getData().getBytes("UTF-8"));
	}

	@Override
	public String getName() {
		return "image";
	}

	@Override
	public String getOriginalFilename() {
		// TODO - implementation depends on your requirements
		return attachmentInfo.getName();
	}

	@Override
	public String getContentType() {
		// TODO - implementation depends on your requirements
		return "image/" + attachmentInfo.getExtension();
	}

	@Override
	public boolean isEmpty() {
		return imgContent == null || imgContent.length == 0;
	}

	@Override
	public long getSize() {
		return imgContent.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return imgContent;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(imgContent);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		new FileOutputStream(dest).write(imgContent);
	}
	
	
	public static void main(String[] args) {
		String url2 = "https://mpiquiz.herokuapp.com/api/public/requestid";
		String url = "http://206.253.167.195:8080/arabicclub/api/public/requestid";
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);     

		HttpEntity<WebRequest> request = new HttpEntity<>(WebRequest.builder().build(), headers);
		for (int i = 0; i < 500; i++ ) {
			final int number = i;
			ThreadUtil.run(()->{
			ResponseEntity<String> response = rt.postForEntity(url2, request, String.class);
			System.out.println(number+" - Response "+response);
			
			});
		}
	}
}
