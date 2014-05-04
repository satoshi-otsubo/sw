package models.entity;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import com.avaje.ebean.annotation.*;

import play.db.ebean.*;
 
@Entity
public class Station extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	public Long station_g_cd;
    @NotNull
    public String station_name;
    public String station_name_k;
    public String station_name_r;
    @NotNull
    public Long line_id;
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    public Line line;
	public Long prefecture_id;
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefecture_id")
	public Prefecture prefecture;
	public String post;
	public String add;
	public Double lon;
	public Double lat;
	public String open_ymd;
	public String close_ymd;	
    public Integer e_status;
    public Integer e_sort;    
    @CreatedTimestamp
    public Date createDate;
    @UpdatedTimestamp
    public Date updateDate;
    @Transient
    public Double distance;
    @Transient
    public List<Long> stationIds;
    @Transient
    public List<Timetable> timetables;
/*   
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    public List<Timetable> timetables = new ArrayList<Timetable>();
*/
    public String toString() {
        return "";
    } 
    

}