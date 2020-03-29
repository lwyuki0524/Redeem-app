package MemberAllActivityCard;

public class MemberAllActivity_card {
    private String activity_firm;  /*舉辦活動的公司*/
    private String activity_name;         /*活動名稱*/
    private String activity_address;         /*活動地點*/
    private int activityID;         /*活動ID*/

    public MemberAllActivity_card() {
        super();
    }

    public MemberAllActivity_card(String activity_firm, String activity_name, String activity_address,int activityID) {
        super();
        this.activity_firm = activity_firm;
        this.activity_name = activity_name;
        this.activity_address = activity_address;
        this.activityID = activityID;
    }

    public String getFirm() {
        return activity_firm;
    }
    public void setFirm(String activity_firm) {
        this.activity_firm = activity_firm;
    }

    public void setName(String activity_name) {
        this.activity_name = activity_name;
    }
    public String getName() {
        return activity_name;
    }

    public void setAddress(String activity_address) {
        this.activity_address = activity_address;
    }
    public String getAddress() {
        return activity_address;
    }

    public void setID(int activityID) {
        this.activityID = activityID;
    }
    public int getID() {
        return activityID;
    }

}
