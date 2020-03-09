package com.eureka.service.Entity;

import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.eureka.service.Converter.RoleEnumConverter;
import com.eureka.service.Enum.RoleEnum;
import com.eureka.service.Interface.RoleAccess.IRoleAccess;
import com.eureka.service.Sequence.BaseSequence;
import com.eureka.service.Validator.IEmail;
import com.eureka.service.Validator.IId;
import com.eureka.service.Validator.IMaxLength;
import com.eureka.service.Validator.IMinLength;
import com.eureka.service.Validator.IUsername;
import com.eureka.service.Validator.Input.IInputEmail;
import com.eureka.service.Validator.Input.IInputSelectStatic;
import com.eureka.service.Validator.Input.IInputText;
import com.eureka.service.Validator.Setting.IInputDisableOnUpdate;
import com.eureka.service.Validator.Setting.IInputSort;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class UserEntity extends BaseEntity {

    private static final long serialVersionUID = 0L;

    @IId
    @Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.SEQUENCE, generator = "Seq_User_Master")
    @org.hibernate.annotations.GenericGenerator(name = "Seq_User_Master", strategy = "com.eureka.service.Sequence.DefaultSequence", parameters = {
            @org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "U"),
            @org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%019d") })
    @ApiModelProperty(required = true)
    protected String userCode;

    @Override
    public String getId() {
        return this.userCode;
    }

    @Override
    public void setId(final String id) {
        this.userCode = id;
    }

    @JsonIgnore
    @IInputSelectStatic(param = { "ROLE_ADMIN", "ROLE_USER" })
    @Getter(value = AccessLevel.NONE)
    @Convert(converter = RoleEnumConverter.class)
    protected RoleEnum roleEnum = RoleEnum.ROLE_USER;

    public RoleEnum getRoleEnum() {
        if (this.roleEnum == null) return RoleEnum.ROLE_USER;
        return this.roleEnum; 
    }

    @IUsername
    @IInputText
    @IInputSort
    @IInputDisableOnUpdate
    @IMinLength(param = 1)
    @IMaxLength(param = 50)
    protected String username;

    @IEmail
    @IInputEmail
    @IInputSort
    protected String emailAddress;

    @Transient
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    protected IRoleAccess roleAccess;

}