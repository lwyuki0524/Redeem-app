package MemberGoodsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.sa.savirtualcurrencyapp.R;

public class MemberGoodsAdapter extends RecyclerView.Adapter<MemberGoodsAdapter.ViewHolder>  {
    private Context context;
    private List<MemberGoods_card> activityList;

    public MemberGoodsAdapter(Context context, List<MemberGoods_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public MemberGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_my_good_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberGoodsAdapter.ViewHolder holder, int position) {
        final MemberGoods_card activity = activityList.get(position);
        holder.goodsName.setText(activity.getName());
        holder.goodsAmount.setText(activity.getAmount());
        holder.goodsPrice.setText(activity.getPrice());
        holder.goodsImg.setImageBitmap(activity.getImg());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView goodsName;
        TextView goodsAmount;
        TextView goodsPrice;
        ImageView goodsImg;
        public ViewHolder( View itemView) {
            super(itemView);
            goodsName = (TextView) itemView.findViewById(R.id.goods_name);
            goodsAmount = (TextView) itemView.findViewById(R.id.goods_amount);
            goodsPrice = (TextView) itemView.findViewById(R.id.goods_price);
            goodsImg = (ImageView) itemView.findViewById(R.id.goods_img);
        }
    }
}
