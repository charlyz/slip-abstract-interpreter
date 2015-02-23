import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import slip.internal.Method;

/**
 * Class managing static analysis and reading / writing in analysis files
 * 
 * @author Cedric Vanderperren
 */
public class StaticAnalysis
{
	public static final int NR = 0;
	public static final int OK = 1;
	public static final int KO = 2;
	public static final int UK = 3;
	public static final int XX = 4;
	public static final String[] annotations = {"NR", "OK", "KO", "UK", "XX"};
	
	public static final String PATH_TIME_FILE = "temps.txt";
	public static long uselessTime = 0;
	// Time to retry of total duration (e.q. waiting on the standard input)

	private static int[] errors;
	// errors[i] is the type of error for label i+1 (among NR, OK, KO or UK).
	private static String[] errorMessages;
	// errorMessages[i] contains error messages for label i+1 (if any).	

	/** Initializes errors management
	 * 
	 * @pre numberOfLabels >= 0, the number of labels in the slip program
	 * @post creates arrays errors and errorMessages of size numberOfLabels
	 * @post All labels are declared as non reachable
	 */
	public static void initErrorsManagement(int numberOfLabels)
	{
		errors = new int[numberOfLabels];
		errorMessages = new String[numberOfLabels];
	}

	/** Sets appropriated value when a label has encountered an error
	 * 
	 * @pre label > 0
	 * @post A (new) annotation code is associated to errors[label-1]
	 * @post errorMessages[label-1] contains errorMessage (if any)
	 */
	public static void setValueWhenError(int label, String errorMessage)
	{
		switch(errors[label-1])
		{
		case NR: errors[label-1] = KO; 	break;
		case OK: errors[label-1] = UK;	break;
		case KO:  						break;
		case UK: 						break;
		}
		// Decision taken from previous value of errors[label-1]

		if(errorMessages[label-1] != null)
		{
			if(!errorMessages[label-1].contains(errorMessage))
				errorMessages[label-1] += ", " + errorMessage;
			// Adds errorMessage only if it's not yet in errorMessages[label-1]
		}
		else 
			errorMessages[label-1] = errorMessage;
	}

	/** Sets appropriated value when a label has not encountered an error
	 * 
	 * @pre label > 0
	 * @post A (new) annotation code is associated to errors[label-1]
	 */
	public static void setValueWhenNoError(int label)
	{
		switch(errors[label-1])
		{
		case NR: errors[label-1] = OK; 	break;
		case OK: 						break;
		case KO: errors[label-1] = UK; 	break;
		case UK: 						break;
		}
		// Decision taken from previous value of errors[label-1]
	}

	/** Writes duration of analysis in the time file
	 * 
	 * @pre duration > uselessTime, the duration of analysis incl. uselesstime
	 * @pre nameOfSlipFile != null
	 * @post Deletes line starting by "nameOfSlipFile.slip"
	 * @post Adds to end of time file a line "nameOfSlipFile.slip MM:ss:cc"
	 */
	public static void writeInTimeFile(long duration, String nameOfSlipFile)
	{
		duration -= uselessTime;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(duration);
		// Creation of a Calendar instance for the correct duration of analysis

		String min = "" + cal.get(Calendar.MINUTE);
		if(cal.get(Calendar.MINUTE) < 10) min = "0" + min;
		String sec = "" + cal.get(Calendar.SECOND);
		if(cal.get(Calendar.SECOND) < 10) sec = "0" + sec;
		String csec = "" + (cal.get(Calendar.MILLISECOND) / 10);
		if((cal.get(Calendar.MILLISECOND) / 10) < 10) csec = "0" + csec;
		// Conversion in aim to respect format MM:ss:cc

		String content = 
		  getFileWithoutLinesStartingWith(nameOfSlipFile + ".slip", PATH_TIME_FILE)
		  + nameOfSlipFile + ".slip " + min + ":" + sec + ":" + csec;

		writeInFile(PATH_TIME_FILE, content);
		
		System.out.println(min + ":" + sec + ":" + csec);
	}

	/** Writes unreachable methods in the file nameOfSlipFile.urm
	 * 
	 * @pre methods fully initialized
	 * @pre nameOfSlipFile != null
	 * @post Creates file with unreachable methods name (one by line)
	 */
	public static void writeInURMethodsFile(Method[] methods, String nameOfFile)
	{
		String path = nameOfFile + ".urm";
		StringBuffer content = new StringBuffer();

		for(Method m : methods)
		{
			if(errors[m.getL().labelInt-1] == NR) 
			{
				content.append(m.getM());
				if(!m.isStatic()) content.append("/" + m.getLevel());
				// Adds level in case of non static method
				content.append("\r\n");
			}
		}

		writeInFile(path, content.toString());
	}

	/** Writes analysis data in the file nameOfFile.ann
	 * 
	 * @pre nameOfFile != null
	 * @pre nameOfFile.properties created, accessible and contains lines 
	 * 		"X = <l1, l2, ..., ln>" where l1,...,ln are labels in internal slip
	 * @post Creates file with analysis data based on nameOfFile.properties, 
	 * 		which each line is "X AN <messages>" 
	 * 		(AN is an annotation among NR, OK, KO or UK)
	 */
	public static void writeInAnalysisFile(String nameOfFile)
	{
		String path = nameOfFile + ".ann";
		StringBuffer content = new StringBuffer();

		Object[] instrIDs = Settings.getSettings().keySet().toArray();
		Arrays.sort(instrIDs);
		// instrIDs contains all values sorted (lexic.), from file .properties
		
		int[] instrIDsInInt = new int[instrIDs.length];
		for(int i = 0; i < instrIDsInInt.length; i++)
		{
			try 
			{ 
				instrIDsInInt[i] = Integer.parseInt((String) instrIDs[i]);
				instrIDs[i] = null;
			}
			catch(Exception exc) { instrIDsInInt[i] = -1; }
		}
		Arrays.sort(instrIDsInInt);
		// instrIDs contains only values that are not integers
		// instrIDsInInt is sorted and contains the integer values from instrIDs 
		// and -1 for not integer values
		
		int i = 0;
		if(instrIDsInInt.length>0) 
			while(instrIDsInInt[i] == -1) i++;
		// i is the first index for a real integer value from instrIDs
		
		for(int j = 0; j < instrIDs.length; j++)
		{
			if(instrIDs[j]==null) 
			{
				instrIDs[j] = "" + instrIDsInInt[i];
				i++;
			}
		}
		// instrIDs contains now all values from .properties file, sorted in 
		// numeric order (first int val. in numeric order and next non int val.)

		for(Object instrID : instrIDs)
		{
			int annotation = XX;
			String messages = " ";
			for(int l : Settings.getLabelsForInstructionID((String)instrID))
			{
				switch(errors[l-1]) 
				{
				case KO: 
					annotation = KO; break;
				case OK: 
					if(annotation == XX) annotation = OK; 
					if(annotation == NR) annotation = UK; break;
				case UK:
					if(annotation != KO) annotation = UK; break;
				case NR:
					if(annotation == XX) annotation = NR;
					if(annotation == OK) annotation = UK; break;
				}
				// Decision taken from value of errors[l-1] and from current
				// value of annotation

				if(errorMessages[l-1] != null)
				{
					if(!messages.contains(errorMessages[l-1]))
						messages += errorMessages[l-1] + ", ";
				}
				
				messages = messages.trim();
				while(messages.endsWith(",")) 
				{
					messages = messages.substring(0, messages.length()-1);
					messages = messages.trim();
				}
				// Retries useless commas at the end of string
			}

			content.append(instrID + " " + annotations[annotation] 
			                                      + " " + messages + "\r\n");			
		}

		writeInFile(path, content.toString());	
	}
	
	/** Returns content of file path without lines starting by key
	 * 
	 * @pre path != null
	 * @pre key != null
	 * @post Returns content of file path without lines starting by key (if any)
	 * 			or returns empty string if file is not accessible
	 */
	public static String getFileWithoutLinesStartingWith(String key,String path)
	{
		StringBuffer result = new StringBuffer();

		try
		{
			BufferedReader bf = new BufferedReader(new FileReader(path));
			String line;
			while(((line = bf.readLine()) != null))
			{
				if(!line.startsWith(key)) 
					result.append(line + "\r\n");
				// Appends all lines at result except lines starting with key
			}
		}
		catch(IOException e)
		{
			return "";
		}

		return result.toString();
	}

	/** Writes content in file path or prints an error if not possible
	 * 
	 * @pre path != null
	 * @pre content != null
	 * @post Writes content in file path or prints an error if not possible
	 */
	public static void writeInFile(String path, String content)
	{
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(path));
			file.write(content);
			file.close(); 
		}
		catch(IOException e1)
		{
			System.err.println("Error: Impossible to write in " + path);
		} 
	}
}
