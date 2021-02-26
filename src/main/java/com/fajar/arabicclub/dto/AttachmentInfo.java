package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.arabicclub.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8953767869805765435L;
	private static final double MAX_SIZE = 9000.d; //9KB
	private String name;
	private String extension;
	private String data;
	private Map<String,Object> blob;
	private String url;
	private int total;
	private int order;
	
	public String getPureBase64Data() {
		if (null == url) {
			return null;
		}
		if (url.contains(";base64,")) {
			return url.split(";base64,")[1];
		}
		try {
			return url.split(",")[1];
		} catch (Exception e) {
			 
			return null;
		}
	}
	
	public static List<AttachmentInfo> extractAttachmentInfos(String pureBase64String, String name) {
		List<AttachmentInfo> result = new ArrayList<AttachmentInfo>();
		int fileSize = StringUtil.base64StringFileSize(pureBase64String);
		if (fileSize <= MAX_SIZE) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(pureBase64String);
			attachmentInfo.setTotal(1);
			attachmentInfo.setOrder(1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
			return result;
		}
		double division =  Math.ceil(fileSize/MAX_SIZE);
		double partialSize =  Math.ceil(pureBase64String.length()/division);
		System.out.println("fileSize: "+fileSize);
		System.out.println("Max fileSize: "+MAX_SIZE);
		System.out.println("division: "+division);
		System.out.println("partialSize: "+partialSize);
		String[] dividedString = StringUtil.divideStringInto(pureBase64String, partialSize);
		for (int i = 0; i < dividedString.length; i++) {
			AttachmentInfo attachmentInfo = new AttachmentInfo();
			attachmentInfo.setData(dividedString[i]);
			attachmentInfo.setTotal((int)division);
			attachmentInfo.setOrder(i + 1);
			attachmentInfo.setName(name);
			result.add(attachmentInfo);
		}
		
		return result ;
	}

}
