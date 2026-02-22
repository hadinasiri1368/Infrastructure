package com.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Table(name = "AHA_PERSON")
@Entity(name = "person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Person extends BaseEntity{
    @Column(name = "IS_COMPANY", columnDefinition = "NUMBER(1)", nullable = false)
    private Boolean isCompany;
    @Column(name = "FIRST_NAME", columnDefinition = "VARCHAR2(100)", nullable = false)
    private String firstName;
    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR2(200)", nullable = false)
    private String lastName;
    @Column(name = "BIRTH_DATE", columnDefinition = "CHAR(10)", nullable = false)
    private String birthDate;
    @Column(name = "NATIONAL_CODE", columnDefinition = "VARCHAR2(20)", nullable = false)
    private String nationalCode;
    @Column(name = "POSTAL_CODE", columnDefinition = "VARCHAR2(20)")
    private String postalCode;
    @Column(name = "BIRTH_CERTIFICATION_ID", columnDefinition = "VARCHAR2(30)")
    private String birthCertificationId;
    @Column(name = "BIRTH_CERTIFICATION_NUMBER", columnDefinition = "VARCHAR2(50)")
    private String birthCertificationNumber;
    @Column(name = "REGISTERATION_NUMBER", columnDefinition = "VARCHAR2(50)")
    private String registrationNumber;
    @Column(name = "ISSUING_CITY", columnDefinition = "VARCHAR2(100)")
    private String issuingCity;
    @Column(name = "FAX", columnDefinition = "VARCHAR2(100)")
    private String fax;
    @Column(name = "CELL_PHONE", columnDefinition = "VARCHAR2(100)")
    private String cellPhone;
    @Column(name = "E_MAIL", columnDefinition = "VARCHAR2(100)")
    private String email;
    @Column(name = "PARENT", columnDefinition = "VARCHAR2(200)")
    private String parent;
    @Column(name = "PHONE", columnDefinition = "VARCHAR2(200)")
    private String phone;
    @Column(name = "COMPANY_NAME", columnDefinition = "VARCHAR2(200)")
    private String companyName;
    @Column(name = "ADDRESS", columnDefinition = "VARCHAR2(1000)")
    private String address;
    @Column(name = "LATIN_FIRST_NAME", columnDefinition = "VARCHAR2(200)")
    private String latinFirstName;
    @Column(name = "LATIN_LAST_NAME", columnDefinition = "VARCHAR2(200)")
    private String latinLastName;
    @Column(name = "IS_IRANIAN", columnDefinition = "NUMBER(1)")
    private Boolean isIranian;
    @Column(name = "REF_ID", columnDefinition = "NUMBER(18)")
    private Long refId;
}
