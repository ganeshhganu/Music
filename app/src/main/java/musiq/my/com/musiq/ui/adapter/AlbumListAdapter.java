package musiq.my.com.musiq.ui.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.launcher.Launcher;
import musiq.my.com.musiq.common.ui.custom.view.SongCard;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private Cursor mCursor;
    private WeakReference<Activity> mActivity;

    public AlbumListAdapter(Activity activity, Cursor cursor) {
        mCursor = cursor;
        mActivity = new WeakReference(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflater_album_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        holder.songCard.setAlbumName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))
                .setArtistName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                .setAlbumArt(mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)))
                .setTotalSongs(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position);
                        Intent intent = new Intent();
                        intent.putExtra(AppConstants.ALBUM_ID, mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        intent.putExtra(AppConstants.ALBUM_NAME, mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                        intent.putExtra(AppConstants.ALBUM_SONG_COUNT, mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions
                                    .makeSceneTransitionAnimation(mActivity.get(), holder.songCard.getImageView(), "robot");
                            Launcher.launchSongList(v.getContext(), intent, options);
                        }

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SongCard songCard;

        public ViewHolder(View itemView) {
            super(itemView);
            songCard = (SongCard) itemView.findViewById(R.id.song_card);
        }
    }
}
