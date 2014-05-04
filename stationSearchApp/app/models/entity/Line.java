package models.entity;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import com.avaje.ebean.annotation.*;

import play.db.ebean.*;
 
@Entity
public class Line extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	@NotNull
	public Long company_id;
	@ManyToOne
    @JoinColumn(name = "company_id")
    public Company company;
    @NotNull
    public String line_name;
    public String line_name_k;
    public String line_name_h;
    public String line_color_c;   
    public String line_color_t;
    public Integer line_type;
    public Double lon;
    public Double lat;
    public Integer zoom;
    public Integer e_status;
    public Integer e_sort;
    @CreatedTimestamp
    public Date createDate;
    @UpdatedTimestamp
    public Date updateDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    public List<Station> stations = new ArrayList<Station>();
    
    public String toString() {
        return "";
    } 
    

}