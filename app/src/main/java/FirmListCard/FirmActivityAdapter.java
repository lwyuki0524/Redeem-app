package FirmListCard;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.sa.savirtualcurrencyapp.FirmActivityItem;
import tw.sa.savirtualcurrencyapp.FirmActivityList;
import tw.sa.savirtualcurrencyapp.R;

import static android.content.Context.MODE_PRIVATE;


public class FirmActivityAdapter extends RecyclerView.Adapter<FirmActivityAdapter.ViewHolder> {
    private Context context;
    private List<FirmActivityList_card> activityList;

    public FirmActivityAdapter(Context context, List<FirmActivityList_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public FirmActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.firm_activity_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FirmActivityAdapter.ViewHolder holder, int position) {
        final FirmActivityList_card activity = activityList.get(position);
        holder.activityName.setText(activity.getName());
        holder.activityListItem.setId(activity.getID());
        holder.activityListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "活動ID:"+activity.getID(), Toast.LENGTH_SHORT).show();
                String id = ""+activity.getID();
                Activity activity = (Activity)v.getContext();
                Intent intent = new Intent(activity, FirmActivityItem.class);
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
        CardView activityListItem;
        ViewHolder(View itemView) {
            super(itemView);
            activityName = (TextView) itemView.findViewById(R.id.activity_name);
            activityListItem = (CardView) itemView.findViewById(R.id.list_activity_item);
        }
    }
}