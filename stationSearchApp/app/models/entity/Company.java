package models.entity;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import com.avaje.ebean.annotation.*;

import play.db.ebean.*;

@Entity
public class Company extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	public Long rr_cd;
    @NotNull
    public String company_name;
    public String company_name_k;
    public String company_name_h;
    public String company_name_r;
    public String company_url;
    public Integer company_type;
    public Integer e_status;
    public Integer e_sort;
    @CreatedTimestamp
    public Date createDate;
    @UpdatedTimestamp
    public Date updateDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    public List<Line> lines = new ArrayList<Line>();
    
    public String toString() {
        return "";
    } 
    
}