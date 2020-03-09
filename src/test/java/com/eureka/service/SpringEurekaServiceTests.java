package com.eureka.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.eureka.service.Core.Request.RequestFilter;
import com.eureka.service.Core.Request.RequestPage;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.Response.ResponsePage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.RequestMapping;

@FunctionalInterface
interface CheckMatcher {
	void match(String data) throws Exception;
}

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringEurekaServiceTests {

	protected String classPath = "com.eureka.identity.Controller";
	private final Gson gson = new Gson();
	private static String Token = null;
	private List<String> controllers = null;

	public void login() throws Exception {

		Type type = new TypeToken<Map<String, String>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		final Map<String, String> user = new HashMap<>();
		user.put("username", "admin");
		user.put("password", "Abc@@123");
		this.mockMvc.perform(
				post("/api/v1.0/user/signin").content(gson.toJson(user)).header("content-type", "application/json"))
				.andDo(print()).andExpect(status().isOk()).andExpect(getString(json -> {
					Map<String, String> map = this.gson.fromJson(json, type);
					assertTrue(map.containsKey("token"));
					Token = map.get("token");
				}));

	}

	@Before
	public void setup() throws Exception {

		this.login();

		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false);
		provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

		final Set<BeanDefinition> classes = provider.findCandidateComponents(this.classPath);
		this.controllers = classes.stream().map(t -> {
			Class<?> clazz;
			try {
				clazz = Class.forName(t.getBeanClassName());
			} catch (ClassNotFoundException e) {
				return null;
			}
			return clazz.getAnnotation(RequestMapping.class);
		}).filter(t -> t != null).map(t -> t.value()[0]).filter(t -> !t.equals("/api/v1.0/system"))
				.collect(Collectors.toList());

	}

	@Autowired
	private MockMvc mockMvc;

	public <T> ResultMatcher getString(CheckMatcher checkMatcher) {
		return result -> checkMatcher.match(result.getResponse().getContentAsString());
	}

	@Test
	public void getAll() throws Exception {

		Type type = new TypeToken<ResponseData<ResponsePage<Object>>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		final RequestPage requestPage = new RequestPage(1, 10, "");
		for (String string : controllers) {
			System.out.println(string);
			this.mockMvc
					.perform(post(string).content(gson.toJson(requestPage)).header("content-type", "application/json")
							.header("Authorization", "Bearer " + Token))
					.andDo(print()).andExpect(status().isOk()).andExpect(getString(json -> {
						ResponseData<ResponsePage<Object>> responseData = this.gson.fromJson(json, type);

						assertNotNull("1\t" + string, responseData);
						assertTrue("3\t" + string, responseData.getStatus());
						assertTrue("4\t" + string, responseData.getData().getRecords().size() >= 0);
					}));
		}
	}

	@Test
	public void filter() throws Exception {

		Type type = new TypeToken<ResponseData<List<Object>>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		final RequestFilter filterQuery = new RequestFilter("");
		for (String string : controllers) {
			System.out.println(string);
			this.mockMvc
					.perform(post(string + "/filter").content(gson.toJson(filterQuery))
							.header("content-type", "application/json").header("Authorization", "Bearer " + Token))
					.andDo(print()).andExpect(status().isOk()).andExpect(getString(json -> {
						ResponseData<List<Object>> responseData = this.gson.fromJson(json, type);

						assertNotNull("1\t" + string, responseData);
						assertTrue("3\t" + string, responseData.getStatus());
						assertTrue("4\t" + string, responseData.getData().size() >= 0);
					}));
		}
	}

	@Test
	public void getById() throws Exception {

		Type type = new TypeToken<ResponseData<Object>>() {
			private static final long serialVersionUID = 1L;
		}.getType();

		for (String string : controllers) {
			System.out.println(string);
			this.mockMvc.perform(get(string + "/1").header("content-type", "application/json").header("Authorization",
					"Bearer " + Token)).andDo(print()).andExpect(getString(json -> {
						ResponseData<Object> responseData = this.gson.fromJson(json, type);
						assertNotNull("1\t" + string, responseData);
						assertNotNull("2\t" + string, responseData.getStatus());
					}));
		}
	}

	@Test
	public void count() throws Exception {

		Type type = new TypeToken<ResponseData<Long>>() {
			private static final long serialVersionUID = 1L;
		}.getType();
		final RequestFilter filterQuery = new RequestFilter("");

		for (String string : controllers) {
			System.out.println(string);
			this.mockMvc
					.perform(post(string + "/count").content(gson.toJson(filterQuery))
							.header("content-type", "application/json").header("Authorization", "Bearer " + Token))
					.andDo(print()).andExpect(getString(json -> {
						ResponseData<Long> responseData = this.gson.fromJson(json, type);
						assertNotNull("1\t" + string, responseData);
						assertTrue("2\t" + string, responseData.getData() >= 0);
					}));
		}
	}

}
