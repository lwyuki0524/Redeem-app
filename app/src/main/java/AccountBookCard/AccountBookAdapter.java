package AccountBookCard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import tw.sa.savirtualcurrencyapp.R;

public class AccountBookAdapter extends RecyclerView.Adapter<AccountBookAdapter.ViewHolder> {
    private Context context;
    private List<AccountBookItem_card> activityList;

    public AccountBookAdapter(Context context, List<AccountBookItem_card> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public AccountBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.accounting_book_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountBookAdapter.ViewHolder holder, int position) {
        final AccountBookItem_card activity = activityList.get(position);
        holder.tradeTime.setText(activity.getTime());
        holder.tradeType.setText(activity.getType());
        holder.tradeAttri_title.setText(activity.getAttri_title());
        holder.tradeAttri.setText(activity.getAttri());
        holder.tradeTargetName.setText(activity.getTargetName());
        holder.tradeMoney.setText(activity.getMoney());
//        byte[] byteArray= Base64.decode(image, Base64.DEFAULT);
//        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
//        Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(context.getResources(), R.id.image, options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
//        String imageType = options.outMimeType;
//        calculateInSampleSize(options,imageWidth,imageHeight);
//        holder.image.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.id.image, 100, 100));


        holder.image.setImageBitmap(activity.getImage());
//        holder.image.setText(activity.getName());
        holder.tradeAccountingItem.setId(activity.getID());
//        holder.tradeAccountingItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(context, "活動ID:"+activity.getID(), Toast.LENGTH_SHORT).show();
////                String id = ""+activity.getID();
////                Activity activity = (Activity)v.getContext();
////                Intent intent = new Intent(activity, FirmActivityItem.class);
////                // 儲存活動ID
////                SharedPreferences record = context.getSharedPreferences("record", MODE_PRIVATE);
////                record.edit()
////                        .putString("activityID", id)
////                        .commit();
////                activity.startActivity(intent);
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tradeTime;
        TextView tradeType;
        TextView tradeAttri_title;
        TextView tradeAttri;
        TextView tradeTargetName;
        TextView tradeMoney;
        ImageView image;
        CardView tradeAccountingItem;
        ViewHolder(View itemView) {
            super(itemView);
            tradeTime = (TextView) itemView.findViewById(R.id.date);
            tradeType = (TextView) itemView.findViewById(R.id.types);
            tradeAttri_title = (TextView) itemView.findViewById(R.id.goodsOrActivity_title);
            tradeAttri = (TextView) itemView.findViewById(R.id.goodsOrActivity);
            tradeTargetName = (TextView) itemView.findViewById(R.id.tradeTargetName);
            tradeMoney = (TextView) itemView.findViewById(R.id.money);
            image = (ImageView) itemView.findViewById(R.id.image);
            tradeAccountingItem = (CardView) itemView.findViewById(R.id.list_accounting_item);
        }
    }
//
//    //計算圖片的縮放值
//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 2;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) >= reqHeight
//                    && (halfWidth / inSampleSize) >= reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
//
//    // 根據路徑獲得圖片並壓縮，返回bitmap用於顯示
//    public static Bitmap getSmallBitmap(String filePath) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, 480, 800);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        return BitmapFactory.decodeFile(filePath, options);
//    }
//
//    //把bitmap轉換成String
//    public static String bitmapToString(String filePath) {
//
//        Bitmap bm = getSmallBitmap(filePath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
//        byte[] b = baos.toByteArray();
//        return Base64.encodeToString(b, Base64.DEFAULT);
//    }
//
//
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }

}
