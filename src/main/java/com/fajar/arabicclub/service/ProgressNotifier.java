package com.fajar.arabicclub.service;

public interface ProgressNotifier {

	void updateProgress(int progress, int max, int totalProportion);
}
