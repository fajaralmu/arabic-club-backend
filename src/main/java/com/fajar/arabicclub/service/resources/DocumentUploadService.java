package com.fajar.arabicclub.service.resources;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.arabicclub.dto.AttachmentInfo;
import com.fajar.arabicclub.entity.BaseEntity;
import com.fajar.arabicclub.entity.setting.SingleDocumentModel;
import com.fajar.arabicclub.repository.EntityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentUploadService {
	@Autowired
	private FileService fileService;
	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private FileRemovalService documentRemovalService;

	public String upload(SingleDocumentModel SingleDocumentModel) {
		return upload(SingleDocumentModel, null);
	}
	/**
	 * upload single document
	 * 
	 * @param SingleDocumentModel
	 * @return
	 */
	public String upload(SingleDocumentModel SingleDocumentModel, HttpServletRequest httpServletRequest) {

		AttachmentInfo document = SingleDocumentModel.getAttachmentInfo();
		if (document != null) {

			if (null != SingleDocumentModel.getId()) {
				removeOldFile(SingleDocumentModel);
			}
			try {
				String savedFileName = fileService.writeDocumentApi(SingleDocumentModel.getClass().getSimpleName(), document, httpServletRequest);
				SingleDocumentModel.setDocumentName(savedFileName);
				return savedFileName;
			} catch (Exception e) {
				e.printStackTrace();
				SingleDocumentModel.setDocumentName(null);
				return null;
			}

		}
		return null;
	}

	private void removeOldFile(SingleDocumentModel SingleDocumentModel) {
		BaseEntity existingRecord = entityRepository.findById(((BaseEntity)SingleDocumentModel).getClass(), SingleDocumentModel.getId());
		if (null == existingRecord) {
			return;
		}
		SingleDocumentModel existingdocumentModel = (SingleDocumentModel) existingRecord;
		if (null != existingdocumentModel.getDocumentName()) {
			documentRemovalService.removeDocument(existingdocumentModel.getDocumentName());
		}
	}

	 
 
}
