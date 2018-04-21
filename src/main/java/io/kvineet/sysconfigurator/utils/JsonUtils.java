package io.kvineet.sysconfigurator.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
	
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	public static <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, typeReference);
	}
	
	public static String toJson(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public static <T> T fromJson(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(str, clazz);
	}
	

}
