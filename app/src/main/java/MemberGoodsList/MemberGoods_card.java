package MemberGoodsList;

import android.graphics.Bitmap;

public class MemberGoods_card {
    private String goods_name;         /*商品名稱*/
    private String goods_price;         /*商品價錢*/
    private String goods_amount;         /*商品數量*/
    private Bitmap goods_img;  /*商品圖片*/

    public MemberGoods_card() {
        super();
    }

    public MemberGoods_card(String goods_name, String goods_price, String goods_amount,Bitmap goods_img) {
        super();
        this.goods_name = goods_name;
        this.goods_price = goods_price;
        this.goods_amount = goods_amount;
        this.goods_img = goods_img;
    }

    public String getName() {
        return goods_name;
    }
    public void setName(String goods_name) {
        this.goods_name = goods_name;
    }

    public void setPrice(String goods_price) {
        this.goods_price = goods_price;
    }
    public String getPrice() {
        return goods_price;
    }

    public void setAmount(String goods_amount) {
        this.goods_amount = goods_amount;
    }
    public String getAmount() {
        return goods_amount;
    }

    public void setImg(Bitmap goods_img) {
        this.goods_img = goods_img;
    }
    public Bitmap getImg() {
        return goods_img;
    }
}
