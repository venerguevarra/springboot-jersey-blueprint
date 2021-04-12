package com.doesitwork.springboot.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.doesitwork.springboot.domain.enums.EntityStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@SuppressWarnings("serial")
public abstract class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    @JsonProperty(access = Access.READ_WRITE)
    protected UUID id;

    @Column(nullable = false, updatable = false, insertable = true)
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Manila")
    protected Date createdDate;

    @Column(columnDefinition = "BINARY(16)", nullable = true)
    @JsonProperty(access = Access.READ_WRITE)
    protected UUID createdBy;

    @Column(nullable = true, updatable = true, insertable = false)
    @UpdateTimestamp
    @Temporal(TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Manila")
    protected Date modifiedDate;

    @Column(columnDefinition = "BINARY(16)", nullable = true)
    @JsonProperty(access = Access.READ_WRITE)
    protected UUID modifiedBy;

    @Enumerated
    @Column(columnDefinition = "SMALLINT")
    protected EntityStatus active;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}