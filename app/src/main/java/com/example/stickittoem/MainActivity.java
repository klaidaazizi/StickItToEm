package com.example.stickittoem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RadioButton radioButton1, radioButton2, radioButton3;
    private ImageView image1, image2, image3;
    private Map<RadioButton, ImageView> imageToButtonMap;
    private ArrayList<RadioButton> buttons;
    private ImageView checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showImages();

    }

    private void showImages() {
            imageToButtonMap = new HashMap<>();
            image1 = (ImageView) findViewById(R.id.imageView1);
            image2 = (ImageView) findViewById(R.id.imageView2);
            image3 = (ImageView) findViewById(R.id.imageView3);

            buttons = new ArrayList<>();
            radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
            buttons.add(radioButton1);
            imageToButtonMap.put(radioButton1, image1);
            radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
            buttons.add(radioButton2);
            imageToButtonMap.put(radioButton2, image2);
            radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
            buttons.add(radioButton3);
            imageToButtonMap.put(radioButton3, image3);

        }

        private void selectImage(View view){
            for (RadioButton button: buttons){
                button.setChecked(false);
            }

            RadioButton checkedButton = findViewById(view.getId());
            checkedButton.setChecked(true);
            checked = imageToButtonMap.get(checkedButton);
        }


}