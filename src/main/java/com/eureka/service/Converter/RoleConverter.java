package com.eureka.service.Converter;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.eureka.service.Core.Role;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

	@Override
	public String convertToDatabaseColumn(Role dict) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson.toJson(dict.getMap());
	}

	@Override
	public Role convertToEntityAttribute(String json) {
		if (json == null || json.trim().isEmpty())
			return new Role() {
				{
					setMap(new HashMap<>());
				}
			};

		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<Map<String, Long>>() {
			private static final long serialVersionUID = 1L;
		}.getType();
		return new Role() {
			{
				setMap(gson.fromJson(json, type));
			}
		};
	}

}