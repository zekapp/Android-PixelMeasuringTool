# Android-PixelMeasuringTool
A imageView with a tool to calculate the pixel between items in the picture

![alt tag](https://github.com/zekapp/Android-PixelMeasuringTool/blob/master/art/pic-1.png)
![alt tag](https://github.com/zekapp/Android-PixelMeasuringTool/blob/master/art/pic-2.png)
![alt tag](https://github.com/zekapp/Android-PixelMeasuringTool/blob/master/art/pic-3.png)

## Download

via Gradle:

    dependencies {
      compile 'com.zekapp.library:pixelmeasuringview:1.0.7'
    }
    
## Usage

    <app.com.pixelmeasuringview.PixelMeasuringView
        xmlns:measure="http://schemas.android.com/apk/res-auto"
        android:id="@+id/lawn_ball_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true"
        android:scaleType="centerCrop"
        android:background="@drawable/background"
        measure:is_line_all_visible="true"
        measure:is_measure_text_visible="true"
        measure:circles_color="@android:color/black"
        measure:ruler_color="@android:color/black"
        />
