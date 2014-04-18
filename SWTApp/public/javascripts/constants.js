// 検索ページごとのクッキーに保存される検索条件名
var KEYWORD_SEARCH_CONDITIONS = "keywordSearchConditions";
var CATEGORY_SEARCH_CONDITIONS = "categorySearchConditions";
var GEOCODE_SEARCH_CONDITIONS = "geocodeSearchConditions";
var CONDITION_SEARCH_CONDITIONS = "conditionSearchConditions";

var SEARCH_KIND_KEYWORD = "keyword"; 
var SEARCH_KIND_CATEGORY = "category"; 
var SEARCH_KIND_GEOCODE = "geocode"; 

var GEOCODE_DEFAULT_SEARCH_AREA = 10000;

var KEY_ENTER = 13;

var ERRORS_GET_POSITION_REFUSAL = 1;
var ERRORS_GET_POSITION_DEVICE = 2;
var ERRORS_GET_POSITION_TIMEOUT = 3;