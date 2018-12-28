package com.rain.flame.serialization;

import com.rain.flame.common.URL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {
    ObjectOutput serialize(URL url, OutputStream output) throws IOException;

    ObjectInput deserialize(URL url, InputStream input) throws IOException;

    public byte getContentTypeId();

    public String getContentType();

}
