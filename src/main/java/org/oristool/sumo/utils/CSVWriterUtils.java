package org.oristool.sumo.utils;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CSVWriterUtils {

	public static CSVWriter getWriterInstance(String fileName) {
		CSVWriter dataWriter = null;

		try {
			dataWriter = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataWriter;
	}

	public static void writeLine(CSVWriter writer, ArrayList<String> entries) {
		String[] entriesArray = entries.toArray(new String[1]);
		for (int j = 0; j < entriesArray.length; j++) {
			entriesArray[j] = entriesArray[j].replace(".", ",");
		}
		writer.writeNext(entriesArray);
	}

	public static void closeWriter(CSVWriter writer) {
		try {
			Objects.requireNonNull(writer).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
