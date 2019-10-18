package utilities;

import dataobject.LookupItem;
import dataobject.Posting;

import java.io.RandomAccessFile;
import java.util.List;

public interface Compressor {

    public byte[] compress(int[] integersToWrite);

    public List<Posting> decompress(LookupItem lookupItem, RandomAccessFile randomAccessFile);
}
