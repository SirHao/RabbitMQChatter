package cqu.ChatSystem.Utils.Serializer.imp;

import com.alibaba.fastjson.JSON;
import cqu.ChatSystem.Utils.Serializer.Serializer;
import static cqu.ChatSystem.Utils.Serializer.SerializerAlgorithm.JSON_ALG;


public class JSONSerializer implements Serializer {

    @Override
    public int getSerializerAlgorithm() {
        return JSON_ALG;
    }

    @Override
    public String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, String serObj) {
        return JSON.parseObject(serObj,clazz);
    }
}
