package FirmStaticsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.sa.savirtualcurrencyapp.R;

public class FirmStaticsAdapter extends RecyclerView.Adapter<FirmStaticsAdapter.ViewHolder>  {
    private Context context;
    private List<FirmStatics_card> activityList;

    public FirmStaticsAdapter(Context context, List<FirmStatics_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public FirmStaticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.firm_statics_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirmStaticsAdapter.ViewHolder holder, int position) {
        final FirmStatics_card activity = activityList.get(position);
        holder.activityName.setText(activity.getName());
        holder.joinPeople.setText(activity.getJoinPeople());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityName;
        TextView joinPeople;
        public ViewHolder( View itemView) {
            super(itemView);
            activityName = (TextView) itemView.findViewById(R.id.activityName);
            joinPeople = (TextView) itemView.findViewById(R.id.joinPeople);
        }
    }
}
