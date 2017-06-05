package liubin.com.myapplication.bean;

import android.util.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * xxtea 解密 double 加密数据
 */
public class DoubleAdapter extends TypeAdapter<Double>
    implements JsonSerializer<Double>, JsonDeserializer<Double> {

  @Override
  public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
    JsonPrimitive primitive = new JsonPrimitive(src);
    return primitive;
  }

  @Override
  public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return json.getAsDouble();
  }

  @Override public void write(JsonWriter out, Double value) throws IOException {
    Log.e("adsfasdf", "asdf");
  }

  @Override public Double read(JsonReader in) throws IOException {
    return in.nextDouble();
  }
}
