package com.agaram.eln.primary.model.instrumentDetails;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity(name = "Lsordershareto")
@Table(name = "Lsordershareto")
public class Lsordershareto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "sharetocode")
	private Long sharetocode;
	
	@Column(columnDefinition = "varchar(250)")
	private String sharebyunifiedid;
	@Column(columnDefinition = "varchar(250)")
	private String sharetounifiedid;
	
	@Column(columnDefinition = "varchar(250)")
	private String sharebyusername;
	
	@Column(columnDefinition = "varchar(250)")
	private String sharetousername;
	
	private Long sharebatchcode;
	@Column(columnDefinition = "varchar(250)")
	private String sharebatchid;
	
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private String shareitemdetails;
	
	private Date sharedon;
	
	private Date unsharedon;
	
	private Date sharemodifiedon;
	
	private int sharerights =0;
	
	private int sharestatus =0;
	
	private int ordertype =0;

	public Long getSharetocode() {
		return sharetocode;
	}

	public void setSharetocode(Long sharetocode) {
		this.sharetocode = sharetocode;
	}

	public String getSharebyunifiedid() {
		return sharebyunifiedid;
	}

	public void setSharebyunifiedid(String sharebyunifiedid) {
		this.sharebyunifiedid = sharebyunifiedid;
	}

	public String getSharetounifiedid() {
		return sharetounifiedid;
	}

	public void setSharetounifiedid(String sharetounifiedid) {
		this.sharetounifiedid = sharetounifiedid;
	}

	public Long getSharebatchcode() {
		return sharebatchcode;
	}

	public void setSharebatchcode(Long sharebatchcode) {
		this.sharebatchcode = sharebatchcode;
	}

	public String getSharebatchid() {
		return sharebatchid;
	}

	public void setSharebatchid(String sharebatchid) {
		this.sharebatchid = sharebatchid;
	}

	public String getShareitemdetails() {
		return shareitemdetails;
	}

	public void setShareitemdetails(String shareitemdetails) {
		this.shareitemdetails = shareitemdetails;
	}

	public int getSharerights() {
		return sharerights;
	}

	public void setSharerights(int sharerights) {
		this.sharerights = sharerights;
	}

	public int getSharestatus() {
		return sharestatus;
	}

	public void setSharestatus(int sharestatus) {
		this.sharestatus = sharestatus;
	}

	public int getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(int ordertype) {
		this.ordertype = ordertype;
	}

	public String getSharebyusername() {
		return sharebyusername;
	}

	public void setSharebyusername(String sharebyusername) {
		this.sharebyusername = sharebyusername;
	}

	public String getSharetousername() {
		return sharetousername;
	}

	public void setSharetousername(String sharetousername) {
		this.sharetousername = sharetousername;
	}

	public Date getSharedon() {
		return sharedon;
	}

	public void setSharedon(Date sharedon) {
		this.sharedon = sharedon;
	}

	public Date getUnsharedon() {
		return unsharedon;
	}

	public void setUnsharedon(Date unsharedon) {
		this.unsharedon = unsharedon;
	}

	public Date getSharemodifiedon() {
		return sharemodifiedon;
	}

	public void setSharemodifiedon(Date sharemodifiedon) {
		this.sharemodifiedon = sharemodifiedon;
	}
	
	
}
