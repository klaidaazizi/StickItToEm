package database;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stickittoem.R;

import java.util.ArrayList;

// this will help show the CardView within our listview for sticker history.h
public class StickerViewAdapter extends BaseAdapter {
    private ArrayList<Message> stickerList;
    private LayoutInflater layout;

    public StickerViewAdapter(ArrayList<Message> array, Context applicationContext) {
        this.stickerList = stickerList;
        this.layout = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return stickerList.size();
    }

    @Override
    public Object getItem(int i) {
        return stickerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflate sticker view cards
        // check this part about null out
        view = layout.inflate(R.layout.sticker_card, null);
        // fill in the items
        TextView timeText = view.findViewById(R.id.timeValue);
        ImageView stickerPicture = view.findViewById(R.id.stickerPicture);
        TextView senderName = view.findViewById(R.id.senderValue);
        timeText.setText(stickerList.get(i).getTimeStamp());
        senderName.setText(stickerList.get(i).getSender());

        // TODO: figure this out for images
        // may need to change stickerID to int from String
        Log.d("STICKER_VIEW_ADAPTER","parse sticker ID, : " + Integer.parseInt(
                stickerList.get(i).getStickerID()
        ));
        Log.d("STICKER_VIEW_ADAPTOR", "sticker id, : " + stickerList.get(i).getStickerID());

        stickerPicture.setImageResource(Integer
                .parseInt(stickerList.get(i).getStickerID()));
        return view;
    }
}
