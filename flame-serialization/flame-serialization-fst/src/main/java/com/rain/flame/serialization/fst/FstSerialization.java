/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rain.flame.serialization.fst;


import com.rain.flame.common.URL;
import com.rain.flame.serialization.ObjectInput;
import com.rain.flame.serialization.ObjectOutput;
import com.rain.flame.serialization.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FstSerialization implements Serialization {
    public FstSerialization(){
    }
    @Override
    public byte getContentTypeId() {
        return 9;
    }

    @Override
    public String getContentType() {
        return "x-application/fst";
    }

    @Override
    public ObjectOutput serialize(URL url, OutputStream out) throws IOException {
        return new FstObjectOutput(out);
    }

    @Override
    public ObjectInput deserialize(URL url, InputStream is) throws IOException {
        return new FstObjectInput(is);
    }
}
