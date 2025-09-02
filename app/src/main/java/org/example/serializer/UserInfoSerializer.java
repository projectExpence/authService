package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.dto.UserInfoDto;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoDto> {

    private final ObjectMapper objectMapper=new ObjectMapper();
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, UserInfoDto userInfoDto) {
        if(userInfoDto==null){
            return new byte[0];
        }
        try{
            return objectMapper.writeValueAsString(userInfoDto)
                    .getBytes(StandardCharsets.UTF_8);
        } catch (Exception e){
            throw new RuntimeException("Error serializing userData");
        }
    }
    @Override
    public void close(){

    }
}
