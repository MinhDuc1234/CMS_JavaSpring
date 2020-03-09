package com.eureka.service.Entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.eureka.service.Config.ValueConfig;
import com.eureka.service.Core.Response.ResponseFilter;
import com.eureka.service.Interface.Entity.IBaseEntity;
import com.eureka.service.Util.StringUtil;
import com.eureka.service.Validator.Setting.IInputIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseEntity implements Serializable, IBaseEntity {

    private static final long serialVersionUID = 1L;

    @IInputIgnore
    @ApiModelProperty(hidden = true)
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    protected Boolean isDelete = false;

    @JsonIgnore
    public Boolean getIsDelete() {
        return this.isDelete == null ? false : this.isDelete;
    }
    @JsonProperty
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    @ApiModelProperty(hidden = true)
    @Column(name = ValueConfig.ENTITY_CREATED_BY)
    protected String createdBy;

    @ApiModelProperty(hidden = true)
    @Column(name = "changedBy")
    protected String changedBy;

    @ApiModelProperty(hidden = true, example = ValueConfig.DATE_DEFAULT)
    @Column(name = "changedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    protected Date changedDate;

    @ApiModelProperty(hidden = true, example = ValueConfig.DATE_DEFAULT)
    @Column(name = "createdDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ValueConfig.DATE_FORMAT)
    protected Date createdDate;

    @ApiModelProperty(hidden = true)
    @Version
    @JsonIgnore
    private Long version;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private String query;

    protected void preSaveOrUpdate() {
        this.query = this.getQuery();
        if (this.query == null)
            this.query = "";
    }

    @PrePersist
    public void prePersist() {
        this.isDelete = false;
        this.createdDate = new Date();
        this.changedDate = new Date();
        this.preSaveOrUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        this.changedDate = new Date();
        this.preSaveOrUpdate();
    }

    public ResponseFilter toResponseFilter() {
        return new ResponseFilter(this.getId(), this.getDisplay());
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getQuery() {
        return StringUtil.toQueryString(this.getId() + " " + this.getDisplay());
    }

    @ApiModelProperty(hidden = true)
    public abstract String getDisplay();

    @ApiModelProperty(hidden = true)
    @Transient
    public abstract String getId();

    @Transient
    public abstract void setId(String id);

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public Boolean isNewItem() {
        return (this.getId() == null || this.getId().trim().equals("") ? true : false);
    }

}