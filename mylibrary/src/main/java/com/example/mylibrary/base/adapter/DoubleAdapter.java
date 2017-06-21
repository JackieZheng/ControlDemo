package com.example.mylibrary.base.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * {@link Gson} double 类型数据转换Adapter
 *
 *
 * <pre>
 * 一. 在没有添加   compile 'com.google.code.gson:gson:2.8.1' 时
 *     只能继承 {@link TypeAdapter}.
 *     实现 {@link JsonSerializer} 和 {@link JsonDeserializer} 接口无效.
 *  1. 在 {@link GsonBuilder} 中注册
 *      序列化时    调用 {@link DoubleAdapter#write(JsonWriter, Number)}
 *      反序列化时  调用 {@link DoubleAdapter#read(JsonReader)}
 *
 *  2. 作为字段注解时
 *      序列化时    不会调用 {@link DoubleAdapter#write(JsonWriter, Number)}
 *      反序列化时  调用 {@link DoubleAdapter#read(JsonReader)}
 *
 * 二. 添加 compile 'com.google.code.gson:gson:2.8.1' 时
 *  1. 在 {@link GsonBuilder} 中注册,只能继承 {@link TypeAdapter}.
 *     实现 {@link JsonSerializer} 和 {@link JsonDeserializer} 接口无效.
 *
 *  2. 作为字段注解时
 *      继承 {@link TypeAdapter}. 或实现 {@link JsonSerializer} 和 {@link JsonDeserializer}接口都可以
 *      同时存在时,优先使用 继承 {@link TypeAdapter}
 *      序列化方法,和反序列化方法都会被调用
 *
 *  </pre>
 */
public class DoubleAdapter extends TypeAdapter<Number>
    implements JsonSerializer<Double>, JsonDeserializer<Double> {

  @Override
  public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src);
  }

  @Override
  public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return json.getAsDouble();
  }

  @Override public void write(JsonWriter jsonWriter, Number number) throws IOException {
    if (number == null) {
      jsonWriter.nullValue();
      return;
    }
    jsonWriter.value(number);
  }

  @Override public Number read(JsonReader jsonReader) throws IOException {
    if (jsonReader.peek() == JsonToken.NULL) {
      jsonReader.nextNull();
      return null;
    }

    try {
      String value = jsonReader.nextString();
      if ("".equals(value)) {
        return null;
      }
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    }
  }
}
