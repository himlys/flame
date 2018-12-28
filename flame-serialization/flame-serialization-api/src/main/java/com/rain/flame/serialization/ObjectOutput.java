package com.rain.flame.serialization;

import java.io.IOException;

public interface ObjectOutput extends DataOutput{
    /**
     * write object.
     *
     * @param obj object.
     */
    void writeObject(Object obj) throws IOException;
}
