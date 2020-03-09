package com.eureka.service.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.eureka.service.Core.FkCode;
import com.eureka.service.Core.FkError;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.Response.ResponseFilter;
import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Security.JwtTokenProvider;
import com.eureka.service.Validator.Input.IInputSelectUrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class ForeignCode {
    private String fieldName = "";
    private String validateService = "";
    private Set<String> codes = new HashSet<>();
}

@Service
public class ForeignKeyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ElasticLoggingService elasticLoggingService;

    private String bearerToken;

    @PostConstruct
    void initJwtToken() {
        SystemUser systemUser = new SystemUser();
        systemUser.setId("0");
        systemUser.setRoleEnum(RoleEnum.ROLE_ADMIN);
        this.bearerToken = "Bearer " + this.jwtTokenProvider.createToken(systemUser);
    }

    public static Map<String, List<FkCode>> map = new HashMap<>();

    private static String convertUrlToServiceUrl(String url) {
        String[] strings = url.split("\\/");
        if (strings.length < 2)
            return url;
        List<String> arr = Arrays.asList(strings);
        arr.set(0, url.startsWith("/") ? "http:/" : "http://");
        arr.set(1, arr.get(1) + "-service");
        return String.join("/", arr).replace("/filter", "");
    }

    public static void register(FkCode fkCode, Class<?> clazz) {
        String packagePath = clazz.getName();
        if (!ForeignKeyService.map.containsKey(packagePath)) {
            ForeignKeyService.map.put(packagePath, new ArrayList<>());
        }

        try {
            Field field = clazz.getDeclaredField(fkCode.getFieldName());
            IInputSelectUrl iInput = field.getAnnotation(IInputSelectUrl.class);
            fkCode.setValidateService(convertUrlToServiceUrl(iInput.param()));
            ForeignKeyService.map.get(packagePath).add(fkCode);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected List<FkError> internalValidate(List<ForeignCode> foreignCodes) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", this.bearerToken);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        return foreignCodes.stream().map(t -> {
            FkError fkError = new FkError();
            fkError.setFieldName(t.getFieldName());
            t.getCodes().removeIf(e -> e == null);
            try {
                if (t.getCodes().size() > 0) {
                    ResponseEntity<ResponseData<List<ResponseFilter>>> responseEntity = this.restTemplate.exchange(
                            t.getValidateService() + "/names/{ids}", HttpMethod.GET, entity,
                            new ParameterizedTypeReference<ResponseData<List<ResponseFilter>>>() {
                            }, String.join(",", t.getCodes()));

                    Set<String> idSet = responseEntity.getBody().getData().stream().map(e -> e.getId())
                            .collect(Collectors.toSet());
                    fkError.setCodes(t.getCodes().stream().filter(e -> !idSet.contains(e)).collect(Collectors.toSet()));
                } else {
                    fkError.setCodes(t.getCodes());
                }
            } catch (HttpClientErrorException e) {
                fkError.setCodes(t.getCodes());
                this.elasticLoggingService.addToQueue(e);
                e.printStackTrace();
            } catch (Exception ex) {
                this.elasticLoggingService.addToQueue(ex);
                fkError.setCodes(t.getCodes());
            }
            return fkError;
        }).filter(t -> t.getCodes().size() > 0).collect(Collectors.toList());
    }

    public <T extends BaseEntity> List<FkError> validate(T obj) {
        List<ForeignCode> foreignCodes = new ArrayList<>();
        if (ForeignKeyService.map.containsKey(obj.getClass().getCanonicalName())) {
            ForeignKeyService.map.get(obj.getClass().getCanonicalName()).forEach(t -> {
                ForeignCode foreignCode = new ForeignCode();
                foreignCode.setValidateService(t.getValidateService());
                foreignCode.setFieldName(t.getFieldName());
                t.getCallback().update(foreignCode.getCodes(), obj);
                foreignCodes.add(foreignCode);
            });
            return this.internalValidate(foreignCodes);
        }
        return new ArrayList<>();
    }

    public <T extends BaseEntity> List<FkError> validate(List<T> objs) {
        List<ForeignCode> foreignCodes = new ArrayList<>();
        String classPath = "";
        if (objs.size() > 0) {
            classPath = objs.get(0).getClass().getCanonicalName();
        }
        if (ForeignKeyService.map.containsKey(classPath)) {
            ForeignKeyService.map.get(classPath).forEach(t -> {
                ForeignCode foreignCode = new ForeignCode();
                foreignCode.setValidateService(t.getValidateService());
                foreignCode.setFieldName(t.getFieldName());
                objs.forEach(obj -> {
                    t.getCallback().update(foreignCode.getCodes(), obj);
                });
                foreignCodes.add(foreignCode);
            });

            return this.internalValidate(foreignCodes);
        }
        return new ArrayList<>();
    }

}