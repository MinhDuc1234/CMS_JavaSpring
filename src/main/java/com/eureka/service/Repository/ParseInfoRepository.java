package com.eureka.service.Repository;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.GenericList;
import com.eureka.service.Core.ServerQuery;
import com.eureka.service.Core.SystemUser;
import com.eureka.service.Core.Hql.WhereClause;
import com.eureka.service.Core.Request.RequestFilter;
import com.eureka.service.Core.Response.ResponseData;
import com.eureka.service.Core.UI.Column;
import com.eureka.service.Core.UI.Column.MinMaxValue;
import com.eureka.service.Core.UI.Input;
import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Interface.Enum.IEnum;
import com.eureka.service.Util.EntityInput;
import com.eureka.service.Util.ListUtil;
import com.eureka.service.Util.StringUtil;
import com.eureka.service.Validator.Setting.IPermission;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class ParseInfoRepository<T extends BaseEntity> {

    @Value("${server.file-path:./template/}")
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    protected String templateRoot;

    protected String tableName;
    protected IPermission iPerm;
    protected Class<?> tableType;
    protected List<Field> fields;
    protected List<Input> inputs;
    protected List<Input> columns;
    protected Set<String> canSortSet;
    protected Map<String, String> fieldMapper;
    protected Map<String, Class<?>> enumField;
    protected Set<String> foreignField;
    protected Set<String> booleanField;
    protected String displayKey = "display";
    protected String queryKey = "query";
    protected String primaryKey;
    protected Boolean manualPrimaryKey = false;

    @Autowired
    protected Validator validator;

    public ParseInfoRepository() {
        Type superClass = this.getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Entity annotation = ((Class<?>) type).getAnnotation(Entity.class);
        this.iPerm = ((Class<?>) type).getAnnotation(IPermission.class);
        this.tableName = annotation.name();
        this.tableType = (Class<?>) type;
        this.fields = EntityInput.getFields(this.tableType);
        this.inputs = EntityInput.getInputs(this.fields);
        this.canSortSet = this.inputs.stream().filter(t -> t.getCanSort()).map(t -> t.getName()).collect(Collectors.toSet());
        this.foreignField = EntityInput.getForeignField(this.fields);
        this.foreignField.add(ValueConfig.ENTITY_CREATED_BY);
        this.enumField = EntityInput.getEnumField(this.fields);
        this.booleanField = EntityInput.getBooleanField(this.fields);
        this.primaryKey = EntityInput.getPrimaryKey(this.fields);
        this.manualPrimaryKey = EntityInput.getManualPrimaryKey(this.fields);
    }

    @SuppressWarnings("unchecked")
    protected void createExcelTemplate() throws Exception {
        List<Column> columns = EntityInput.getColumns(this.fields);
        this.fieldMapper = new HashMap<>();
        columns.forEach(t -> this.fieldMapper.put(t.getName(), t.getFieldName()));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Import");

        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
        DataFormat fmt = workbook.createDataFormat();

        // Header
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);
        headerStyle.setFont(font);
        XSSFRow row = sheet.createRow(0);
        for (int i = 0, j = 0; j < columns.size(); j++) {
            Column column = columns.get(j);

            DataValidationConstraint dvConstraint = null;
            String message = null;
            CellStyle textStyle = workbook.createCellStyle();
            switch (column.getColumnType()) {
            case SELECT:
                dvConstraint = dvHelper
                        .createExplicitListConstraint((String[]) ((List<String>) column.getParam()).toArray());
                message = "Please select the value in range " + ((List<String>) column.getParam()).toString();
                textStyle.setDataFormat(fmt.getFormat("@"));
                break;

            case DATE:
                textStyle.setDataFormat(fmt.getFormat(ValueConfig.DATE_FORMAT));
                break;
            case NUMBER:
                textStyle.setDataFormat(fmt.getFormat("#,##0"));
                break;
            case DECIMAL:
                textStyle.setDataFormat(fmt.getFormat("#,##0.00"));
                break;
            case TEXT:
                textStyle.setDataFormat(fmt.getFormat("@"));
                MinMaxValue minMaxValue = (MinMaxValue) column.getParam();
                if (minMaxValue.getMin() != null || minMaxValue.getMax() != null) {
                    dvConstraint = dvHelper.createTextLengthConstraint(OperatorType.BETWEEN, "0", "32767");
                    if (minMaxValue.getMin() != null)
                        dvConstraint.setFormula1(minMaxValue.getMin().toString());
                    if (minMaxValue.getMax() != null)
                        dvConstraint.setFormula2(minMaxValue.getMax().toString());
                    message = "Text length from " + dvConstraint.getFormula1() + " to " + dvConstraint.getFormula2()
                            + " characters";
                }
                break;
            case NONE:
                continue;
            }

            XSSFCell cell = row.createCell(i);
            cell.setCellValue(column.getName());
            cell.setCellStyle(headerStyle);

            short color = IndexedColors.BLACK1.getIndex();
            if (!column.getNullable()) {
                textStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            textStyle.setTopBorderColor(color);
            textStyle.setLeftBorderColor(color);
            textStyle.setBorderTop(BorderStyle.THIN);
            textStyle.setBorderLeft(BorderStyle.THIN);

            sheet.setDefaultColumnStyle(i, textStyle);

            if (dvConstraint != null) {
                CellRangeAddressList addressList = new CellRangeAddressList(-1, -1, i, i);
                DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                validation.createErrorBox("Error", message);
                validation.setShowErrorBox(true);
                sheet.addValidationData(validation);
            }

            // Auto size
            sheet.autoSizeColumn(i++);
        }

        // Write to file
        FileOutputStream fileOut = new FileOutputStream(this.templateRoot + this.getTableName() + ".xlsx");
        workbook.write(fileOut);

        // Closing the file and workbook
        fileOut.close();
        workbook.close();
    }

    protected ResponseData<String> manualValidate(List<T> entities) {
        List<String> messages = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            final int fi = i;
            Set<ConstraintViolation<T>> violations = this.validator.validate(entity);
            if (violations.size() > 0) {
                messages.add(violations.stream().map(t -> {
                    return "[" + fi + "][" + t.getRootBeanClass().getSimpleName() + "][" + t.getPropertyPath() + "]: "
                            + t.getMessage();
                }).collect(Collectors.joining("\n")));
            }
        }
        if (messages.size() > 0)
            return ResponseData.error(messages);
        return ResponseData.success("");
    }

    protected ResponseData<String> manualValidateFk(List<T> entities) {
        List<String> messages = new ArrayList<>();
        GenericList<T> genericList = new GenericList<>();
        genericList.setList(entities);
        Set<ConstraintViolation<GenericList<T>>> violations = this.validator.validate(genericList);
        messages.add(violations.stream().map(t -> {
            return "[" + t.getRootBeanClass().getSimpleName() + "][" + t.getPropertyPath() + "]: " + t.getMessage();
        }).collect(Collectors.joining("\n")));
        if (messages.size() > 0)
            return ResponseData.error(messages);
        return ResponseData.success("");
    }

    public String getFirstId() {
        return "0000000000";
    }

    protected List<WhereClause> getFilterEnumQuery(Map<String, List<String>> filterEnum) {
        List<WhereClause> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : filterEnum.entrySet()) {
            if (this.enumField.containsKey(entry.getKey()) && (entry.getValue() != null && entry.getValue().size() > 0)) {
                list.add(new WhereClause(entry.getKey(), "IN", ListUtil.unique(entry.getValue().stream().map(t -> {
                    if (this.enumField.get(entry.getKey()) != null) {
                        Object obj = IEnum.fromString(t, this.enumField.get(entry.getKey()));
                        return obj;
                    }
                    return t;
                }).collect(Collectors.toList()))));
            }
        }
        return list;
    }

    protected List<WhereClause> getFilterForeignKeyQuery(Map<String, List<String>> filterForeignKey) {
        List<WhereClause> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : filterForeignKey.entrySet()) {
            if (this.foreignField.contains(entry.getKey())
                    && (entry.getValue() != null && entry.getValue().size() > 0)) {
                list.add(new WhereClause(entry.getKey(), "IN", ListUtil.unique(entry.getValue())));
            }
        }
        return list;
    }

    protected List<WhereClause> getFilterBooleanQuery(Map<String, Boolean> filterBoolean) {
        List<WhereClause> list = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : filterBoolean.entrySet()) {
            if (this.booleanField.contains(entry.getKey()) && entry.getValue() != null) {
                list.add(new WhereClause(entry.getKey(), "=", entry.getValue()));
            }
        }
        return list;
    }

    protected void updateServerQuery(ServerQuery serverQuery, RequestFilter filterQuery) {
        if (filterQuery.getQueryString() != null) {
            String queryString = StringUtil.toQueryString(filterQuery.getQueryString());
            if (queryString.length() > 0) {
                serverQuery.getWhereClauses().add(new WhereClause(this.getQueryKey(), "LIKE", "%" + queryString + "%"));
            }
        }
        if (filterQuery.getFilterEnum() != null) {
            serverQuery.addWhereClauses(this.getFilterEnumQuery(filterQuery.getFilterEnum()));
        }
        if (filterQuery.getFilterForeignKey() != null) {
            serverQuery.addWhereClauses(this.getFilterForeignKeyQuery(filterQuery.getFilterForeignKey()));
        }
        if (filterQuery.getFilterCheckbox() != null) {
            serverQuery.addWhereClauses(this.getFilterBooleanQuery(filterQuery.getFilterCheckbox()));
        }
        if (filterQuery.getExcludeIds() != null && filterQuery.getExcludeIds().size() > 0) {
            serverQuery.getWhereClauses()
                    .add(new WhereClause(this.getPrimaryKey(), "NOT IN", filterQuery.getExcludeIds()));
        }
        if (filterQuery.getOrders() != null) {
            serverQuery.setOrders(filterQuery.getOrders());
        }
    }

    protected final void initSave(T entity, SystemUser systemUser) {
        entity.setCreatedBy(systemUser.getId());
        entity.setChangedBy(systemUser.getId());
    }

    protected final void initUpdate(T newEntity, T oldEntity, SystemUser systemUser) {
        newEntity.setId(oldEntity.getId());
        newEntity.setVersion(oldEntity.getVersion());
        newEntity.setIsDelete(oldEntity.getIsDelete());
        newEntity.setChangedBy(systemUser.getId());
        newEntity.setCreatedBy(oldEntity.getCreatedBy());
        newEntity.setChangedDate(oldEntity.getChangedDate());
        newEntity.setCreatedDate(oldEntity.getCreatedDate());
    }

}