package com.example.musicplayer2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/*
* 컨텐트 리졸버에서 조회한 앨범 이미지의 고유 아이디를 가지고
* 다시 콘텐트 리졸버로 해당 아이디의 이미지 데이터를 가져옵니다.
*
* 가져온 이미지 정보는 크기가 너무클 수도 있어 샘플링 작업(이미지의 크기를 줄여주는 작업)이 필요합니다.
* 이 역할을 getAlbumImage 메서드를 통해서 수행합니다.
*
* 그냥 이미지 크기를 효율적으로 줄여주는 메서드라고 생각하시면 됩니다.
* 크기를 변경하 싶으시면 MAX_IMAGE_SIZE 매개변수의 값을 변경해주시면됩니다.
* (170으로 설정된 값을 입맛에 따라 변경해주시면 됩니다.~)
 * */
public class MyAdapter extends BaseAdapter {

    List<MusicDTO> list; // 뮤직플레이어 리스트
    LayoutInflater inflater; // 레이아웃(xml) 불러와서 해당 xml의 요소 사용가능
    Activity activity;  // 값을 넘겨준 Activity 가져오기

    // 기본 생성자
    public MyAdapter() {}

    // 초기화 생성자
    public MyAdapter(Activity activity, List<MusicDTO> list){
        this.list = list;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { // 리스트 개수 가져오기
        return list.size();
    }

    @Override
    public Object getItem(int position) {   // 해당 아이템의 번호(ID) 가져오기
        return position;
    }

    @Override
    public long getItemId(int position) { // 해당 아이템의 번호(ID) 가져오기
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            // 음악 하나를 보여줄 listview_item.xml을 가져옴
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            // LinearLayout 새로 하나 생성함
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // LinearLayout 넣어줌
            convertView.setLayoutParams(layoutParams);
        }

        // 앨범 사진 넣을 공간 지정
        ImageView imageView = (ImageView) convertView.findViewById(R.id.album);
        // 이미지가 MAX_IMAGE_SIZE보다 크거나 작을 경우 원하는 크기로 앨범 사진을 재조정 해줌
        Bitmap albumImage = getAlbumImage(activity, Integer.parseInt((list.get(position)).getAlbumId()), 170);
        imageView.setImageBitmap(albumImage);

        // 음악 제목 넣을 공간 지정
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());

        // 음악 아티스트 넣을 공간 지정
        TextView artist = (TextView) convertView.findViewById(R.id.artist);
        artist.setText(list.get(position).getArtist());

        return convertView;
    }

    // 이미지의 크기 옵션(크기 재조정 시 사용할 변수)
    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    // 가져온 이미지 정보는 크기가 너무클 수도 있어
    // 이미지 크기를 줄여주는 샘플링 작업
    private Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {
        ContentResolver res = context.getContentResolver();
        // 이미지 파일 들어있는 공간인가? 여기 실행하면서 다시 확인해봐야함
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
        if(uri != null) {
            ParcelFileDescriptor fd = null;
            try{
                fd = res.openFileDescriptor(uri, "r");

                // 가장 가까운 2의 거듭제곱 배율 인수를 계산하고
                // 이를 sBitmapOptionsCache.inSampleSize로 전달하면
                // 디코딩 속도가 빨라지고 품질이 향상됩니다.

                // 크기를 얻어오기 위한옵션 ,
                // inJustDecodeBounds값이 true로 설정되면
                // decoder가 bitmap object에 대해 메모리를 할당하지 않고,
                // 따라서 bitmap을 반환하지도 않는다.
                // 다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
                options.inJustDecodeBounds = true; // bitmap object에 대해 메모리 할당 안함
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                int scale = 0;
                // 사진의 너비나 높이가 MAX_IMAGE_SIZE보다 높으면
                // 크기를 가장 가까운 2의 거듭제곱 배율 인수로 바꿔줌
                if(options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
                    scale = (int) Math.pow(2, (int) Math.round(
                            Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)
                    ));
                }

                options.inJustDecodeBounds = false; // bitmap object에 대해 메모리 할당
                options.inSampleSize = scale; // 크기 바꾼거 전달

                // 재조정한 크기로 이미지 읽어오기
                Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if(b != null) {
                    // 조정한 크기가 MAX_IMAGE_SIZE가 아니라면 MAX_IMAGE_SIZE로 재조정 해줌
                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
                        b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e){
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

}
