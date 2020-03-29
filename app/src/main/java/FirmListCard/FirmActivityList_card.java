package FirmListCard;

public class FirmActivityList_card {  /*卡片內的資料結構*/
    private String activity_name;  /*活動名稱*/
    private int activityID;         /*活動ID*/

    public FirmActivityList_card() {
        super();
    }

    public FirmActivityList_card(String activity_name,int activityID) {
        super();
        this.activity_name = activity_name;
        this.activityID = activityID;
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



}
