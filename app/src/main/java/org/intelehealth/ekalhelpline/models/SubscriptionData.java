package org.intelehealth.ekalhelpline.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionData {
    @SerializedName("phonenumber")
    @Expose
    public String phonenumber;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("slotselected")
    @Expose
    public String slotselected;

    @SerializedName("bucketsubscribedto")
    @Expose
    public int bucketsubscribedto;

    @SerializedName("subscribedby")
    @Expose
    public String subscribedby;

    @SerializedName("language")
    @Expose
    public String language;

    @SerializedName("apptype")
    @Expose
    public String apptype;
}
