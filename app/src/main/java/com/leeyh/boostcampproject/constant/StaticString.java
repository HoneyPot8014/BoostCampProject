package com.leeyh.boostcampproject.constant;

public class StaticString {

    //API Request Base Url
    public static final String API_URL = "https://openapi.naver.com/v1/search/movie.json?";
    //API Client id value
    public static final String CLIENT_ID = "X-Naver-Client-Id";
    public static final String CLIENT_ID_VALUE = "uoL0Z0WNyn1rWKu0avBJ";
    //API Secret Client id value
    public static final String SECRET_ID = "X-Naver-Client-Secret";
    public static final String SECRET_ID_VALUE = "EyRJTLngz9";

    //request method
    public static final String GET = "GET";

    //request QueryString
    public static final String QUERY = "query";
    public static final String DISPLAY = "display";
    public static final String START = "start";
    public static final String GENRE = "genre";
    public static final String COUNTRY = "country";
    public static final String YEAR_FROM = "yearfrom";
    public static final String YEAR_TO = "yearto";
    public static final String TOTAL = "total";
    public static final String ITEMS = "items";
    public static final String LINK = "link";
    public static final String IMAGE = "image";

    //cache info
    public static final int CACHE_SIZE = 1024 * 1024 * 4;

    //default request StringQuery
    public static final String DEFAULT_START = "1";

}
