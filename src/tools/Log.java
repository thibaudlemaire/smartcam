package tools;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Cette classe est utilisée pour capturer les logs des différents modules
 * @author thibaud
 *
 */
public class Log {

	public final static Logger logger = Logger.getLogger("pactLog");
	
	public StreamHandler getStdOutSh() {
		SingleLineFormatter fmt = new SingleLineFormatter();
	    final StreamHandler seh = new StreamHandler(System.out, fmt) {
	        @Override
	        public synchronized void publish(final LogRecord record) {
	            super.publish(record);
	            flush();
	        }
	    };
	    return seh;
	}
	public StreamHandler getStdErrSh() {
		SingleLineFormatter fmt = new SingleLineFormatter();
	    final StreamHandler seh = new StreamHandler(System.err, fmt) {
	        @Override
	        public synchronized void publish(final LogRecord record) {
	            super.publish(record);
	            flush();
	        }
	    };
	    return seh;
	}
	/**
	 * Fonction qui initialise le logger
	 */
	public void initLogger()
	{
		logger.setLevel(Level.ALL); //pour envoyer les messages de tous les niveaux
		logger.setUseParentHandlers(false); // pour supprimer la console par défaut
		addConsoleHandler();
		//addFileHandler();
	}
	
	/**
	 * Fonction qui écrit les log sur la console
	 */
	private void addConsoleHandler()
	{
		StreamHandler shOut = getStdOutSh();
		StreamHandler shErr = getStdOutSh();
		shErr.setLevel(Level.WARNING);
		shOut.setLevel(Level.ALL);
		logger.addHandler(shOut);
		logger.addHandler(shErr);
	}
	
	/**
	 * Fonction qui écrit les logs dans un fichier
	 */
	@SuppressWarnings("unused")
	private void addFileHandler()
	{
		try 
		{
			FileHandler fileHandler = new FileHandler("log/history.log", 100, 0);
			fileHandler.setLevel(Level.ALL);
			logger.addHandler(fileHandler);
		} 
		catch (SecurityException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
}
