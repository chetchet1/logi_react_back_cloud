package kr.co.seoulit.logistics.logiinfosvc.compinfo.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name="DEPARTMENT")
@IdClass(DepartmentPK.class)
public class DepartmentEntity implements Serializable {
    private String workplaceName;
    private String deptName;
    @Id
    private String deptCode;
    @Id
    private String workplaceCode;
    private String companyCode;
    private String deptEndDate;
    private String deptStartDate;
//    private String status = "NORMAL";
}
