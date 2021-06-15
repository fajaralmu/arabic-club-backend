package com.fajar.arabicclub.service.quiz;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.fajar.arabicclub.constants.AnswerCode;
import com.fajar.arabicclub.entity.Quiz;
import com.fajar.arabicclub.entity.QuizChoice;
import com.fajar.arabicclub.entity.QuizQuestion;
import com.fajar.arabicclub.service.ProgressNotifier;
import com.fajar.arabicclub.util.CollectionUtil;

public class QuizCreatorByExcel {

	private Workbook wb;
	final String base64;
	private Quiz quiz;
	private Sheet sheet;
	private List<Row> rows;
	private ProgressNotifier notifier;
	private boolean essay = false;
	
	public void setNotifier(ProgressNotifier notifier) {
		this.notifier = notifier;
	}
	
	void updateProgress(int val, int max, int proportion) {
		if (notifier == null) return;
		notifier.updateProgress(val, max, proportion);
	}
	
	
	public QuizCreatorByExcel(String base64) {
		this.base64 = base64;
	}

	public Quiz read() throws InvalidFormatException, UnsupportedEncodingException, IOException {
		wb = WorkbookFactory.create(base64ToInputStream(base64));
		sheet = wb.getSheetAt(0);
		rows = CollectionUtil.fromIterator( sheet.rowIterator());
		
		setQuizInfo();
		updateProgress(1,1,10);
		setQuestions();
		return quiz;
	}
	
	private void setQuestions() {
		for (int i = 6; i < rows.size(); i++) {
			QuizQuestion question = essay? getQuestionEssay(rows.get(i)) : getQuestion(rows.get(i));
			quiz.addQuestion(question);
			updateProgress(1,rows.size()-6, 90);
		}
	}

	private QuizQuestion getQuestion(Row row) {
		QuizQuestion question = new QuizQuestion();
		Double number = row.getCell(0).getNumericCellValue();
		String statement = row.getCell(1).getStringCellValue();
		String answer = row.getCell(3).getStringCellValue().toUpperCase();
		
		if (quiz.isQuestionsTimered()) {
			Double duration = row.getCell(2).getNumericCellValue();
			question.setDuration(duration.longValue());
		}
		 
		question.setStatement(statement);
		question.setNumber(number.intValue());
		question.setAnswerCode(AnswerCode.valueOf(answer));
		question.setChoices(getChoices(row));
		
		System.out.println("QST: "+question);
		
		return question;
	}
	private QuizQuestion getQuestionEssay(Row row) {
		QuizQuestion question = new QuizQuestion();
		question.setEssay(essay);
		Double number = row.getCell(0).getNumericCellValue();
		String statement = row.getCell(1).getStringCellValue();
		String answer = row.getCell(2).getStringCellValue();
		question.setStatement(statement);
		question.setNumber(number.intValue());
		question.setAnswerEssay(answer);
		
		System.out.println("QST: "+question);
		
		return question;
	}

	private List<QuizChoice> getChoices(Row row) {
		List<QuizChoice> choices = new LinkedList<>();
		int offset = 4;
		for (int i = 0; i < AnswerCode.values().length; i++) {
			try {
				QuizChoice choice = new QuizChoice();
				choice.setStatement(row.getCell(offset + i).getStringCellValue());
				choice.setAnswerCode(AnswerCode.values()[i]);
				choices.add(choice);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return choices ;
	}

	private Row getRowAt(int index) {
		return rows.get(index);
	}
	private void setQuizInfo() {
		quiz = new Quiz();
		String title = getRowAt(1).getCell(1).getStringCellValue();
		String quizModeString =getRowAt(1).getCell(2).getStringCellValue(); 

		QuizMode quizMode = QuizMode.MULTIPLE_CHOICE;
		try {
			quizMode = QuizMode.valueOf(quizModeString);
		} catch (Exception e) { }
		if (quizMode.equals(QuizMode.ESSAY)) {
			this.essay = true;
		}
		
		String timerMode = getRowAt(2).getCell(1).getStringCellValue();
		TimerMode mode;
		try {
			mode = TimerMode.valueOf(timerMode);
		} catch (Exception e) {
			mode = TimerMode.NO_TIMER;
		}
		 
		System.out.println("TIMER MODE:"+mode);

		quiz.setTitle(title);
		quiz.setActive(false);
		quiz.setCreatedDate(new Date());
		quiz.setShowAllQuestion(false);
		if (mode.equals(TimerMode.FULL_TIMER)) {
			Double duration = getRowAt(3).getCell(1).getNumericCellValue();
			System.out.println("duration: "+duration);
			quiz.setDuration(duration.longValue() );
		}  
		quiz.setQuestionsTimered(mode.equals(TimerMode.QUESTION_TIMER));
		
		
	}

	private ByteArrayInputStream base64ToInputStream(String base64) throws UnsupportedEncodingException {
		byte[] decodedString = Base64.getDecoder().decode(base64.getBytes("UTF-8"));
		return new ByteArrayInputStream(decodedString);
	}

	public static void main(String[] args) throws IOException, InvalidFormatException {
//		URL resource = new QuizCreatorByExcel(null).getClass().getClassLoader().getResource("quiz_template.xlsx");
//		String fileName =  (resource.getFile());
		QuizCreatorByExcel creator = new QuizCreatorByExcel(encodeFileToBase64("D:\\Development\\Kafila Projects\\arabic-club-backend\\target\\classes\\quiz_template.xlsx"));
		
		creator.read();
	}
	
	private static String encodeFileToBase64(String fileName) throws IOException {
		File file = new File(fileName);
		byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
		String base64 = new String(encoded, StandardCharsets.US_ASCII);
		System.out.println("Base 64: " + base64);
		return base64;
	}
	
	static enum TimerMode {
		NO_TIMER, FULL_TIMER, QUESTION_TIMER
	}
	static enum QuizMode {
		ESSAY, MULTIPLE_CHOICE
	}
}
