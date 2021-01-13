package com.example.madcamp2nd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<MusicDto> list;
    private MediaPlayer mediaPlayer;
    private TextView title;
    private TextView artist;
    private ImageView album,previous,play,pause,next;
    private SeekBar seekBar;
    boolean isPlaying = true;
    private ContentResolver res;
    private ProgressUpdate progressUpdate;
    private int position;
    private ImageButton recomm;
    private ListView friends_list;
    Context context;
    List<String> friends_string_list = new ArrayList<>();;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        System.out.println("start of oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);
        Intent intent = getIntent();
        System.out.println("after get intent");
        mediaPlayer = new MediaPlayer();
        title = (TextView)findViewById(R.id.title);
        artist = (TextView)findViewById(R.id.artist);
        album = (ImageView)findViewById(R.id.album);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        position = intent.getIntExtra("position",0);
        list = (ArrayList<MusicDto>) intent.getSerializableExtra("playlist");
        res = getContentResolver();
        previous = (ImageView)findViewById(R.id.pre);
        play = (ImageView)findViewById(R.id.play);
        pause = (ImageView)findViewById(R.id.pause);
        next = (ImageView)findViewById(R.id.next);
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        friends_string_list.add("이우현");
        Users user = (Users) intent.getSerializableExtra("user");
        Call<Users> call = RetrofitClient.getApiService().usersave(user);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()){
                    Log.e("Music Activity", "on response");
//                                Log.e("ta", "dddd " + response.body().getContacts());
//
//                    temp_name =  response.body().getUid();
//                    temp_number = response.body().getContacts();
////                                    temp_gallery = response.body().getGallery();
//
//                    user.setContacts(temp_number);
////                                    user.setGallery(temp_gallery);
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.putExtra("user", user);
//                    startActivity(intent);
//                    finish();
                }
                else{
//                    Log.e("Login Activity", "dddd login fail");
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {
//                Log.e("Login Activity", "dddd login fail");
            }
        });
        friends_list = (ListView) findViewById(R.id.friends_list);
        TextView friends_header = new TextView(context);
        friends_header.setText("누구에게 추천할까요?");
        friends_list.addHeaderView(friends_header);
        recomm = (ImageButton)findViewById(R.id.recommendation);
        System.out.println("before setonclicklistener");
        recomm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("onclick start");
                if (friends_list.getVisibility() == View.VISIBLE){
                    friends_list.setVisibility(View.INVISIBLE);
                }
                else {
                    friends_list.setVisibility(View.VISIBLE);
                    FriendsListAdapter adapter = new FriendsListAdapter((Activity) context, friends_string_list);
                    friends_list.setAdapter(adapter);
                    //                friends_list.setOnClickListener(new AdapterView.OnItemClickListener(){
                    //                    @Override
                    //                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //                        Intent intent = new Intent(this, RecommendActivity.class);
                    //                        intent.putExtra("position", position);
                    //                        System.out.println("before starting activity");
                    //                        startActivity(intent);
                    //                        System.out.println("after starting activity");
                    //                    }
                    //                });
                }
                System.out.println("onclick end");
            }
        });
        System.out.println("before playing music");
        playMusic(list.get(position));
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();
        System.out.println("before setonseekbar");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress()>0 && play.getVisibility()==View.GONE){
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position+1<list.size()) {
                    position++;
                    playMusic(list.get(position));
                }
            }
        });
        System.out.println("end of oncreate");
    }
    public void playMusic(MusicDto musicDto) {
        try {
            seekBar.setProgress(0);
            title.setText(musicDto.getTitle());
            artist.setText(musicDto.getArtist());
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+musicDto.getId());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
//            Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int dpWidth = displayMetrics.widthPixels;
            int dpHeight = displayMetrics.heightPixels;
            Bitmap bitmap = musicDto.getAlbumImage(this, Integer.parseInt(musicDto.getAlbumId()), dpWidth);
            album.setImageBitmap(bitmap);
            Glide.with(getApplicationContext()).load(Uri.parse("content://media/external/audio/albumart/" + musicDto.getAlbumId()))
                    .apply(bitmapTransform(new BlurTransformation(22)))
                    .into((ImageView)findViewById(R.id.background));
        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }
    //앨범이 저장되어 있는 경로를 리턴합니다.
    private static String getCoverArtPath(long albumId, Context context) {
        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
            case R.id.pre:
                if(position-1>=0 ){
                    position--;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                }
                break;
            case R.id.next:
                if(position+1<list.size()){
                    position++;
                    playMusic(list.get(position));
                    seekBar.setProgress(0);
                }
                break;
        }
    }
    class ProgressUpdate extends Thread{
        @Override
        public void run() {
            while(isPlaying){
                try {
                    Thread.sleep(500);
                    if(mediaPlayer!=null){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate",e.getMessage());
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}