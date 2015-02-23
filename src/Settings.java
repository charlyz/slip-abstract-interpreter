import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Class managing configuration parameters of the program
 * 
 * @author Cedric Vanderperren
 */
public class Settings
{

	/**
	 * Instance variable
	 */
	private static Properties settings = new Properties();

	/**
	 * Initialize configuration parameters
	 */
	public static void initSettings(String nameFileForSettings)
	{
		try
		{
			loadProperties(settings, nameFileForSettings);
			// Loading configuration parameters
		}
		catch (Exception e)
		{
			System.err.println("Error: File " + nameFileForSettings + 
				" needed to produce expected results");
			System.exit(0);
		}
	}
	
	/**
	 * Gets a sorted array of label numbers for an instructionID
	 * 
	 * @pre instructionID != null && instructionID exists in .properties file
	 * @return a sorted array of label numbers as described in .properties file
	 */
	public static int[] getLabelsForInstructionID(String instructionID)
	{
		String labelsInString = getSetting(instructionID, null);
		labelsInString = labelsInString.substring(1, labelsInString.length()-1);
		String[] labelsStringArray = labelsInString.split(",");
		
		int[] labels = new int[labelsStringArray.length];
		int i = 0;
		for(String s : labelsStringArray)
		{
			labels[i] = Integer.parseInt(s);
			i++;
		}
		
		Arrays.sort(labels);
		
		return labels;
	}
	
	
	/**
	 * Get the configuration parameter key<br>
	 * 
	 * @param key
	 *            The key in the object settings of class Properties in the
	 *            configuration parameters
	 * @param defaultText
	 *            The default text if key cannot be found
	 *            
	 * @pre key != nulls
	 * 
	 * @return settings.getProperty(key, defaultText);
	 */

	private static String getSetting(String key, String defaultText)
	{
		return settings.getProperty(key, defaultText);
	}

	/**
	 * Load data of file nameOfFile in object properties<br>
	 * 
	 * @param properties
	 *            Object in which data will be loaded
	 * @param nameOfFile
	 *            Name of file containing the data
	 * 
	 * @throws Exception
	 *             If file does not exist or could not be found, if properties
	 *             is null
	 */
	public static void loadProperties(Properties properties, String nameOfFile)
			throws Exception
	{
		FileInputStream fileInputSteam = new FileInputStream(nameOfFile);
		// Creation of internal file to extract data
		properties.load(fileInputSteam);
		// Loading of configuration data
		fileInputSteam.close();
		// Closure of internal file
	}

	/**
	 * Get object settings of class Propeties
	 * 
	 * @return settings
	 */
	public static Properties getSettings()
	{
		return settings;
	}
}
