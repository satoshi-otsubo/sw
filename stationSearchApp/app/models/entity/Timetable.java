package models.entity;
 
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import com.avaje.ebean.annotation.*;

import play.db.ebean.*;
 
@Entity
public class Timetable extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
    
	@NotNull
	public Long station_id;
	public Long line_id;
/*
	@ManyToOne
    @JoinColumn(name = "station_id")
    public Station station;
*/
	@NotNull
	public Integer kind;
	public String line_name;
	
	public String direction;
	public String mark;
	public String trn;
	public String sta;
	
	@NotNull
	public Integer hour;
	
	@NotNull
	public Integer minute;
	
    @CreatedTimestamp
    public Date createDate;
 
    @Version
    @UpdatedTimestamp
    public Date updateDate;
    
    public String toString() {
        return "Parent [id=" + id + ", createDate="
                + createDate + ", updateDate=" + updateDate + "]";
    } 
}