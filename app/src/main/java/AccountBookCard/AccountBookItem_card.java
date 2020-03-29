package AccountBookCard;

import android.graphics.Bitmap;

public class AccountBookItem_card {
    private String tradeTime;  /*交易時間*/
    private String tradeType;         /*交易類型(收入/支出)*/
    private String tradeAttri_title;         /*交易屬性標題(賣出商品/活動名稱)*/
    private String tradeAttri;         /*交易屬性內容(商品名稱/活動名稱)*/
    private String tradeTargetName;         /*交易對象*/
    private String tradeMoney;         /*交易金額*/
    private Bitmap image;         /*照片(商品照片<表示賣商品給會員>/紅包圖示<表示給會員紅包>)*/
    private int tradeID;         /*交易ID*/

    public AccountBookItem_card() {
        super();
    }

    public AccountBookItem_card(String tradeTime, String tradeType, String tradeAttri_title,
                                String tradeAttri, String tradeTargetName, String tradeMoney, Bitmap image, int tradeID) {
        super();
        this.tradeTime = tradeTime;
        this.tradeType = tradeType;
        this.tradeAttri_title = tradeAttri_title;
        this.tradeAttri = tradeAttri;
        this.tradeTargetName = tradeTargetName;
        this.tradeMoney = tradeMoney;
        this.image = image;
        this.tradeID = tradeID;
    }

    public String getTime() {
        return tradeTime;
    }
    public void setTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setType(String tradeType) {
        this.tradeType = tradeType;
    }
    public String getType() {
        return tradeType;
    }

    public void setAttri_title(String tradeAttri_title) {
        this.tradeAttri_title = tradeAttri_title;
    }
    public String getAttri_title() {
        return tradeAttri_title;
    }

    public void setAttri(String tradeAttri) {
        this.tradeAttri = tradeAttri;
    }
    public String getAttri() {
        return tradeAttri;
    }

    public void setTargetName(String tradeTargetName) {
        this.tradeTargetName = tradeTargetName;
    }
    public String getTargetName() {
        return tradeTargetName;
    }

    public void setMoney(String tradeMoney) {
        this.tradeMoney = tradeMoney;
    }
    public String getMoney() {
        return tradeMoney;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public Bitmap getImage() {
        return image;
    }

    public void setID(int tradeID) {
        this.tradeID = tradeID;
    }
    public int getID() {
        return tradeID;
    }

}
