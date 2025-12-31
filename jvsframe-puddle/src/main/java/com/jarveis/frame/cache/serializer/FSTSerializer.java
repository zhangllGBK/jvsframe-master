package com.jarveis.frame.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * FST序列化工具
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class FSTSerializer implements Serializer {

	private FSTConfiguration fstConfiguration;

	public FSTSerializer() {
		fstConfiguration = FSTConfiguration.getDefaultConfiguration();
		fstConfiguration.setClassLoader(Thread.currentThread()
				.getContextClassLoader());
	}

	public byte[] serialize(Object obj) {
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try {
			out = new ByteArrayOutputStream();
			fout = new FSTObjectOutput(out, fstConfiguration);
			fout.writeObject(obj);
			fout.flush();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}

	public Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		FSTObjectInput in = null;
		try {
			in = new FSTObjectInput(new ByteArrayInputStream(bytes),
					fstConfiguration);
			return in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

}
