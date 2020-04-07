package cqu.ChatSystem.Utils.Serializer;


import cqu.ChatSystem.Utils.Serializer.imp.JSONSerializer;

public interface Serializer {

    Serializer DEFAULT=new JSONSerializer();

    int getSerializerAlgorithm();

    String serialize(Object object);

    <T> T deserialize(Class<T> clazz,String serObj);
}
