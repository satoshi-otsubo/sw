package models.entity;
 
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;

import play.db.ebean.*;
 
@Entity
public class Prefecture extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	@NotNull
	public String pref_name;
	
	public Long prefecturearea_id;
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefecturearea_id")
	public Prefecturearea prefecturearea;
	
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prefecture")
    public List<Station> stations = new ArrayList<Station>();
    
    public String toString() {
        return "";
    } 
    

}