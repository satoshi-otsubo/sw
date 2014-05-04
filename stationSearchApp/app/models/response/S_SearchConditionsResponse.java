package models.response;

import java.util.List;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;

import models.entity.Station;

/**
 * 検索結果表示用
 *
 * @author 
 * @since 
 */
public class S_SearchConditionsResponse extends BaseResponse{
    public List<R_Prefecture> prefectures;
}
