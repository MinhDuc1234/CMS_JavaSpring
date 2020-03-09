package com.eureka.service.Entity;

import javax.persistence.Id;

import com.eureka.service.Sequence.BaseSequence;
import com.eureka.service.Validator.IId;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileEntity extends BaseEntity {

    private static final long serialVersionUID = -1549108044549010955L;

    @IId
    @Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.SEQUENCE, generator = "Seq_File")
    @org.hibernate.annotations.GenericGenerator(name = "Seq_File", strategy = "com.eureka.service.Sequence.DefaultSequence", parameters = {
            @org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "F"),
            @org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%09d") })
    protected String fileCode;

    protected String filePath;

    protected String mimeType;

    protected String dataType;

    @Override
    public String getId() {
        return this.getFileCode();
    }

    @Override
    public void setId(String id) {
        this.setFileCode(id);
    }

    @Override
    public String getDisplay() {
        return this.getFilePath();
    }

}