package MemberActivityRecommend;

import android.graphics.Bitmap;

public class RecommendActivity_card {
    private String activity_name;  /*活動名稱*/
    private int activityID;         /*活動ID*/
    private Bitmap activityimg;         /*活動廠商照片*/

    public RecommendActivity_card() {
        super();
    }

    public RecommendActivity_card(String activity_name, int activityID, Bitmap activityimg) {
        super();
        this.activity_name = activity_name;
        this.activityID = activityID;
        this.activityimg = activityimg;
    }

    public String getName() {
        return activity_name;
    }

    public void setName(String activity_name) {
        this.activity_name = activity_name;
    }

    public void setID(int activityID) {
        this.activityID = activityID;
    }

    public int getID() {
        return activityID;
    }

    public void setImg(Bitmap activityimg) {
        this.activityimg = activityimg;
    }

    public Bitmap getImg() {
        return activityimg;
    }
}
