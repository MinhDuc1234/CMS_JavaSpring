package com.eureka.service.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Converter
public class ListConverter implements AttributeConverter<List<?>, String> {

	@Override
	public String convertToDatabaseColumn(List<?> list) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(list);
	}

	@Override
	public List<?> convertToEntityAttribute(String json) {
		if (json == null || json.trim().isEmpty())
			return new ArrayList<>();

		Gson gson = new Gson();
		return gson.fromJson(json, List.class);
	}

}