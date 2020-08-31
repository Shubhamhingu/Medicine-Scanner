package com.example.scroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    Button takePicture;
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    ImageView photo;
    private Bitmap bitmap;
    TextView textView,textView2;
    private static final int INPUT_SIZE = 224;
    private Executor executor = Executors.newSingleThreadExecutor();
    private com.example.scroller.Classifier classifier;
    private static final String MODEL_PATH = "model.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels.txt";
    public String resf,res;
    public String arr[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePicture=findViewById(R.id.btnCamera);
        //imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.tvResult);
        textView2=findViewById(R.id.tvSecond);
        photo=findViewById(R.id.medpic);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromCamera();
            }
        });

        initTensorFlowAndLoadModel();
    }
    public String textset(String k){
        switch (k) {
            case "0":
                resf = "Crocin Pain Relief provides targeted pain relief. It provides symptomatic relief from mild to moderate pain e.g from headache, migraine, toothache and musculoskeletal pain. Its formula contains clinically proven ingredients paracetamol and caffeine. It acts at the center of pain.\nCrocin Advance Strips provides fast and effective relief from pain. It is India’s first paracetamol tablet with optizorb technology. It is known suitable for people with heart conditions , high blood pressure and sensitive stomach.\n\nKey Ingredients:-\n1)Paracetamol\n\nKey Benefits:-\n1)It gets absorbed 25% faster than standard paracetamol\n2)Used to relieve backache, headache and toothache\n3)Reduces the pain caused due to the fever\n4)Relieves mild pain in the muscles\n5)Treats the pain that is set after vaccination that sometimes leads to fever";
                break;
            case "1":
                resf = "Dolo 650 Tablet is a medicine used to relieve pain and to reduce fever. It is used to treat many conditions such as headache, body ache, toothache and common cold.\nDolo 650 Tablet may be prescribed alone or in combination with another medicine. You should take it regularly as advised by your doctor. It is usually best taken with food otherwise it may upset your stomach. Do not take more or use it for longer than recommended. Side effects are rare if this medicine is used correctly but this medicine may cause stomach pain, nausea, and vomiting in some people. Consult your doctor if any of these side effects bother you or do not go away.\n\nUSES OF DOLO TABLET:-\n1)Pain relief.\n2)Fever.\n\nSIDE EFFECTS OF DOLO TABLET:-\n1)Most side effects do not require any medical attention and disappear as your body adjusts to the medicine. Consult your doctor if they persist or if you’re worried about them.\nCommon side effects of Dolo:-\n1)No common side effects seen.";
                break;
            case "2":
                resf = "Ondem -MD 4 Tablet is an antiemetic medicine commonly used to control nausea and vomiting due to certain medical conditions like stomach upset. It is also used to prevent nausea and vomiting caused due to any surgery, cancer drug therapy or radiotherapy.\nUSES OF ONDEM TABLET MD:-\n1)Nausea.\n2)Vomiting.\n\nSIDE EFFECTS OF ONDEM TABLET MD:=\nMost side effects do not require any medical attention and disappear as your body adjusts to the medicine. Consult your doctor if they persist or if you’re worried about them.\n\nCommon side effects of Ondem:-\n1)Constipation.\n2)Diarrhea.\n3)Fatigue.\n4)Headache.";
                break;
            case "3":
                resf = "Saridon is a mild analgesic, primarily used as a pain reliever and used to treat mild fever. It is also used to get instant relief from the pain in the case of a backache, a headache, arthritis and a toothache. It also reduces pain in the body caused due to fever. It has a fast acting formula and provides quick relief.\nKey Ingredients:\n1) Paracetamol I.P. 250 mg.\n2) Propyphenazone I.P. 150 mg.\n3) Caffeine I.P. (anhydrous) 50 mg.\n\nKey Benefits:-\n1) Gets rid of the pain in as fast as 30 minutes after consumption.\n2) Works on triple action formula that blocks prostaglandin secretion, pain signal transmission along with modulating the intensity of pain.\n3) Effective medicine to treat fever, backache, headache, arthritis, toothache.\n4) Helps in reducing pain associated with fever,cold and flu.\n\nDosage:\n1) Adults: Single dose of 1 tablet. Take 3 doses in 24 hours in case of necessity.\n2) Not to be consumed by children younger than 12 years.\n\nSafety Information:\n1) Read the label carefully before use.\n2) Keep out of the reach of children.\n3) Do not exceed the recommended dose.";
                break;
            case "4":
                resf = "Rantac 150 MG Tablet is a very effective medicine that is used to reduce the amount of acid produced in the stomach. It is used to treat and prevent stomach ulcers, gastroesophageal reflux disease (GERD) and other acidity related disorders. It also relieves indigestion and sour stomach due to excess acid production in the stomach.\nSide effects:-\n1)Major & minor side effects for Rantac 150 MG Tablet.\n2)Headache.\n3)Dizziness.\n4)Nausea or vomiting.\n5)Diarrhea.\n6)Stomach pain.\n7)Mental confusion.\n8)Muscle pain.\n9)Difficulty in breathing.\n10)Gynecomastia (Growth of breast in males).";
                break;
        }
        return resf;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission() == PackageManager.PERMISSION_GRANTED) {
            takePicture.setVisibility(View.VISIBLE);
        } else {
            takePicture.setVisibility(View.INVISIBLE);
            makeRequest();
        }
    }

    private int checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
            final List<com.example.scroller.Classifier.Recognition> results = classifier.recognizeImage(bitmap);
            String substring = results.toString().substring(3, results.toString().length() - 1);
            if(substring.contains(",")){
                textView.setText("Please Retry Again");
                textView2.setText("");
                photo.setImageDrawable(null);

            }

            else{
                textView.setText(substring);
                arr=results.toString().split(" ");
                //textView2.setText(arr[0]);
                res=textset(arr[0].substring(1));
                int resk = getResources().getIdentifier(substring, "drawable", this.getPackageName());
                photo.setImageResource(resk);
                textView2.setText(res);
            }

        }
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = com.example.scroller.TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

}
