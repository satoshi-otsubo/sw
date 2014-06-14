package models.entity;
 
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.Id;


import play.db.ebean.*;
 
@Entity
public class Prefecturearea extends Model {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;
	@NotNull
	public String area_name;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prefecturearea")
    public List<Prefecture> prefectures = new ArrayList<Prefecture>();
    
    public String toString() {
        return "";
    } 
    

}