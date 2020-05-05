package com.xinyan.Encoder;

import com.alibaba.fastjson.JSON;

/**
 * @author xinyan.xie
 * @description
 * @date 2020/5/5
 */
public class JSONSerializer implements Serializer{



    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }



    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);

    }

}
