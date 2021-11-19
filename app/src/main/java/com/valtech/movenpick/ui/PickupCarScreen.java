package com.valtech.movenpick.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.valtech.movenpick.BuildConfig;
import com.valtech.movenpick.R;
import com.valtech.movenpick.entities.UserSettings;
import com.valtech.movenpick.interfaces.ResponseParser;
import com.valtech.movenpick.manager.UserSettingsManager;
import com.valtech.movenpick.manager.Utility;
import com.valtech.movenpick.model.CountryModel;
import com.valtech.movenpick.util.HotelPickupConstants.GeneralConstants;
import com.valtech.movenpick.util.HotelPickupConstants.MessageConstants;
import com.valtech.movenpick.util.HotelPickupUtility;
import com.valtech.movenpick.util.PreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_ALERT;
import static com.valtech.movenpick.util.HotelPickupConstants.MessageConstants.MESSAGE_TYPE_MESSAGE;

public class PickupCarScreen extends AppCompatActivity implements OnClickListener, Callback, ResponseParser, OnKeyListener {
    private EditText barCodeField, mobileNumber;
    private ProgressDialog dialog;
    private Handler handler;
    ImageView imgLogo;
    LinearLayout ll_select_;
    ImageView imgv_contry_flag;
    TextView txt_contry_code;
    public static ArrayList<CountryModel> data = new ArrayList<>();
    Button btnSubmit, btnDiscountSubmit;

    static String validMobileNumber;

    public PickupCarScreen() {
        this.barCodeField = null;
        this.dialog = null;
        this.handler = null;
        validMobileNumber = "";
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_pickup_car_screen);

        setData();

        PreferencesUtil.putInt(this, "SelectedCountry", 0);

        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        txt_contry_code = (TextView) findViewById(R.id.txt_contry_code);
        imgv_contry_flag = (ImageView) findViewById(R.id.imgv_contry_flag);
        ll_select_ = (LinearLayout) findViewById(R.id.ll_select_);
        ll_select_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PickupCarScreen.this, CountryPopupActivity.class));

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        btnDiscountSubmit = (Button) findViewById(R.id.btnDiscountSubmit);
        btnDiscountSubmit.setOnClickListener(this);

        init();

    }

    private void init() {
        this.handler = new Handler(this);
        Typeface font = HotelPickupUtility.getDefaultFont(this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle((CharSequence) "Pickup Car");
        // toolBar.setTitleTextColor(Color.parseColor("#ffffff"));
        // toolBar.setBackgroundColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolBar);
        try {
            Field field = toolBar.getClass().getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            TextView title = (TextView) field.get(toolBar);
            title.setTextSize(2, 15.0f);
            title.setTypeface(font);
        } catch (Throwable ignore) {
            Log.e("HotelPickup.PickupCarScreen.init", ignore.getMessage(), ignore);
        }
        this.barCodeField = (EditText) findViewById(R.id.barCodeField);
        this.barCodeField.setTypeface(font);
        this.barCodeField.setHintTextColor(getResources().getColor(R.color.white));
        this.barCodeField.requestFocus();

        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        this.mobileNumber.setTypeface(font);

        this.mobileNumber.setHintTextColor(getResources().getColor(R.color.white));

        this.barCodeField.setOnKeyListener(this);

        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(this.barCodeField, 2);
        ((ImageButton) findViewById(R.id.scanButton)).setOnClickListener(this);
        initCustomLogo();


    }


    public void setData() {


        data = new ArrayList<>();

        CountryModel c1;

        c1 = new CountryModel();

        c1.setCountry_name("Kuwait");
        c1.setCountry_code("+965");
        c1.setContry_flag(R.drawable.kuwait);

        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("UAE");
        c1.setCountry_code("+971");
        c1.setContry_flag(R.drawable.andorra_bb);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Saudi Arabia ");
        c1.setCountry_code("+966");
        c1.setContry_flag(R.drawable.saudiarabia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Bahrain");
        c1.setCountry_code("+973");
        c1.setContry_flag(R.drawable.bahrain);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Qatar ");
        c1.setCountry_code("+974");
        c1.setContry_flag(R.drawable.qatar);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Oman ");
        c1.setCountry_code("+968");
        c1.setContry_flag(R.drawable.oman);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Iraq ");
        c1.setCountry_code("+964");
        c1.setContry_flag(R.drawable.iraq);
        data.add(c1);


        c1 = new CountryModel();

        c1.setCountry_name("Afghanistan");
        c1.setCountry_code("+93");
        c1.setContry_flag(R.drawable.afghanistan);

        data.add(c1);

        c1 = new CountryModel();

        c1.setCountry_name("Albania");
        c1.setCountry_code("+355");
        c1.setContry_flag(R.drawable.albania);

        data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("Angola");
        c1.setCountry_code("+244");
        c1.setContry_flag(R.drawable.angola);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Argentina");
        c1.setCountry_code("+54");
        c1.setContry_flag(R.drawable.argentina);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Armenia");
        c1.setCountry_code("+374");
        c1.setContry_flag(R.drawable.armenia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Aruba");
        c1.setCountry_code("+297");
        c1.setContry_flag(R.drawable.aruba);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Australia");
        c1.setCountry_code("+61");
        c1.setContry_flag(R.drawable.australia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Austria");
        c1.setCountry_code("+43");
        c1.setContry_flag(R.drawable.austria);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Azerbaijan");
        c1.setCountry_code("+994");
        c1.setContry_flag(R.drawable.azerbaijan);
        data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("Bangladesh");
        c1.setCountry_code("+880");
        c1.setContry_flag(R.drawable.bangladesh);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Belarus");
        c1.setCountry_code("+375");
        c1.setContry_flag(R.drawable.belarus);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Belgium");
        c1.setCountry_code("+32");
        c1.setContry_flag(R.drawable.belgium);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Benin");
        c1.setCountry_code("+229");
        c1.setContry_flag(R.drawable.benin);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Bhutan");
        c1.setCountry_code("+975");
        c1.setContry_flag(R.drawable.bhutan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Bolivia");
        c1.setCountry_code("+591");
        c1.setContry_flag(R.drawable.bolivia);
        data.add(c1);

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Bosnia and Herzegovina");
        c1.setCountry_code("+387");
        c1.setContry_flag(R.drawable.bosniaandherzegovina);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Botswana");
        c1.setCountry_code("+267");
        c1.setContry_flag(R.drawable.botswana);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Brazil");
        c1.setCountry_code("+55");
        c1.setContry_flag(R.drawable.brazil);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Brunei");
        c1.setCountry_code("+673");
        c1.setContry_flag(R.drawable.brunei);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Bulgaria");
        c1.setCountry_code("+359");
        c1.setContry_flag(R.drawable.bulgaria);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Burundi");
        c1.setCountry_code("+257");
        c1.setContry_flag(R.drawable.burundi);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Cambodia");
        c1.setCountry_code("+855");
        c1.setContry_flag(R.drawable.cambodia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Cameroon");
        c1.setCountry_code("+237");
        c1.setContry_flag(R.drawable.cameroon);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Canada");
        c1.setCountry_code("+1");
        c1.setContry_flag(R.drawable.canada);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Chile");
        c1.setCountry_code("+56");
        c1.setContry_flag(R.drawable.chile);
        data.add(c1);

        /************************************************************/


        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("China");
        c1.setCountry_code("+86");
        c1.setContry_flag(R.drawable.china);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Colombia");
        c1.setCountry_code("+57");
        c1.setContry_flag(R.drawable.colombia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Costa Rica");
        c1.setCountry_code("+506");
        c1.setContry_flag(R.drawable.costarica);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Croatia ");
        c1.setCountry_code("+385");
        c1.setContry_flag(R.drawable.croatia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Cuba ");
        c1.setCountry_code("+53");
        c1.setContry_flag(R.drawable.cuba);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Czech Republic ");
        c1.setCountry_code("+420");
        c1.setContry_flag(R.drawable.czechrepublic);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Denmark ");
        c1.setCountry_code("+45");
        c1.setContry_flag(R.drawable.denmark);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Ecuador ");
        c1.setCountry_code("+593");
        c1.setContry_flag(R.drawable.ecuador);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Egypt ");
        c1.setCountry_code("+20");
        c1.setContry_flag(R.drawable.egypt);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("El Salvador ");
        c1.setCountry_code("+503");
        c1.setContry_flag(R.drawable.eisalvador);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Estonia ");
        c1.setCountry_code("+372");
        c1.setContry_flag(R.drawable.estonia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Ethiopia ");
        c1.setCountry_code("+251");
        c1.setContry_flag(R.drawable.ethiopia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Finland ");
        c1.setCountry_code("+358");
        c1.setContry_flag(R.drawable.finland);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("France ");
        c1.setCountry_code("+33");
        c1.setContry_flag(R.drawable.france);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Gambia ");
        c1.setCountry_code("+220");
        c1.setContry_flag(R.drawable.gambia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Germany ");
        c1.setCountry_code("+49");
        c1.setContry_flag(R.drawable.germany);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Ghana ");
        c1.setCountry_code("+233");
        c1.setContry_flag(R.drawable.ghana);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Greece ");
        c1.setCountry_code("+30");
        c1.setContry_flag(R.drawable.greece);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Guinea ");
        c1.setCountry_code("+224");
        c1.setContry_flag(R.drawable.guinea);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Guyana ");
        c1.setCountry_code("+592");
        c1.setContry_flag(R.drawable.guyana);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Haiti ");
        c1.setCountry_code("+509");
        c1.setContry_flag(R.drawable.haiti);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Honduras ");
        c1.setCountry_code("+504");
        c1.setContry_flag(R.drawable.honduras);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Hong Kong ");
        c1.setCountry_code("+852");
        c1.setContry_flag(R.drawable.hongkong);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Hungary ");
        c1.setCountry_code("+36");
        c1.setContry_flag(R.drawable.hungary);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Iceland ");
        c1.setCountry_code("+354");
        c1.setContry_flag(R.drawable.iceland);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("India");
        c1.setCountry_code("+91");
        c1.setContry_flag(R.drawable.india);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Indonesia ");
        c1.setCountry_code("+62");
        c1.setContry_flag(R.drawable.indonesia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Iran ");
        c1.setCountry_code("+98");
        c1.setContry_flag(R.drawable.iran);
        data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("Ireland ");
        c1.setCountry_code("+353");
        c1.setContry_flag(R.drawable.ireland);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Israel ");
        c1.setCountry_code("+972");
        c1.setContry_flag(R.drawable.israel);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Italy ");
        c1.setCountry_code("+39");
        c1.setContry_flag(R.drawable.italy);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Japan ");
        c1.setCountry_code("+81");
        c1.setContry_flag(R.drawable.japan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Jordan ");
        c1.setCountry_code("+962");
        c1.setContry_flag(R.drawable.jordan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Kazakhstan ");
        c1.setCountry_code("+7");
        c1.setContry_flag(R.drawable.kazakhstan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Kenya ");
        c1.setCountry_code("+254");
        c1.setContry_flag(R.drawable.kenya);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Kyrgyzstan ");
        c1.setCountry_code("+996");
        c1.setContry_flag(R.drawable.kyrgyzstan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Lebanon ");
        c1.setCountry_code("+961");
        c1.setContry_flag(R.drawable.lebanon);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Liberia ");
        c1.setCountry_code("+231");
        c1.setContry_flag(R.drawable.liberia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Libya ");
        c1.setCountry_code("+218");
        c1.setContry_flag(R.drawable.libya);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Lithuania ");
        c1.setCountry_code("+370");
        c1.setContry_flag(R.drawable.lithuania);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Luxembourg ");
        c1.setCountry_code("+352");
        c1.setContry_flag(R.drawable.luxembourg);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Malaysia ");
        c1.setCountry_code("+60");
        c1.setContry_flag(R.drawable.malaysia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Maldives ");
        c1.setCountry_code("+960");
        c1.setContry_flag(R.drawable.maldives);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Mali ");
        c1.setCountry_code("+223");
        c1.setContry_flag(R.drawable.mali);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Mauritius ");
        c1.setCountry_code("+230");
        c1.setContry_flag(R.drawable.mauritius);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Mexico ");
        c1.setCountry_code("+52");
        c1.setContry_flag(R.drawable.mexico);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Monaco ");
        c1.setCountry_code("+377");
        c1.setContry_flag(R.drawable.monaco);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Mongolia ");
        c1.setCountry_code("+976");
        c1.setContry_flag(R.drawable.mongolia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Morocco ");
        c1.setCountry_code("+212");
        c1.setContry_flag(R.drawable.morocco);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Mozambique ");
        c1.setCountry_code("+258");
        c1.setContry_flag(R.drawable.mozambique);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Myanmar ");
        c1.setCountry_code("+95");
        c1.setContry_flag(R.drawable.myanmar);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Namibia ");
        c1.setCountry_code("+264");
        c1.setContry_flag(R.drawable.namibia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Nepal ");
        c1.setCountry_code("+977");
        c1.setContry_flag(R.drawable.nepal);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Netherlands ");
        c1.setCountry_code("+31");
        c1.setContry_flag(R.drawable.netherlands);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("New Zealand ");
        c1.setCountry_code("+64");
        c1.setContry_flag(R.drawable.newzealand);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Niger ");
        c1.setCountry_code("+227");
        c1.setContry_flag(R.drawable.niger);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Nigeria ");
        c1.setCountry_code("+234");
        c1.setContry_flag(R.drawable.nigeria);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Norway ");
        c1.setCountry_code("+47");
        c1.setContry_flag(R.drawable.norway);
        data.add(c1);


        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Pakistan ");
        c1.setCountry_code("+92");
        c1.setContry_flag(R.drawable.pakistan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Panama ");
        c1.setCountry_code("+507");
        c1.setContry_flag(R.drawable.panama);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Paraguay ");
        c1.setCountry_code("+595");
        c1.setContry_flag(R.drawable.paraguay);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Peru ");
        c1.setCountry_code("+51");
        c1.setContry_flag(R.drawable.peru);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Poland ");
        c1.setCountry_code("+48");
        c1.setContry_flag(R.drawable.poland);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Portugal ");
        c1.setCountry_code("+351");
        c1.setContry_flag(R.drawable.portugal);
        data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("Romania ");
        c1.setCountry_code("+40");
        c1.setContry_flag(R.drawable.romania);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Russian ");
        c1.setCountry_code("+7");
        c1.setContry_flag(R.drawable.russian);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Rwanda ");
        c1.setCountry_code("+250");
        c1.setContry_flag(R.drawable.rwanda);
        data.add(c1);

        /************************************************************/


        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Samoa ");
        c1.setCountry_code("+685");
        c1.setContry_flag(R.drawable.samoa);
        data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("Senegal ");
        c1.setCountry_code("+221");
        c1.setContry_flag(R.drawable.senegal);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Serbia ");
        c1.setCountry_code("+381");
        c1.setContry_flag(R.drawable.serbia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Seychelles ");
        c1.setCountry_code("+248");
        c1.setContry_flag(R.drawable.seychelles);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Sierra Leone ");
        c1.setCountry_code("+232");
        c1.setContry_flag(R.drawable.sierraleone);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Singapore ");
        c1.setCountry_code("+65");
        c1.setContry_flag(R.drawable.singapore);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Slovakia ");
        c1.setCountry_code("+421");
        c1.setContry_flag(R.drawable.slovakia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Slovenia ");
        c1.setCountry_code("+386");
        c1.setContry_flag(R.drawable.slovenia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Somalia ");
        c1.setCountry_code("+252");
        c1.setContry_flag(R.drawable.somalia);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("South Africa ");
        c1.setCountry_code("+27");
        c1.setContry_flag(R.drawable.southafrica);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("South Sudan ");
        c1.setCountry_code("+211");
        c1.setContry_flag(R.drawable.southsudan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Spain ");
        c1.setCountry_code("+34");
        c1.setContry_flag(R.drawable.spain);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Sri Lanka ");
        c1.setCountry_code("+94");
        c1.setContry_flag(R.drawable.srilanka);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Sudan ");
        c1.setCountry_code("+249");
        c1.setContry_flag(R.drawable.sudan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Swaziland ");
        c1.setCountry_code("+268");
        c1.setContry_flag(R.drawable.swaziland);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Sweden ");
        c1.setCountry_code("+46");
        c1.setContry_flag(R.drawable.sweden);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Switzerland ");
        c1.setCountry_code("+41");
        c1.setContry_flag(R.drawable.switzerland);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Taiwan ");
        c1.setCountry_code("+886");
        c1.setContry_flag(R.drawable.taiwan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Tajikistan ");
        c1.setCountry_code("+992");
        c1.setContry_flag(R.drawable.tajikistan);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Tanzania ");
        c1.setCountry_code("+255");
        c1.setContry_flag(R.drawable.tanzania);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Thailand ");
        c1.setCountry_code("+66");
        c1.setContry_flag(R.drawable.thailand);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Togo ");
        c1.setCountry_code("+228");
        c1.setContry_flag(R.drawable.togo);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Tunisia  ");
        c1.setCountry_code("+216");
        c1.setContry_flag(R.drawable.tunisia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Turkey ");
        c1.setCountry_code("+90");
        c1.setContry_flag(R.drawable.turkey);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Turkmenistan ");
        c1.setCountry_code("+993");
        c1.setContry_flag(R.drawable.turkmenistan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Uganda ");
        c1.setCountry_code("+256");
        c1.setContry_flag(R.drawable.uganda);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Ukraine ");
        c1.setCountry_code("+380");
        c1.setContry_flag(R.drawable.ukraine);
        data.add(c1);


//            c1 = new CountryModel();
//            c1.setCountry_name("UAE ");
//            c1.setCountry_code("+971");
//            c1.setContry_flag(R.drawable.ae);
//            data.add(c1);


        c1 = new CountryModel();
        c1.setCountry_name("United Kingdom ");
        c1.setCountry_code("+44");
        c1.setContry_flag(R.drawable.unitedkingdom);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("United States ");
        c1.setCountry_code("+1");
        c1.setContry_flag(R.drawable.unitedstates);
        data.add(c1);

        /************************************************************/

        /**************************************************************/

        c1 = new CountryModel();
        c1.setCountry_name("Uruguay ");
        c1.setCountry_code("+598");
        c1.setContry_flag(R.drawable.uruguay);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Uzbekistan ");
        c1.setCountry_code("+998");
        c1.setContry_flag(R.drawable.uzbekistan);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Vanuatu ");
        c1.setCountry_code("+678");
        c1.setContry_flag(R.drawable.vanuatu);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Viet Nam ");
        c1.setCountry_code("+84");
        c1.setContry_flag(R.drawable.vietnam);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Yemen ");
        c1.setCountry_code("+967");
        c1.setContry_flag(R.drawable.yemen);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Zambia ");
        c1.setCountry_code("+260");
        c1.setContry_flag(R.drawable.zambia);
        data.add(c1);

        c1 = new CountryModel();
        c1.setCountry_name("Zimbabwe ");
        c1.setCountry_code("+263");
        c1.setContry_flag(R.drawable.zimbabwe);
        data.add(c1);

    }


    @Override
    protected void onResume() {
        super.onResume();

        int position = PreferencesUtil.getInt(this, "SelectedCountry", 0);

        txt_contry_code.setText(data.get(position).getCountry_code());

        imgv_contry_flag.setImageResource(data.get(position).getContry_flag());

    }

    public void initCustomLogo() {
        try {

            String path = new Utility(PickupCarScreen.this).getLogoURI();


            if (!path.isEmpty()) {

                File mediaStorageDir = new File(path);
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

                Uri uri = Uri.fromFile(mediaStorageDir);
                final Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(),
                        options);

                imgLogo.setImageBitmap(bitmap);
            } else {
                imgLogo.setImageResource(R.drawable.app_main_logo);
            }


        } catch (Exception exc) {
            imgLogo.setImageResource(R.drawable.app_main_logo);
            exc.printStackTrace();
        }
    }


    protected Dialog onCreateDialog(int id) {
        if (id != 0) {
            return null;
        }
        this.dialog = HotelPickupUtility.createProgressDialog("Loading, please wait...", this);
        return this.dialog;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        if (id == 0) {
            ((ProgressDialog) dialog).setMessage("Loading, please wait...");
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0 && event.getKeyCode() == 66) {
            String barCode = this.barCodeField.getText().toString();
            if (barCode.trim().length() == 8) {
                validateBarCode();
                return true;
            } else {
                barCodeField.setText("");
                barCodeField.setError("Enter valid 8 digits barcode!");
                return false;
            }
        }
        return false;
    }

    public void parseResponse(byte[] response) {
        Message msg;
        Throwable t;
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(response);
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                in.close();
                byteArrayInputStream = null;
                NodeList errorNodeList = doc.getElementsByTagName("error");
                if (errorNodeList == null || errorNodeList.getLength() <= 0) {
                    NodeList eventIDNodeList = doc.getElementsByTagName("eventID");
                    if (eventIDNodeList == null || eventIDNodeList.getLength() == 0) {
                        throw new RuntimeException("Missing node eventID");
                    }
                    String id = eventIDNodeList.item(0).getTextContent();
                    if (id == null || id.trim().length() == 0) {
                        throw new RuntimeException("event id is " + id);
                    }
                    int eventID = Integer.parseInt(id);
                    msg = new Message();
                    msg.what = MessageConstants.MESSAGE_TYPE_FINISHED;
                    msg.obj = Integer.valueOf(eventID);
                    this.handler.sendMessage(msg);
                    if (byteArrayInputStream != null) {
                        try {
                            byteArrayInputStream.close();
                            return;
                        } catch (IOException e) {
                            return;
                        }
                    }
                    return;
                }
                throw new RuntimeException(errorNodeList.item(0).getTextContent());
            } catch (Throwable th2) {
                th = th2;
                byteArrayInputStream = in;
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            t = th3;
            msg = new Message();
            msg.what = -1;
            msg.obj = t;
            this.handler.sendMessage(msg);
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean handleMessage(Message msg) {
        int code = msg.what;
        Object data = msg.obj;
        switch (code) {
            case ExploreByTouchHelper.HOST_ID /*-1*/:
                Throwable err = (Throwable) data;
                String errMessage = err.getMessage();
                if (errMessage == null || errMessage.trim().length() == 0) {
                    errMessage = err.toString();
                }
                this.dialog.cancel();
                HotelPickupUtility.showError(errMessage, this);
                Log.e("HotelPickup.PickupCarScreen.handleMessage", errMessage, err);
                this.barCodeField.setText("");
                this.mobileNumber.setText("");
                break;
            case MESSAGE_TYPE_ALERT /*2320*/:
                this.dialog.dismiss();
                HotelPickupUtility.showError(msg.obj, this);
                break;
            case MESSAGE_TYPE_MESSAGE:
                this.dialog.setMessage(data.toString());
                break;
            case MessageConstants.MESSAGE_TYPE_FINISHED /*2320*/:
                this.dialog.dismiss();
                if (data instanceof Integer) {
                    onEventIDFetched(((Integer) data).intValue());
                    break;
                }
                break;
        }
        return true;
    }

    private void onEventIDFetched(int eventID) {


        String mobile = mobileNumber.getText().toString();
        if (!mobile.equals("")) {
            validMobileNumber = txt_contry_code.getText().toString() + mobileNumber.getText().toString() + "";
        }


        Log.d("validMobileNumber", validMobileNumber);


        Intent nextIntent = new Intent(this, PickupAskKeyTagScreen.class);
        nextIntent.putExtra(GeneralConstants.BARCODE, this.barCodeField.getText().toString());
        nextIntent.putExtra(GeneralConstants.EVENT_ID, eventID);
        nextIntent.putExtra(GeneralConstants.MOBILE_NUM, validMobileNumber);
        startActivity(nextIntent);
        finish();
    }

    public void doSubmit() {

        try {

            UserSettings settings = UserSettingsManager.getUserSettings(this);
            String apiURL = settings.getApiURL();
            String eventIDs = settings.joinedEventIDs();
            int mode = 1;
            if (settings.getMode().equalsIgnoreCase("free")) {
                mode = 1;
            } else {
                mode = 2;
            }
            if (apiURL == null || apiURL.trim().length() == 0) {
                throw new RuntimeException("Missing api url");
            }

            if (eventIDs == null || eventIDs.trim().length() == 0) {
                throw new RuntimeException("Set Events from Settings!");
            }


            apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=checkpickup").toString();


            String postData = "barcode=" + URLEncoder.encode(this.barCodeField.getText().toString(), "UTF-8") +
                    "&eventids=" + URLEncoder.encode(eventIDs, "UTF-8") +
                    "&pickupmode=" + mode;
            Log.e("postData", postData);
            byte[] postBytes = postData.getBytes();


            Log.e("apiURL", apiURL);
            Bridge.post(apiURL)
                    .throwIfNotSuccess()
                    .header("app_version", BuildConfig.VERSION_CODE + "")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(postBytes)
                    .request(new com.afollestad.bridge.Callback() {
                        @Override
                        public void response(com.afollestad.bridge.Request request, @Nullable com.afollestad.bridge.Response response, @Nullable BridgeException e) {

                            if (e != null) {


                                Log.d("response", String.valueOf(e.getMessage()));
                                Message msg = new Message();
                                msg.what = ExploreByTouchHelper.HOST_ID;
                                msg.obj = e;
                                handler.sendMessage(msg);

                            } else {
                                Log.d("response", String.valueOf(response.toString()));

                                String responseString = response.asString();

                                Log.d("response", String.valueOf(responseString));


                                try {
                                    JSONObject jsonObject = XML.toJSONObject(responseString);
                                    JSONObject serviceJson = jsonObject.getJSONObject("service");

                                    if(serviceJson.has("transamount")){
                                        String mobile = mobileNumber.getText().toString();
                                        if (!mobile.equals("")) {
                                            validMobileNumber = txt_contry_code.getText().toString() + mobileNumber.getText().toString() + "";
                                        }
                                        PickupCarScreen.this.dialog.dismiss();
                                        Intent intent = new Intent(PickupCarScreen.this, DiscountScreen.class);
                                        intent.putExtra("Json",serviceJson.toString());
                                        intent.putExtra(GeneralConstants.MOBILE_NUM, validMobileNumber);
                                        startActivity(intent);
                                        return;
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                }
                                parseResponse(response.asBytes());


                            }
                        }


                    });


        } catch (Exception e) {

            Message msg = new Message();
            msg.what = ExploreByTouchHelper.HOST_ID;
            msg.obj = e;
            handler.sendMessage(msg);

        }


    }


    public void doVerifyMobile() {

        String mobileNum = txt_contry_code.getText().toString() + mobileNumber.getText().toString() + "";
        Log.d("mobileNumInput", mobileNum);


        Bridge.get("http://phonelookup.smscamp.com/api/Home/lookup?num=" + mobileNum)
                .throwIfNotSuccess()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .request(new com.afollestad.bridge.Callback() {
                    @Override
                    public void response(com.afollestad.bridge.Request request, @Nullable com.afollestad.bridge.Response response, @Nullable BridgeException e) {

                        if (e != null) {


                            Log.d("response", String.valueOf(e.getMessage()));
                            Message msg = new Message();
                            msg.what = ExploreByTouchHelper.HOST_ID;
                            msg.obj = e;
                            handler.sendMessage(msg);


                        } else {
                            try {
                                Log.d("response", String.valueOf(response.asJsonObject()));

                                JSONObject jObj = response.asJsonObject();

                                if (jObj.getString("status").equals("valid")) {


                                    if (jObj.getJSONObject("carrier").getString("type").equals("landline")) {
                                        Message msg = new Message();
                                        msg.what = MESSAGE_TYPE_ALERT;
                                        msg.obj = "This is LandLine Number!";
                                        handler.sendMessage(msg);
                                    } else {
                                        validMobileNumber = jObj.getString("phone_number");

                                        Message msg = new Message();
                                        msg.what = MESSAGE_TYPE_MESSAGE;
                                        msg.obj = "Mobile validation successful. Checking barcode...";
                                        handler.sendMessage(msg);
                                        //  onEventIDFetched(12345);
                                        doSubmit();
                                    }


                                } else {

                                    Message msg = new Message();
                                    msg.what = MESSAGE_TYPE_ALERT;
                                    msg.obj = "Mobile Number is Invalid!";
                                    handler.sendMessage(msg);
                                }
                            } catch (BridgeException e1) {
                                e1.printStackTrace();

                                Log.d("response", String.valueOf(e.getMessage()));
                                Message msg = new Message();
                                msg.what = ExploreByTouchHelper.HOST_ID;
                                msg.obj = e1;
                                handler.sendMessage(msg);

                            } catch (JSONException e1) {

                                Log.d("response", String.valueOf(e.getMessage()));
                                Message msg = new Message();
                                msg.what = ExploreByTouchHelper.HOST_ID;
                                msg.obj = e1;
                                handler.sendMessage(msg);
                                e1.printStackTrace();
                            }


                        }
                    }


                });


    }


    public void onClick(View view) {
        if (view.getId() == R.id.scanButton) {


            onScanClicked();

        }

        if (view.getId() == R.id.btnSubmit) {

            UserSettings settings = UserSettingsManager.getUserSettings(this);

            int isMobile = settings.getMobileOptional();

            boolean isKuwait = true;

            if (txt_contry_code.getText().toString().trim().equals("+965")) {
                if (mobileNumber.getText().toString().startsWith("5") || mobileNumber.getText().toString().startsWith("6") || mobileNumber.getText().toString().startsWith("9")) {
                    isKuwait = true;
                } else {
                    isKuwait = false;
                }
            }

            Log.d("isKuwait", isKuwait + "");
            Log.d("isMobile", isMobile + "");

            if (barCodeField.getText().toString().equals("")) {
                barCodeField.setError("Please Enter or Scan Barcode");
            } else if (mobileNumber.getText().toString().equals("") && isMobile == 0) {
                mobileNumber.setError("Valid Mobile Number Required");
            } else if (!isKuwait && isMobile == 0) {
                mobileNumber.setError("Valid Mobile Number Required");
            } else {

                validateBarCode();

            }


        }

        if (view.getId() == R.id.btnDiscountSubmit) {

            UserSettings settings = UserSettingsManager.getUserSettings(this);

            int isMobile = settings.getMobileOptional();

            boolean isKuwait = true;

            if (txt_contry_code.getText().toString().trim().equals("+965")) {
                if (mobileNumber.getText().toString().startsWith("5") || mobileNumber.getText().toString().startsWith("6") || mobileNumber.getText().toString().startsWith("9")) {
                    isKuwait = true;
                } else {
                    isKuwait = false;
                }
            }

            Log.d("isKuwait", isKuwait + "");
            Log.d("isMobile", isMobile + "");

            if (barCodeField.getText().toString().equals("")) {
                barCodeField.setError("Please Enter or Scan Barcode");
            } else if (mobileNumber.getText().toString().equals("") && isMobile == 0) {
                mobileNumber.setError("Valid Mobile Number Required");
            } else if (!isKuwait && isMobile == 0) {
                mobileNumber.setError("Valid Mobile Number Required");
            } else {
                showDialog(0, Bundle.EMPTY);


                Log.d("isMobile", isMobile + "");
                if (isMobile == 1) {
                    doSubmitDiscount();
                } else {
                    //doVerifyMobile();
                    doSubmitDiscount();
                }

            }


        }


    }

    private void doSubmitDiscount() {
        try {

            UserSettings settings = UserSettingsManager.getUserSettings(this);
            String apiURL = settings.getApiURL();
            String eventIDs = settings.joinedEventIDs();
            if (apiURL == null || apiURL.trim().length() == 0) {
                throw new RuntimeException("Missing api url");
            }

            if (eventIDs == null || eventIDs.trim().length() == 0) {
                throw new RuntimeException("Set Events from Settings!");
            }


            apiURL = new StringBuilder(String.valueOf(apiURL)).append("?action=checkpickupspecialprice").toString();


            String postData = "barcode=" + URLEncoder.encode(this.barCodeField.getText().toString(), "UTF-8") + "&eventids=" + URLEncoder.encode(eventIDs, "UTF-8");
            Log.e("postData", postData);
            byte[] postBytes = postData.getBytes();


            Log.e("apiURL", apiURL);
            Bridge.post(apiURL)
                    .throwIfNotSuccess()
                    .header("app_version", BuildConfig.VERSION_CODE + "")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(postBytes)
                    .request(new com.afollestad.bridge.Callback() {
                        @Override
                        public void response(com.afollestad.bridge.Request request, @Nullable com.afollestad.bridge.Response response, @Nullable BridgeException e) {

                            if (e != null) {


                                Log.d("response", String.valueOf(e.getMessage()));
                                Message msg = new Message();
                                msg.what = ExploreByTouchHelper.HOST_ID;
                                msg.obj = e;
                                handler.sendMessage(msg);

                            } else {
                                Log.d("response", String.valueOf(response.toString()));


                                parseResponse(response.asBytes());


                            }
                        }


                    });


        } catch (Exception e) {

            Message msg = new Message();
            msg.what = ExploreByTouchHelper.HOST_ID;
            msg.obj = e;
            handler.sendMessage(msg);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(this, QRCodeScanner.class), 1);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void onScanClicked() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                startActivityForResult(new Intent(this, QRCodeScanner.class), 1);
            }
        } else {
            startActivityForResult(new Intent(this, QRCodeScanner.class), 1);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
            String barcode = data.getStringExtra("scanned.barCode");
            if (barcode != null) {
                barcode = HotelPickupUtility.decryptBarcode(barcode, this);
            }
            this.barCodeField.setText(barcode);


        }
    }

    private void validateBarCode() {
        showDialog(0, Bundle.EMPTY);


        UserSettings settings = UserSettingsManager.getUserSettings(this);

        int isMobile = settings.getMobileOptional();

        Log.d("isMobile", isMobile + "");
        if (isMobile == 1) {
            doSubmit();
        } else {
            //doVerifyMobile();
            doSubmit();
        }


        //  startActivity(new Intent(this, PickupAskKeyTagScreen.class).putExtra(GeneralConstants.BARCODE, this.barCodeField.getText().toString()).putExtra(GeneralConstants.EVENT_ID, 12345).putExtra("MobileNumber", txt_contry_code.getText().toString() + mobileNumber.getText().toString()));

    }
}
