package org.lf.admin.db.pojo;

import java.util.Date;

public class VYHPSQ extends PagedPojo{
    private String dm;

    private Integer appId;

    private String sqr;

    private Integer sqType;

    private Integer sqbmId;

    private String deptName;

    private Integer deptNo;

    private Integer status;

    private String remark;
    
    private Date sqsj;

    private String sqrmc;
    
    public String getDm() {
        return dm;
    }

    public void setDm(String dm) {
        this.dm = dm == null ? null : dm.trim();
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getSqr() {
        return sqr;
    }

    public void setSqr(String sqr) {
        this.sqr = sqr == null ? null : sqr.trim();
    }

    public Integer getSqType() {
        return sqType;
    }

    public void setSqType(Integer sqType) {
        this.sqType = sqType;
    }

    public Integer getSqbmId() {
        return sqbmId;
    }

    public void setSqbmId(Integer sqbmId) {
        this.sqbmId = sqbmId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? null : deptName.trim();
    }

    public Integer getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(Integer deptNo) {
        this.deptNo = deptNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public Date getSqsj() {
		return sqsj;
	}

	public void setSqsj(Date sqsj) {
		this.sqsj = sqsj;
	}

	public String getSqrmc() {
		return sqrmc;
	}

	public void setSqrmc(String sqrmc) {
		this.sqrmc = sqrmc == null ? null : sqrmc.trim();
	}
    
}