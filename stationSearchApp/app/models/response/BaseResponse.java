package models.response;

import java.util.List;

import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.ConstraintValidatorContext;

import models.entity.Station;

/**
 * 結果表示基礎クラス
 *
 * @author 
 * @since 
 */
public class BaseResponse {
    /**
     * エラーコード
     */
    public Integer code;
    /**
     * ステータス
     */
    public String status;
    /**
     * メッセージ
     */
    public List<String> messages;
    /**
     * ページ
     */
    public Integer page;
    /**
     * 最大ページ数
     */
    public Integer maxPage;
    
    /**
     * アクション
     */
    public Integer action;
    
}
