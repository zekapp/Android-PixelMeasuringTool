# Android-PixelMeasuringTool
A imageView with a tool to calculate the pixel between items in the picture

## Download

via Gradle:

    repositories {
      maven {
          url 'https://dl.bintray.com/zekapp/Maven/'
      }
    }
    dependencies {
      compile 'com.zekapp.library:pixelmeasuringview:1.0.3'
    }
    
## Usage


    <app.com.pixelmeasuringview.PixelMeasuringView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:is_line_all_visible="false"
        app:is_measure_text_visible="true"/>
