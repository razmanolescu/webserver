package adobeserver.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import adobeserver.http.HttpRequest;
import adobeserver.http.HttpResponse;
import adobeserver.http.HttpResponseBuilder;
import adobeserver.security.SecurityManager;
import adobeserver.server.ServerConfig;

/**
 *  Static file handler
 * 
 * 	@author Razvan Manolescu
 */
public class StaticFileHandler implements Handler {

    final static Logger LOG = Logger.getLogger(StaticFileHandler.class.getSimpleName());
	
	final String basePath;
	
	public StaticFileHandler(final ServerConfig config) {
		this.basePath = config.getFileBasePath();
	}

	public  HttpResponse handle(final HttpRequest request)  {
		if(!SecurityManager.getInstance().isValidPath(request.getPath())) {
			return HttpResponseBuilder.newInstance().forbidden().build();
		}
		
		final File file = new File(this.basePath + request.getPath());
		final StringBuilder stringBuilder = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			
			int mark = 0;
			final char[] buffer = new char[2048];
			while(( mark = reader.read(buffer)) > 0) {
				stringBuilder.append(buffer);
				
				if(mark < buffer.length) {
					stringBuilder.setLength(stringBuilder.length() - (buffer.length - mark));
				}
			}
			
			reader.close();
		} catch(IOException ex) {
			LOG.error("Could not read requested file", ex);
			
			return HttpResponseBuilder.newInstance().notFound().build();
		}
		
		return HttpResponseBuilder.newInstance().payload(stringBuilder.toString())
				.payloadFileExtension(FilenameUtils.getExtension(file.getName())).build();
	}
	
}
