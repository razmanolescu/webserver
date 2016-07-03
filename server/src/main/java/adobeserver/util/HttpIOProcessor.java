package adobeserver.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import adobeserver.http.HttpResponse;

/**
 *  HTTP I/O processor. Takes {@link HttpResponse} and {@link HttpRequest} objects from data streams.
 * 
 * 	@author Razvan Manolescu
 */
public class HttpIOProcessor {
	private final static Logger LOG = Logger.getLogger(HttpIOProcessor.class.getSimpleName());
	
    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = DEFAULT_CHARSET.newEncoder();
    private static  final ByteBuffer buffer = ByteBuffer.allocate(ServerConstants.READ_BUFFER_SIZE_BYTES);
    private static int mark = 0;
    
    private static void writeLine(final SocketChannel channel, final String line) throws IOException {
        channel.write(encoder.encode(CharBuffer.wrap(line + "\r\n")));
    }

    public void sendResponse(final SocketChannel channel, final HttpResponse response, final boolean isKeepAlive) {

        try {
            writeLine(channel, response.getVersion() + " " + response.getStatus().getResponseCode()
            		+ " " + response.getStatus().getResponseCode());
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                writeLine(channel, header.getKey() + ": " + header.getValue());
            }
            
            writeLine(channel, "");
            channel.write(ByteBuffer.wrap(response.getPayload()));
        } catch (IOException ex) {
        	LOG.error("Could not send response " + ex.getMessage());
        }
    }

    public List<String> readRequest(final SocketChannel channel) throws IOException {
    	readRawData(channel);
        final List<String> requests = compressLines();
		return requests;
    }
    
    
    private List<String> compressLines() throws IOException {
    	final List<String> requets = new ArrayList<String>(2);
        final StringBuilder sb = new StringBuilder();
        final StringBuilder global = new StringBuilder();
        char lastChar = (char)0;
        
        while (buffer.hasRemaining()) {
            final char curChar = (char) buffer.get();
            sb.append(curChar);
            if (curChar == '\n' && lastChar == '\r') {
                // mark position
                mark = buffer.position();
                
                global.append(sb);
                // Request delimiter is reached
                if(sb.length() == 2) {
                	requets.add(global.toString());
                	global.delete(0, global.length());
                }
                
                sb.delete(0, sb.length());
            }
            lastChar = curChar;
        }
        
        return requets;
    }


    /**
     * Gets raw data into buffer
     * 
     * @param channel
     * @param mark
     * @throws IOException
     */
    private void readRawData(final SocketChannel channel) throws IOException {
    	if(buffer.capacity() - buffer.position() < ServerConstants.MIN_MESSAGE_MEMORY_BYTES) {
    		synchronized (buffer) {
    			if(buffer.capacity() - buffer.position() < ServerConstants.MIN_MESSAGE_MEMORY_BYTES) {
    				//buffer.clear();
    				buffer.rewind();
    				mark = 0;
    			}
			}
    	}
    	
        buffer.limit(buffer.capacity());
        
        if ((channel.read(buffer)) == -1) {
        	LOG.error("EOS encounted");
        	channel.close();	
            throw new IOException("EOS");
        }
        
        // Flip buffer for reading
        buffer.flip();
        buffer.position(mark);
    } 
}
