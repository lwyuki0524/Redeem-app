package MemberActivityRecommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.sa.savirtualcurrencyapp.MemberActivityInfo;
import tw.sa.savirtualcurrencyapp.R;

import static android.content.Context.MODE_PRIVATE;


public class RecommendActivityAdapter extends RecyclerView.Adapter<RecommendActivityAdapter.ViewHolder>  {
    private Context context;
    private List<RecommendActivity_card> activityList;

    public RecommendActivityAdapter(Context context, List<RecommendActivity_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public RecommendActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_main_activity_recommend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendActivityAdapter.ViewHolder holder, int position) {
        final RecommendActivity_card activity = activityList.get(position);
        holder.activityName.setText(activity.getName());
        holder.activityItem.setId(activity.getID());
        holder.firmImg.setImageBitmap(activity.getImg());
        holder.activityItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "活動ID:"+activity.getID(), Toast.LENGTH_SHORT).show();
                String id = ""+activity.getID();
                Activity activity = (Activity)v.getContext();
                Intent intent = new Intent(activity, MemberActivityInfo.class);
                // 儲存活動ID
                SharedPreferences record = context.getSharedPreferences("record", MODE_PRIVATE);
                record.edit()
                        .putString("activityID", id)
                        .commit();
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityName;
        LinearLayout activityItem;
        ImageView firmImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = (TextView) itemView.findViewById(R.id.activity_name);
            activityItem = (LinearLayout) itemView.findViewById(R.id.activity_item);
            firmImg  = (ImageView) itemView.findViewById(R.id.activity_item_img);

        }
    }
}
