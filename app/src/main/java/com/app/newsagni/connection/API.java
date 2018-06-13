package com.app.newsagni.connection;


import com.app.newsagni.connection.callbacks.CallbackCategories;
import com.app.newsagni.connection.callbacks.CallbackCategoryDetails;
import com.app.newsagni.connection.callbacks.CallbackComment;
import com.app.newsagni.connection.callbacks.CallbackDetailsPost;
import com.app.newsagni.connection.callbacks.CallbackDevice;
import com.app.newsagni.connection.callbacks.CallbackInfo;
import com.app.newsagni.connection.callbacks.CallbackListPost;
import com.app.newsagni.model.DeviceInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    /* url */
    String BASE_URL = "http://agnihothram.com/";



    String EXCLUDE_FIELD = "&exclude=content,categories,tags,comments,custom_fields";



    @GET("?json=info")
    Call<CallbackInfo> getInfo();




    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=get_posts" + EXCLUDE_FIELD)
    Call<CallbackListPost> getPostByPage(
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=get_post")
    Call<CallbackDetailsPost> getPostDetialsById(
            @Query("id") int id
    );

    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=get_search_results" + EXCLUDE_FIELD)
    Call<CallbackListPost> getSearchPosts(
            @Query("search") String search,
            @Query("count") int count
    );




    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=get_category_index")
    Call<CallbackCategories> getAllCategories();

    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=get_category_posts" + EXCLUDE_FIELD)
    Call<CallbackCategoryDetails> getCategoryDetailsByPage(
            @Query("id") int id,
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @GET("?json=respond/submit_comment")
    Call<CallbackComment> sendComment(
            @Query("post_id") long post_id,
            @Query("name") String name,
            @Query("email") String email,
            @Query("content") String content
    );

    @Headers({"Cache-Control: max-age=0", "User-Agent: Wordpress"})
    @POST("?api-fcm=register")
    Call<CallbackDevice> registerDevice(@Body DeviceInfo deviceInfo);


}
