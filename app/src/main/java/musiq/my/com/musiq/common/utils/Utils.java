package musiq.my.com.musiq.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.concurrent.TimeUnit;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.ui.custom.transformation.BlurTransformation;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class Utils {

    public static Cursor getAudioList(Context ctx) {
        ContentResolver resolver = ctx.getContentResolver();
        String[] projection = new String[]{Albums._ID, Albums.ALBUM, Albums.ARTIST, Albums.ALBUM_ART, Albums.NUMBER_OF_SONGS};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = Media.ALBUM + " ASC";
        return resolver.query(Albums.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);
    }


    public static Cursor getSongsInAlbum(Context ctx, int albumId) {
        ContentResolver resolver = ctx.getContentResolver();
        String selection = Albums.ALBUM_ID + "=" + albumId;
        String sortOrder = Media.ALBUM + " ASC";
        return resolver.query(Media.EXTERNAL_CONTENT_URI, null, selection, null, sortOrder);
    }


    public static void loadBlurImage(Context context, Uri uri, ImageView imageView){
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .bitmapTransform(new BlurTransformation(context))
                .placeholder(R.drawable.bg_default_album_art)
                .crossFade()
                .into(imageView);
    }

    public static void loadImage(Context context, Uri uri, ImageView imageView){
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.bg_default_album_art)
                .crossFade()
                .into(imageView);
    }

    public static void loadRoundImage(final Context context, Uri uri, final ImageView imageView){
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .override(300, 300)
                .centerCrop()
                .placeholder(R.drawable.bg_default_album_art)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady ( final Bitmap resource,
                                                  final GlideAnimation<? super Bitmap> glideAnimation ) {
                        super.onResourceReady(resource, glideAnimation);

                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }

                });
    }

    public static String getTimeFromMilliSeconds(long milliSecond){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSecond),
                TimeUnit.MILLISECONDS.toSeconds(milliSecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSecond)));
    }

    public static String getTimeTextFromMilliSeconds(long milliSecond){
        return String.format("%02d min:%02d sec",
                TimeUnit.MILLISECONDS.toMinutes(milliSecond),
                TimeUnit.MILLISECONDS.toSeconds(milliSecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSecond)));
    }


}
