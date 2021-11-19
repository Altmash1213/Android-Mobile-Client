package org.intelehealth.hellosaathitraining.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BucketResponse {
    @SerializedName("data")
    @Expose
    public List<Bucket> data;
    @SerializedName("status")
    @Expose
    public String status;

    public static class Bucket {
        @SerializedName("bucketId")
        @Expose
        public int bucketId;
        @SerializedName("bucketName")
        @Expose
        public String bucketName;

        @SerializedName("bucketforgender")
        @Expose
        public String bucketforgender;

        @SerializedName("languagesavailablein")
        @Expose
        public String languagesavailablein;
        @Override
        public String toString() {
            return bucketName;
        }
    }
}