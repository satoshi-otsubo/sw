package models.entity;
 
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import com.avaje.ebean.annotation.*;

import play.db.ebean.*;
 
@Entity
public class Timenotice extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	@NotNull
	public Long station_id;
	@NotNull
	public Integer kind;
	@NotNull
	public String line_name;
	@NotNull
	public String direction;
	@NotNull
	public Integer notice;
	@Column(columnDefinition = "TEXT")
	public String contents;
    @CreatedTimestamp
    public Date createDate;
 
    @UpdatedTimestamp
    public Date updateDate;
    
    public String toString() {
        return "Parent [id=" + id + ", createDate="
                + createDate + ", updateDate=" + updateDate + "]";
    } 
}