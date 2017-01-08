package musiq.my.com.musiq.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private String TAG = "AlbumListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        init();
    }

    private void init() {
        Cursor cursor = Utils.getAudioList(this);
        RecyclerView mSongList = (RecyclerView) findViewById(R.id.song_list);
        FloatingActionButton mNowPlaying = (FloatingActionButton) findViewById(R.id.now_playing);
        mNowPlaying.setOnClickListener(this);
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = getOrient.getRotation();
        int gridSize = (orientation ==1||orientation ==3)?3:2;
        mSongList.setLayoutManager(new GridLayoutManager(this, gridSize));
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
}
