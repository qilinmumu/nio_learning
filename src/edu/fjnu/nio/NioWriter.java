package edu.fjnu.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioWriter {
	public static void main(String[] args) throws IOException {
		//要写入文件的内容
		String str = "你好啊，今天的天气真好。";
		
		byte[] array = str.getBytes("UTF-8");
		//关联文件
		FileOutputStream fos = new FileOutputStream("e://nioTest2.txt");
		//获得通道
		FileChannel channel = fos.getChannel();
		//创建缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(array.length);
		
		buffer.put(array);
		buffer.flip();
		//写入文件
		channel.write(buffer);
		//清空缓冲区
		buffer.clear();
		fos.close();
	}
}
