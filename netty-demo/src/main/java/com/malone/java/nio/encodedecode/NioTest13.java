package com.malone.java.nio.encodedecode;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class NioTest13 {

    public static void main(String[] args) throws Exception {
        String inputFile = "NioTest13_in.txt";
        String outputFile = "NioTest13_out.txt";

        RandomAccessFile inputRandomAccessFile = new RandomAccessFile(inputFile, "r");
        RandomAccessFile outputRandomAccessFile = new RandomAccessFile(outputFile, "rw");

        Long inputLength = new File(inputFile).length();

        FileChannel inputFileChannel = inputRandomAccessFile.getChannel();
        FileChannel outputFileChannel = outputRandomAccessFile.getChannel();
        // 内存映射文件
        MappedByteBuffer inputDate = inputFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputLength);
        // 如何查看文件的编码，在不同的系统上
        // 为什么编码是iso-8859-1，中文还是能正常显示
        Charset charset = Charset.forName("iso-8859-1");
        //  Charset charset = Charset.forName("utf-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        CharBuffer charBuffer = decoder.decode(inputDate);

        ByteBuffer ouputBuffer = encoder.encode(charBuffer);

        outputFileChannel.write(ouputBuffer);
        inputRandomAccessFile.close();
        outputRandomAccessFile.close();

        System.out.println("=============");
        Charset.availableCharsets().forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });
        System.out.println("=============");
    }
}
