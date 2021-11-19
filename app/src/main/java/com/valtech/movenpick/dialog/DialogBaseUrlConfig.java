package com.valtech.movenpick.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.valtech.movenpick.R;
import com.valtech.movenpick.util.PreferencesUtil;

import java.net.MalformedURLException;
import java.net.URL;

import static com.valtech.movenpick.util.HotelPickupUtility.showError;

public class DialogBaseUrlConfig extends Dialog {

    private Context mContext;
    private Button cancelButton, continueButton;


    public DialogBaseUrlConfig(final Context context) {
        super(context);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.dialog_baseurl_config);
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);

        this.mContext = context;


        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        final EditText etBaseURl = (EditText) findViewById(R.id.etBaseURl);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String baseUrl = etBaseURl.getText().toString();

                if (!baseUrl.equalsIgnoreCase("")) {

                    try {

                        URL url = new URL(baseUrl);

                        PreferencesUtil.putString(mContext, "BASE_URL", baseUrl);



                        dismiss();


                    } catch (MalformedURLException e) {

                        showError("Invalid base URL", mContext);
                    }


                } else {
                    showError("Please define base URL", mContext);
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });


    }

    public void dismiss() {
        super.dismiss();
    }


}


