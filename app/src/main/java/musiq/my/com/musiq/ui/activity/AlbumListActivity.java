package musiq.my.com.musiq.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.launcher.Launcher;
import musiq.my.com.musiq.common.ui.Base.BaseActivity;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.manager.preference.PreferenceManager;
import musiq.my.com.musiq.ui.adapter.AlbumListAdapter;

public class AlbumListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mSongList;
    private FloatingActionButton mNowPlaying;
    private String TAG = "AlbumListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        init();
    }

    private void init() {
        Cursor cursor = Utils.getAudioList(this);
        mSongList = (RecyclerView) findViewById(R.id.song_list);
        mNowPlaying = (FloatingActionButton) findViewById(R.id.now_playing);
        mNowPlaying.setOnClickListener(this);
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = getOrient.getRotation();
        Log.e(TAG, "init: "+orientation);
        mSongList.setLayoutManager(new GridLayoutManager(this, 2));
        mSongList.setAdapter(new AlbumListAdapter(cursor));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.now_playing:
                Intent intent = new Intent();
                intent.putExtra(AppConstants.IS_FROM, AppConstants.HOME);
                intent.putExtra(AppConstants.ALBUM_ID, PreferenceManager.getInt(this, AppConstants.ALBUM_ID));
                intent.putExtra(AppConstants.MEDIA_ID, PreferenceManager.getInt(this, AppConstants.MEDIA_ID));
                intent.putExtra(AppConstants.POSITION, PreferenceManager.getInt(this, AppConstants.POSITION));
                Launcher.launchPlayer(this, intent);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged: ");
        GridLayoutManager manager = (GridLayoutManager) mSongList.getLayoutManager();
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            manager.setSpanCount(3);
        }else{
            manager.setSpanCount(2);
        }


    }
}
