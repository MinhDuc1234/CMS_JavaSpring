package com.eureka.service.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.Access;
import com.eureka.service.Core.ServerQuery;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.Hql.WhereClause;
import com.eureka.service.Core.Request.QuickUpdate;
import com.eureka.service.Core.Request.RequestFilter;
import com.eureka.service.Core.Request.RequestPage;
import com.eureka.service.Core.Request.Sort;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.Response.ResponseFilter;
import com.eureka.service.Core.Response.ResponsePage;
import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Enum.Action;
import com.eureka.service.Interface.Callback.UpdateAsLineItemsCallback;
import com.eureka.service.Interface.Callback.UpdateLineItemCallback;
import com.eureka.service.Interface.Repository.IBaseRepository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.Getter;

@Repository
@Component
@Getter
@SuppressWarnings(ValueConfig.UNCHECKED)
public abstract class BaseRepository<T extends BaseEntity> extends ParseInfoRepository<T> implements IBaseRepository {

    @PersistenceContext
    protected EntityManager entityManager;

    @PostConstruct
    private void init() {
        try {
            this.createExcelTemplate();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // These functions below should be modified in sub class
    protected void beforeSave(T doc, SystemUser systemUser) throws Exception {
    }

    protected void afterSave(T doc, SystemUser systemUser) throws Exception {
    }

    protected void beforeUpdate(T newDoc, T oldDoc, SystemUser systemUser) throws Exception {
    }

    protected void afterUpdate(T newDoc, T oldDoc, SystemUser systemUser) throws Exception {
    }

    protected TypedQuery<?> createTypedQuery(ServerQuery serverQuery, Boolean sort, Class<?> clazz) {
        String query = "SELECT " + serverQuery.getSelect() + " FROM " + this.getTableName() + " c";

        // JOIN
        if (serverQuery.getJoins().size() > 0) {
            query += " JOIN " + serverQuery.getJoins().stream().map(e -> {
                return e.toClause();
            }).collect(Collectors.joining(", "));
        }

        // WHERE
        Map<String, Object> param = new HashMap<String, Object>();
        if (serverQuery.getWhereClauses().size() > 0) {
            query += " WHERE " + serverQuery.getWhereClauses().stream().map(e -> {
                String _key = e.getKey();
                String _ext = "";
                while (param.containsKey(_key)) {
                    _key += "_";
                    _ext += "_";
                }

                param.put(_key, e.getValue());
                return e.toClause() + _ext;
            }).collect(Collectors.joining(" AND "));
        }

        // WHERE OR
        if (serverQuery.getWhereOrClauses().size() > 0) {
            if (serverQuery.getWhereClauses().size() == 0) {
                query += " WHERE ";
            } else {
                query += " AND ";
            }

            query += serverQuery.getWhereOrClauses().stream().map(where -> {
                return "(" + where.stream().map(e -> {
                    String _key = e.getKey();
                    String _ext = "";
                    while (param.containsKey(_key)) {
                        _key += "_";
                        _ext += "_";
                    }

                    param.put(_key, e.getValue());
                    return e.toClause() + _ext;
                }).collect(Collectors.joining(" OR ")) + ")";
            }).collect(Collectors.joining(" AND "));
        }

        // SORT BY
        if (sort && serverQuery.getOrders() != null) {
            List<Sort> sorts = serverQuery.getOrders().stream().filter(e -> this.canSortSet.contains(e.getActive()))
                    .collect(Collectors.toList());
            if (sorts.size() > 0) {
                query += " ORDER BY " + sorts.stream().map(e -> e.getActive() + " " + e.getDirection())
                        .collect(Collectors.joining(", "));
            }
        }

        TypedQuery<?> typedQuery = this.entityManager.createQuery(query, clazz);
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }

        return typedQuery;
    }

    protected TypedQuery<?> createTypedQuery(ServerQuery serverQuery, Boolean sort) {
        return this.createTypedQuery(serverQuery, sort, this.getTableType());
    }

    // These functions below will connect with Database
    public void clear() {
        this.entityManager.clear();
    }

    public ResponseData<T> persist(T entity) {
        this.entityManager.persist(entity);
        return new ResponseData<>(entity);
    }

    public ResponseData<T> merge(T newEntity) {
        return new ResponseData<>(this.entityManager.merge(newEntity));
    }

    public Stream<T> getResultStream(ServerQuery serverQuery) {
        TypedQuery<T> typedQuery = (TypedQuery<T>) this.createTypedQuery(serverQuery, true);
        typedQuery.setFirstResult(serverQuery.getSkip());
        if (serverQuery.getLimit() > 0) {
            typedQuery.setMaxResults(serverQuery.getLimit());
        }
        return typedQuery.getResultStream();
    }

    public ResponseData<List<ResponseFilter>> filter(RequestFilter filterQuery) {
        ServerQuery serverQuery = new ServerQuery();
        this.updateServerQuery(serverQuery, filterQuery);

        TypedQuery<T> typedQuery = (TypedQuery<T>) this.createTypedQuery(serverQuery, true, this.getTableType());
        typedQuery.setFirstResult(0);
        if (filterQuery.getFetchAll() != true) {
            typedQuery.setMaxResults(10);
        }
        return new ResponseData<>(
                typedQuery.getResultStream().map(t -> t.toResponseFilter()).collect(Collectors.toList()));
    }

    public ResponseData<List<ResponseFilter>> getNames(List<String> ids) {
        if (ids.size() == 0) {
            return new ResponseData<>(new ArrayList<>());
        }
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.getWhereClauses().add(new WhereClause(this.getPrimaryKey(), "IN", ids));

        TypedQuery<T> typedQuery = (TypedQuery<T>) this.createTypedQuery(serverQuery, true, this.getTableType());
        return new ResponseData<>(
                typedQuery.getResultStream().map(t -> t.toResponseFilter()).collect(Collectors.toList()));
    }

    public ResponseData<Long> count(ServerQuery serverQuery) {
        serverQuery.setSelect("COUNT(c)");

        TypedQuery<Long> typedQuery = (TypedQuery<Long>) this.createTypedQuery(serverQuery, false, Long.class);
        Long total = typedQuery.getSingleResult();
        return ResponseData.success(total);
    }

    public ResponseData<Long> count(RequestFilter filterQuery, SystemUser systemUser) {
        Access access = systemUser.getRoleAccess().getReadAccess(this.getIPerm(), Action.READ, systemUser);
        if (!access.getCanAccess())
            return ResponseData.forbidden();

        ServerQuery serverQuery = access.getServerQuery();
        this.updateServerQuery(serverQuery, filterQuery);
        return this.count(serverQuery);
    }

    // These functions below will be called from Controller
    protected ResponseData<ResponsePage<T>> getAll(ServerQuery serverQuery) {
        ResponsePage<T> page = new ResponsePage<T>();
        page.setRecords(this.getResultStream(serverQuery).collect(Collectors.toList()));
        page.setTotalRecord(this.count(serverQuery).getData());
        page.setCurrentPage(serverQuery.getSkip() / serverQuery.getLimit());
        page.setBlockSize(serverQuery.getLimit());
        page.updateInfo();
        return new ResponseData<>(page);
    }

    public ResponseData<ResponsePage<T>> getAll(SystemUser systemUser, RequestPage requestPage) {
        Access access = systemUser.getRoleAccess().getReadAccess(this.getIPerm(), Action.READ, systemUser);
        if (!access.getCanAccess())
            return ResponseData.forbidden();

        ServerQuery serverQuery = access.getServerQuery();
        serverQuery.setSkip((requestPage.getPageNumber() - 1) * requestPage.getNumberRecord());
        serverQuery.setLimit(requestPage.getNumberRecord());
        this.updateServerQuery(serverQuery, requestPage);
        return this.getAll(serverQuery);
    }

    public ResponseData<ResponsePage<T>> getAll(SystemUser systemUser, ServerQuery serverQuery) {
        return this.getAll(serverQuery);
    }

    public ResponseData<List<T>> fetchAll(SystemUser systemUser, RequestPage requestPage) {
        Access access = systemUser.getRoleAccess().getReadAccess(this.getIPerm(), Action.READ, systemUser);
        if (!access.getCanAccess())
            return ResponseData.forbidden();
        ServerQuery serverQuery = access.getServerQuery();
        serverQuery.setLimit(-1);
        this.updateServerQuery(serverQuery, requestPage);
        return ResponseData.success(this.getResultStream(serverQuery).collect(Collectors.toList()));
    }

    public ResponseData<T> findById(String id, SystemUser systemUser) {
        Access access = systemUser.getRoleAccess().getReadAccess(this.getIPerm(), Action.READ, systemUser);
        if (!access.getCanAccess())
            return ResponseData.forbidden();

        ServerQuery serverQuery = access.getServerQuery();
        serverQuery.getWhereClauses().add(new WhereClause(this.getPrimaryKey(), "=", id));
        Optional<T> optional = this.getResultStream(serverQuery).findFirst();
        if (!optional.isPresent())
            return ResponseData.notFound();
        return new ResponseData<>(optional.get());
    }

    public ResponseData<T> findById(String id) {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.getWhereClauses().add(new WhereClause(this.getPrimaryKey(), "=", id));
        Optional<T> optional = this.getResultStream(serverQuery).findFirst();
        if (!optional.isPresent())
            return ResponseData.notFound();
        return new ResponseData<>(optional.get());
    }

    protected ResponseData<List<T>> findByIds(List<String> ids) {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.getWhereClauses().add(new WhereClause(this.getPrimaryKey(), "IN", ids));
        List<T> entities = this.getResultStream(serverQuery).collect(Collectors.toList());
        return new ResponseData<>(entities);
    }

    //
    protected ResponseData<T> save(T entity, SystemUser systemUser, Boolean fetchData) throws Exception {
        if (!systemUser.getRoleAccess().getWriteAccess(this.getIPerm(), Action.CREATE, systemUser)) {
            return ResponseData.forbidden();
        }
        this.initSave(entity, systemUser);
        this.beforeSave(entity, systemUser);

        ResponseData<T> responseData = this.persist(entity);
        if (responseData.getStatus() == false)
            return responseData;

        this.afterSave(entity, systemUser);
        if (fetchData) {
            return this.findById(entity.getId());
        }
        return new ResponseData<>(entity);
    }

    public ResponseData<T> save(T entity, SystemUser systemUser) throws Exception {
        return this.save(entity, systemUser, true);
    }

    protected ResponseData<List<T>> saveMultiple(List<T> entities, SystemUser systemUser) throws Exception {
        if (!systemUser.getRoleAccess().getWriteAccess(this.getIPerm(), Action.CREATE, systemUser)) {
            return ResponseData.forbidden();
        }
        int i = 0;
        for (T entity : entities) {
            this.initSave(entity, systemUser);
            this.beforeSave(entity, systemUser);
            ResponseData<T> responseData = this.persist(entity);
            if (responseData.getStatus() == false)
                return ResponseData.from(responseData);
            this.afterSave(entity, systemUser);
            if (++i % 100 == 0) {
                this.entityManager.flush();
                this.entityManager.clear();
            }
        }
        return new ResponseData<>(entities);
    }

    public ResponseData<List<T>> saves(List<T> entities, SystemUser systemUser) throws Exception {
        return this.saves(entities, systemUser, true);
    }

    public ResponseData<List<T>> saves(List<T> entities, SystemUser systemUser, Boolean validFk) throws Exception {
        ResponseData<String> validateData = this.manualValidate(entities);
        if (validateData.getStatus() == false)
            return ResponseData.from(validateData);

        if (validFk == false) {
            ResponseData<String> validateFkData = this.manualValidateFk(entities);
            if (validateFkData.getStatus() == false)
                return ResponseData.from(validateFkData);
        }

        ResponseData<List<T>> responseData = this.saveMultiple(entities, systemUser);
        if (responseData.getStatus() == false)
            return responseData;
        return responseData;
    }

    protected final ResponseData<Boolean> delete(T entity) {
        entity.setIsDelete(true);
        this.merge(entity);
        return new ResponseData<>(true);
    }

    public ResponseData<Boolean> delete(String id, SystemUser systemUser) {
        ResponseData<T> responseData = this.findById(id, systemUser);
        if (!responseData.getStatus())
            return ResponseData.from(responseData);

        if (!systemUser.getRoleAccess().getWriteAccess(this.getIPerm(), Action.DELETE, systemUser,
                responseData.getData())) {
            return ResponseData.forbidden();
        }
        return this.delete(responseData.getData());
    }

    public ResponseData<T> update(T newDoc, SystemUser systemUser) throws Exception {
        ResponseData<T> responseData = this.findById(newDoc.getId());

        if (!responseData.getStatus()) {
            return ResponseData.from(responseData);
        }
        T oldDoc = responseData.getData();
        if (!systemUser.getRoleAccess().getWriteAccess(this.getIPerm(), Action.UPDATE, systemUser, oldDoc)) {
            return ResponseData.forbidden();
        }

        this.initUpdate(newDoc, oldDoc, systemUser);
        this.beforeUpdate(newDoc, oldDoc, systemUser);
        ResponseData<T> result = this.merge(newDoc);
        this.afterUpdate(result.getData(), oldDoc, systemUser);
        return result;
    }

    //
    public ResponseData<Boolean> quickUpdateField(QuickUpdate quickUpdate, SystemUser systemUser) throws Exception {
        if (!this.booleanField.contains(quickUpdate.getFieldName())) {
            return ResponseData.error("Field " + quickUpdate.getFieldName() + ": is not exists");
        }
        ResponseData<T> responseData = this.findById(quickUpdate.getId());
        if (!responseData.getStatus()) {
            return ResponseData.from(responseData);
        }
        T oldDoc = responseData.getData();
        if (!systemUser.getRoleAccess().getWriteAccess(this.getIPerm(), Action.UPDATE, systemUser, oldDoc)) {
            return ResponseData.forbidden();
        }
        Field field = oldDoc.getClass().getDeclaredField(quickUpdate.getFieldName());
        field.setAccessible(true);
        field.set(oldDoc, quickUpdate.getFieldValue());
        field.setAccessible(false);
        this.merge(oldDoc);
        return new ResponseData<>(true);
    }

    public ResponseData<Boolean> updateAsLineItems(List<T> entities, UpdateLineItemCallback<T> updateLineItemCallback,
            SystemUser systemUser) throws Exception {
        if (entities == null) {
            return ResponseData.success(null);
        }

        entities.forEach(t -> {
            updateLineItemCallback.updateLineItem(t);
        });
        ResponseData<String> validateData = this.manualValidate(entities);
        if (validateData.getStatus() == false) {
            return ResponseData.from(validateData);
        }

        for (T entity : entities) {
            if (entity.isNewItem()) {
                continue;
            }

            ResponseData<?> updateResData = null;
            if (entity.getIsDelete() == true) {
                updateResData = this.delete(entity.getId(), systemUser);
            } else {
                updateResData = this.update(entity, systemUser);
            }
            if (updateResData.getStatus() == false) {
                return ResponseData.from(updateResData);
            }
        }

        ResponseData<List<T>> savesResData = this.saveMultiple(
                entities.stream().filter(t -> t.isNewItem() == true).collect(Collectors.toList()), systemUser);
        if (savesResData.getStatus() == false)
            return ResponseData.from(savesResData);

        return new ResponseData<>(true);
    }

    protected List<UpdateAsLineItemsCallback<T>> updateAsLineItemsCallbacks;

    protected ResponseData<T> internalSaveWithLineItems(T entity, SystemUser systemUser) throws Exception {
        ResponseData<String> validateData = this.manualValidate(Arrays.asList(entity));
        if (validateData.getStatus() == false)
            return ResponseData.from(validateData);

        return this.save(entity, systemUser, false);
    }

    protected ResponseData<T> internalUpdateWithLineItems(T entity, SystemUser systemUser) throws Exception {
        ResponseData<String> validateData = this.manualValidate(Arrays.asList(entity));
        if (validateData.getStatus() == false)
            return ResponseData.from(validateData);

        return this.update(entity, systemUser);
    }

    public ResponseData<T> saveWithLineItems(T entity, SystemUser systemUser) throws Exception {
        ResponseData<T> responseData = this.internalSaveWithLineItems(entity, systemUser);
        if (responseData.getStatus() == false)
            return responseData;

        if (this.updateAsLineItemsCallbacks != null) {
            for (UpdateAsLineItemsCallback<T> updateAsLineItemsCallback : this.updateAsLineItemsCallbacks) {
                ResponseData<Boolean> updateResponseData = updateAsLineItemsCallback.updateAsLineItems(entity,
                        systemUser);
                if (updateResponseData.getStatus() == false)
                    return ResponseData.from(updateResponseData);
            }
        }
        return responseData;
    }

    public ResponseData<T> updateWithLineItems(T entity, SystemUser systemUser) throws Exception {
        ResponseData<T> responseData = this.internalUpdateWithLineItems(entity, systemUser);
        if (responseData.getStatus() == false)
            return responseData;

        if (this.updateAsLineItemsCallbacks != null) {
            for (UpdateAsLineItemsCallback<T> updateAsLineItemsCallback : this.updateAsLineItemsCallbacks) {
                ResponseData<Boolean> updateResponseData = updateAsLineItemsCallback.updateAsLineItems(entity,
                        systemUser);
                if (updateResponseData.getStatus() == false)
                    return ResponseData.from(updateResponseData);
            }
        }

        return responseData;
    }

}