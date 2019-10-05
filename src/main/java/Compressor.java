import DataObject.LookupItem;
import DataObject.Posting;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;

public interface Compressor {

    public byte[] compress(int[] integersToWrite);

    public List<Posting> decompress(LookupItem lookupItem, RandomAccessFile randomAccessFile);
}
