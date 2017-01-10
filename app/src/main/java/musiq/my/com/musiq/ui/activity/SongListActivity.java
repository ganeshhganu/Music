package musiq.my.com.musiq.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.launcher.Launcher;
import musiq.my.com.musiq.common.ui.Base.BaseActivity;
import musiq.my.com.musiq.common.ui.custom.view.GlideImageView;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.ui.adapter.SongListAdapter;

public class SongListActivity extends BaseActivity implements SongListAdapter.OnItemClickListener {

    private GlideImageView mHeaderImage;
    private RecyclerView mSongList;
    private TextView mAlbumName, mSongCount;
    private SongListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.song_list);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mHeaderImage = (GlideImageView) findViewById(R.id.header_image);
        mSongList = (RecyclerView) findViewById(R.id.song_list);
        mAlbumName = (TextView) findViewById(R.id.album_name);
        mSongCount = (TextView) findViewById(R.id.no_of_songs);

        mAlbumName.setText(getIntent().getStringExtra(AppConstants.ALBUM_NAME));
        mSongCount.setText(getString(R.string.songs, String.valueOf(getIntent().getIntExtra(AppConstants.ALBUM_SONG_COUNT, -1))));
    }

    @Override
    public void itemClickListener(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        Intent intent = new Intent();
        intent.putExtra(AppConstants.IS_FROM, AppConstants.SONG_LIST);
        intent.putExtra(AppConstants.ALBUM_ID, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        intent.putExtra(AppConstants.MEDIA_ID, cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        intent.putExtra(AppConstants.TRACK_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        intent.putExtra(AppConstants.ARTIST_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        intent.putExtra(AppConstants.POSITION, position);
        Launcher.launchPlayer(this, intent);
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(this, "Grant storage permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionGranted() {
        mSongList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongListAdapter(Utils.getSongsInAlbum(this, getIntent().getIntExtra(AppConstants.ALBUM_ID, -1)),
                new WeakReference<Context>(this));
        mSongList.setAdapter(mAdapter);

        mAdapter.setItemClickListner(this);
        Uri uri = ContentUris.withAppendedId(AppConstants.ALBUM_ART, getIntent().getIntExtra(AppConstants.ALBUM_ID, -1));
        mHeaderImage.loadImage(uri);
    }
}
