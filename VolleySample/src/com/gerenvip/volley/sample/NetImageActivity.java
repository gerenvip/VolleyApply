package com.gerenvip.volley.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.toolbox.ImageLoader;
import com.gerenvip.volley.net.ImageOptions;
import com.gerenvip.volley.net.NetClient;

/**
 * 图片加载的Demo
 */
public class NetImageActivity extends Activity {

    private static String[] imageUrls = {
            "http://test.designer.c-launcher.com/resources/wallpaper/img/848/5397d1250cf267d0f0d15dd8/1402458405568/wallpaper_s.jpg",
            "http://test.designer.c-launcher.com/resources/wallpaper/img/391/5397eb860cf20c6436f7565f/1402465158841/wallpaper_s.jpg",
            //"http://test.designer.c-launcher.com/resources/wallpaper/img/333/5397e5d50cf244d2003552cd/1402463701155/wallpaper_s.jpg",
            "http://img.xgo-img.com.cn/pics/1645/1644205.jpg",
            "http://test.designer.c-launcher.com/resources/wallpaper/img/246/5397d1310cf267d0f0d15dde/1402458417493/wallpaper_s.jpg"
    };
    private ImageView iv1, iv2, iv3, iv4;
    private NetClient mNetClient;
    private ImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);
        mNetClient = NetClient.getInstance();
//        mNetClient.setDefaultImageLoaderRes(android.R.drawable.ic_menu_edit, R.drawable.ic_launcher, 480, 800);
        options = new ImageOptions.Builder()
                .setImageOnLoading(android.R.drawable.ic_dialog_dialer)
                .setImageOnFail(android.R.drawable.ic_delete)
                .setImageSize(320, 480)
                .build();
    }

    public void loadImages(View v) {
        //加载默认大小图片
        mNetClient.loadImage(imageUrls[0], iv1);
        //指定加载图片的规格
        mNetClient.loadImage(imageUrls[1], iv2, options);
        //加载图片时带动画
        mNetClient.loadImageWithAnimation(this, imageUrls[2], iv3, options, R.anim.demo_fade_in);
        //使用imageRequest加载图片
        mNetClient.loadImageByRequest(imageUrls[3], iv4);
    }

    static class ViewHolder {
        ImageLoader.ImageContainer imageContainer;
        ImageView ivHolder;
    }
}
