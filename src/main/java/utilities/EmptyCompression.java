package utilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class EmptyCompression implements Compression {
    public void encode(Integer[] input, ByteBuffer output) {
		for (int i : input) {
			output.putInt(i);
        }
    }
    public void decode(byte[] input, IntBuffer output) {
    	ByteBuffer bytes = ByteBuffer.wrap(input);
    	bytes.rewind();
    	output.put(bytes.asIntBuffer());
 }
}