package models.response;

import java.util.List;

import models.entity.Company;

/**
 * 検索結果表示用　会社
 *
 * @author 
 * @since 
 */
public class R_Company {
	public Long id;
	public String company_name;
	public List<R_Line> lines;
}
