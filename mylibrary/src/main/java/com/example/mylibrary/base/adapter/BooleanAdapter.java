package com.example.mylibrary.base.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * {@link Gson} boolean 类型数据转换Adapter
 */
public class BooleanAdapter extends TypeAdapter<Boolean> {
  @Override public void write(JsonWriter jsonWriter, Boolean value) throws IOException {
    if (value == null) {
      jsonWriter.nullValue();
    } else {
      jsonWriter.value(value);
    }
  }

  @Override public Boolean read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    } else if (jsonReader.peek() == JsonToken.BOOLEAN) {
      return jsonReader.nextBoolean();
    } else {
      String e = jsonReader.nextString();
      if ("0".equals(e)) return false;
      if ("false".equals(e.toLowerCase())) return false;
      return true;
    }
  }
}