package me.funkymiller.Core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigSystem {
	/** File name of the .properties file */
	private String propFileName;
	
	/** Properties object that we load the config in to */
	private Properties prop;
	
	/** Stream to read the properties file */
	private InputStream inputStream;
	
	private Logger log;
	
	/**
	 * Creates a Config System instance for a .properties file
	 * @param file = name of the properties file that we will be working with in this instance
	 */
	public ConfigSystem(String file) {
		propFileName = file;
		prop = new Properties();
		inputStream = null;
		log = LoggerFactory.getLogger(getClass());
	}
	
	/**
	 * Load the properties file for the instance in to the properties object
	 * 
	 * @return populated properties object
	 */
	public Properties getProperties() {
				
		try {
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			
			Date time = new Date(System.currentTimeMillis());
			
			log.info(time + " - Read " + prop.size() + " properties from '" + propFileName + "'");
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return prop;
	}
	
	/**
	 * Save the properties file for thie instance
	 */
	public void saveProperties() {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(propFileName);
		} catch (FileNotFoundException e) {
			log.error("Failed to open FileOutputStream for " + propFileName);
			e.printStackTrace();
		}
		
		if (os != null) {
			try {
				prop.store(os, null);
				log.info("Saved " + propFileName);
			} catch (IOException e) {
				log.error("Failed to store " + propFileName);
				e.printStackTrace();
			} finally {
				try {
					os.close();
				} catch (IOException e) {
					log.error("Failed to close FileOutputStream for " + propFileName);
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Add a new property to this instance
	 */
	public void addProperty(String key, String val) {
		prop.put(key, val);
		//log.debug("Adding property " + key + " => " + val + " to " + this.propFileName);
	}
	
	/**
	 * Get the property value for the passes key from this instance's properties object
	 * @param propKey = Key to return the value for
	 * @return Return the value for the Key passed
	 */
	public String getPropVal(String propKey) {
		String propVal = prop.getProperty(propKey,null);
		return propVal;
	}
	
	public void clearProperties() {
		prop.clear();
		saveProperties();
	}
	
}
