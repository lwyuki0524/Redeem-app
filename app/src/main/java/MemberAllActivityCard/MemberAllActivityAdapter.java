package MemberAllActivityCard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.sa.savirtualcurrencyapp.MemberActivityInfo;
import tw.sa.savirtualcurrencyapp.R;

import static android.content.Context.MODE_PRIVATE;

public class MemberAllActivityAdapter extends RecyclerView.Adapter<MemberAllActivityAdapter.ViewHolder> {
    private Context context;
    private List<MemberAllActivity_card> activityList;

    public MemberAllActivityAdapter(Context context, List<MemberAllActivity_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public MemberAllActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_all_activity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberAllActivityAdapter.ViewHolder holder, int position) {
        final MemberAllActivity_card activity = activityList.get(position);
        holder.activityName.setText(activity.getName());
        holder.activityFirm.setText(activity.getFirm());
        holder.activitAddress.setText(activity.getAddress());
        holder.activityID.setId(activity.getID());
        holder.activityID.setOnClickListener(new View.OnClickListener() {
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

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView activityName;
        TextView activityFirm;
        TextView activitAddress;
        LinearLayout activityID;
        ViewHolder(View itemView) {
            super(itemView);
            activityName = (TextView) itemView.findViewById(R.id.act_name);
            activityFirm = (TextView) itemView.findViewById(R.id.act_firm);
            activitAddress = (TextView) itemView.findViewById(R.id.act_address);
            activityID = (LinearLayout) itemView.findViewById(R.id.check_activity_btn);
        }
    }
}

