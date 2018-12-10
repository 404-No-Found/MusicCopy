package com.example.sjy.musiccopy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MusicAdapter extends BaseAdapter {
    private LayoutInflater minflate;
    private File[] musicDirs;

    public MusicAdapter(LayoutInflater minflate, File[] musicDirs) {
        super();
        this.minflate = minflate;
        this.musicDirs = musicDirs;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return musicDirs.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = minflate.inflate(R.layout.item_music_name, null);
            vh.mtextview =convertView
                    .findViewById(R.id.textview_music_name);
            vh.mtextview_author = (TextView) convertView
                    .findViewById(R.id.textview_author_name);
            vh.img =convertView.findViewById(R.id.img);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.mtextview.setText(musicDirs[position].getName());
        MediaMetadataRetriever mnr = new MediaMetadataRetriever();
        Log.d("路径", musicDirs[position].getAbsolutePath());
        mnr.setDataSource(musicDirs[position].getAbsolutePath());
        String author = mnr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (author != null) {
            vh.mtextview_author.setText(author);
        } else {
            vh.mtextview_author.setText("<未知>");
        }
        byte[] img = mnr.getEmbeddedPicture();
        if (img != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            vh.img.setImageBitmap(bitmap);
        } else {
            vh.img.setImageResource(R.drawable.ic_launcher_background);
        }
        return convertView;
    }
    class ViewHolder {
        TextView mtextview;
        TextView mtextview_author;
        ImageView img;
    }
}
