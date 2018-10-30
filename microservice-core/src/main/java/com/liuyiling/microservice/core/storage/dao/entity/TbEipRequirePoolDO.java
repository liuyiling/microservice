package com.liuyiling.microservice.core.storage.dao.entity;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * ${remark}
 *
 * @author liuyiling
 */
@Table(name = "TB_EIP_REQUIRE_POOL")
@Data
public class TbEipRequirePoolDO implements Serializable {


    @Length(max = 36, message = "GUID 长度不能超过36")
    @NotNull(message = "GUID not allow null")
    @Column(name = "GUID")
    private String guid;


    @NotNull(message = "REQUIRE_ID not allow null")
    @Id
    @Column(name = "REQUIRE_ID")
    private BigDecimal requireId;


    @Length(max = 50, message = "SYSTEM_TYPE 长度不能超过50")
    @NotNull(message = "SYSTEM_TYPE not allow null")
    @Column(name = "SYSTEM_TYPE")
    private String systemType;


    @Length(max = 255, message = "ABBR 长度不能超过255")
    @NotNull(message = "ABBR not allow null")
    @Column(name = "ABBR")
    private String abbr;


    @Length(max = 1000, message = "DESCRIPTION 长度不能超过1000")
    @Column(name = "DESCRIPTION")
    private String description;


    @Column(name = "STATUS")
    private BigDecimal status;


    @Length(max = 50, message = "SUBMIT_STAFF_CODE 长度不能超过50")
    @NotNull(message = "SUBMIT_STAFF_CODE not allow null")
    @Column(name = "SUBMIT_STAFF_CODE")
    private String submitStaffCode;


    @Length(max = 50, message = "SUBMIT_STAFF_NAME 长度不能超过50")
    @Column(name = "SUBMIT_STAFF_NAME")
    private String submitStaffName;


    @Length(max = 50, message = "SUBMIT_STAFF_DEPART 长度不能超过50")
    @Column(name = "SUBMIT_STAFF_DEPART")
    private String submitStaffDepart;


    @Column(name = "CHANGE_TO_TASK")
    private BigDecimal changeToTask;


    @Column(name = "SUBMIT_TIME")
    private BigDecimal submitTime;


    @Length(max = 50, message = "SERIAL_NO 长度不能超过50")
    @Column(name = "SERIAL_NO")
    private String serialNo;


    @Length(max = 500, message = "URL_LINK 长度不能超过500")
    @Column(name = "URL_LINK")
    private String urlLink;


    @Length(max = 500, message = "ATTACHMENT 长度不能超过500")
    @Column(name = "ATTACHMENT")
    private String attachment;


    @Length(max = 500, message = "FEEDBACK 长度不能超过500")
    @Column(name = "FEEDBACK")
    private String feedback;


    @Length(max = 500, message = "TASK_SERIAL_NO 长度不能超过500")
    @Column(name = "TASK_SERIAL_NO")
    private String taskSerialNo;


    @NotNull(message = "URGENT not allow null")
    @Column(name = "URGENT")
    private BigDecimal urgent;

}