package com.rain.flame.serialization.fst;

import com.rain.flame.serialization.support.SerializableClassRegistry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.InputStream;
import java.io.OutputStream;

public class FstFactory {

    private static final FstFactory factory = new FstFactory();

    private final FSTConfiguration conf = FSTConfiguration.createStructConfiguration();

    public static FstFactory getDefaultFactory() {
        return factory;
    }

    public FstFactory() {
        SerializableClassRegistry.getRegisteredClasses().keySet().forEach(conf::registerClass);
    }

    public FSTObjectOutput getObjectOutput(OutputStream outputStream) {
        return conf.getObjectOutput(outputStream);
    }

    public FSTObjectInput getObjectInput(InputStream inputStream) {
        return conf.getObjectInput(inputStream);
    }
}
