package com.boda.renuka.assessment;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by renuka on 28/8/18.
 */

public class ResponsePojo {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("products")
    String product;

    @SerializedName("child_categories")
    String child_Category;

    @SerializedName("ranking")
    String rankings;

    @SerializedName("products")
    String poduct_ranking;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getChild_Category() {
        return child_Category;
    }

    public void setChild_Category(String child_Category) {
        this.child_Category = child_Category;
    }

    public String getRankings() {
        return rankings;
    }

    public void setRankings(String rankings) {
        this.rankings = rankings;
    }

    public String getPoduct_ranking() {
        return poduct_ranking;
    }

    public void setPoduct_ranking(String poduct_ranking) {
        this.poduct_ranking = poduct_ranking;
    }

}
