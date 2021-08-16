# Android-PixelMeasuringTool
A tool to measure the pixel between items in the picture.

I don't know what might be possible usage. 
I used it in a calculation of the actual distance of the balls in a game called lawn bowls. If you know the approximate height of the phone when the picture was taken (and getting a little bit of help from the gyroscope to be sure that the phone is parallel to the ground) you can get the actual distance between the two items in the picture.

![Alt Text](art/pixel_measure.gif)

## Download

via Gradle:

    dependencies {
        implementation 'com.zekapp.library:pixelmeasuringview:1.0.10'
    }
    
## Usage

    <app.com.pixelmeasuringview.PixelMeasuringView
        xmlns:measure="http://schemas.android.com/apk/res-auto"
        android:id="@+id/image"
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

## Getting Pixel Programmatically

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            PixelMeasuringView measure = (PixelMeasuringView)findViewById(R.id.image);
            measure.setCallback(new PixelMeasuringCallback() {
                @Override
                public void distanceBetweenCircles(float distance) {
                   
                }
            });
        }

![Check Diagram](art/pic-all.png)

## License

        Copyright (C) 2015 Zeki Guler
        Copyright (C) 2011 The Android Open Source Project
        
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        
           http://www.apache.org/licenses/LICENSE-2.0
        
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
