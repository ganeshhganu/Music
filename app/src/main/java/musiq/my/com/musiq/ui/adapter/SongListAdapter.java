package musiq.my.com.musiq.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.ui.activity.PlayerActivity;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private Cursor mCursor;
    private WeakReference<Context> mContext;
    private OnItemClickListener itemClickListener;

    public SongListAdapter(Cursor cursor, WeakReference<Context> context) {
        mCursor = cursor;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_song_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        holder.mTrackName.setText(mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));
        if (mContext.get() instanceof PlayerActivity) {
            holder.mTrackName.setTextColor(mContext.get().getColor(R.color.white));
        }
        holder.mArtistName.setText(mCursor.getString(mCursor.getColumnIndex(Media.ARTIST)));
        holder.mTrackDuration.setText(Utils.getTimeFromMilliSeconds(mCursor.getLong(mCursor.getColumnIndex(Media.DURATION))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.itemClickListener(mCursor, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void setItemClickListner(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTrackName, mArtistName, mTrackDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrackName = (TextView) itemView.findViewById(R.id.track_name);
            mArtistName = (TextView) itemView.findViewById(R.id.track_artist);
            mTrackDuration = (TextView) itemView.findViewById(R.id.track_duration);
        }
    }

    public interface OnItemClickListener {
        void itemClickListener(Cursor cursor, int position);
    }
}




