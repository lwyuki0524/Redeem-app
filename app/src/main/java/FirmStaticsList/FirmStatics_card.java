package FirmStaticsList;

public class FirmStatics_card {
    private String activity_name;         /*活動名稱*/
    private String join_people;         /*報名人數*/

    public FirmStatics_card() {
        super();
    }

    public FirmStatics_card(String activity_name, String join_people) {
        super();
        this.activity_name = activity_name;
        this.join_people = join_people;
    }

    public String getJoinPeople() {
        return join_people;
    }
    public void setJoinPeople(String join_people) {
        this.join_people = join_people;
    }

    public void setName(String activity_name) {
        this.activity_name = activity_name;
    }
    public String getName() {
        return activity_name;
    }

}
