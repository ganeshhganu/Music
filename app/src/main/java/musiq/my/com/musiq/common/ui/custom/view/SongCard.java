package musiq.my.com.musiq.common.ui.custom.view;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import musiq.my.com.musiq.R;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class SongCard extends LinearLayout{

    private TextView mAlbumName;
    private TextView mArtistName;
    private TextView mTotalSongs;
    private GlideImageView mAlbumArt;

    public SongCard(Context context) {
        super(context);
        init();
    }

    public SongCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SongCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.song_card_inflate, this);
        mAlbumArt = (GlideImageView) findViewById(R.id.album_art);
        mAlbumName = (TextView) findViewById(R.id.album_name);
        mArtistName = (TextView) findViewById(R.id.album_artist);
        mTotalSongs = (TextView) findViewById(R.id.no_of_songs);
    }

    public SongCard setAlbumName(String album) {
        mAlbumName.setText(album);
        return this;
    }

    public SongCard setArtistName(String artist) {
        mArtistName.setText(artist);
        return this;
    }

    public SongCard setTotalSongs(String artist) {
        mTotalSongs.setText(getContext().getString(R.string.songs,artist));
        return this;
    }

    public SongCard setAlbumArt(int id) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, id);
        mAlbumArt.loadImage(uri);
        return this;
    }

    public View getImageView(){
        return mAlbumArt;
    }

    public View getTotalSongsView(){
        return mTotalSongs;
    }

    public View getAlbumNameView(){
        return mAlbumName;
    }
}
