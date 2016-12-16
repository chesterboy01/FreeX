package io.github.chesterboy01.freex;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.github.chesterboy01.freex.Fragment.FragHistory;
import io.github.chesterboy01.freex.Fragment.FragIndex;
import io.github.chesterboy01.freex.Fragment.FragTrade;
import io.github.chesterboy01.freex.Fragment.FragUser;
import io.github.chesterboy01.freex.dialog.ActionItem;
import io.github.chesterboy01.freex.dialog.TitlePopup;
import io.github.chesterboy01.freex.entity.User;
import io.github.chesterboy01.freex.net.CookieApplication;


public class MainActivity extends AppCompatActivity implements UserPass,View.OnClickListener, FragIndex.OnFragmentInteractionListener,FragHistory.OnFragmentInteractionListener,FragTrade.OnFragmentInteractionListener,FragUser.OnFragmentInteractionListener {
    private Fragment[] fragmentArray;
    private TextView txt_title;
    private ImageView img_right;
    protected static final String TAG = "MainActivity";
    public FragIndex index_fragment;
    public FragTrade trade_fragment;
    public FragHistory history_fragment;
    public FragUser user_fragment;
    private ImageView[] bottom_imagebuttons;
    private TextView[] bottom_button_textviews;
    private TitlePopup titlePopup;
    private int index;
    private int currentTabIndex;
    Application appCtx;
    Activity act;

    User conUser;
    int QRflag = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appCtx = getApplication();
        act = this;

        Intent intent = this.getIntent();
        conUser = (User)intent.getSerializableExtra("user");

        txt_title = (TextView) findViewById(R.id.txt_title);
        img_right = (ImageView) findViewById(R.id.img_right);

        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(R.drawable.icon_add);

        //交易未读机制暂时不设定

        index_fragment = new FragIndex();
        trade_fragment = new FragTrade();
        history_fragment = new FragHistory();
        user_fragment = new FragUser();


        fragmentArray = new Fragment[] {index_fragment, trade_fragment,
                history_fragment, user_fragment};

        bottom_imagebuttons = new ImageView[4];
        bottom_imagebuttons[0] = (ImageView) findViewById(R.id.ib_weixin);
        bottom_imagebuttons[1] = (ImageView) findViewById(R.id.ib_contact_list);
        bottom_imagebuttons[2] = (ImageView) findViewById(R.id.ib_find);
        bottom_imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);

        bottom_button_textviews = new TextView[4];
        bottom_button_textviews[0] = (TextView) findViewById(R.id.tv_weixin);
        bottom_button_textviews[1] = (TextView) findViewById(R.id.tv_contact_list);
        bottom_button_textviews[2] = (TextView) findViewById(R.id.tv_find);
        bottom_button_textviews[3] = (TextView) findViewById(R.id.tv_profile);

        bottom_button_textviews[0].setTextColor(0xFF45C01A);

        getFragmentManager().beginTransaction().
                add(R.id.fragment_container,index_fragment).
                add(R.id.fragment_container,trade_fragment).
                add(R.id.fragment_container,history_fragment).
                add(R.id.fragment_container,user_fragment)
                .hide(trade_fragment).
                hide(history_fragment).
                hide(user_fragment).
                show(index_fragment).
                commit();

        //img_right.setOnClickListener(this);

        img_right.setOnClickListener(this);

        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, R.string.menu_qrcode,
                R.drawable.icon_menu_sao));
        /*titlePopup.addAction(new ActionItem(this, R.string.menu_money,
                R.drawable.abv));*/
    }

    public void onTabClicked(View view) {
        img_right.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.re_weixin:
                img_right.setVisibility(View.VISIBLE);
                index = 0;
                if (index_fragment != null) {
                    index_fragment.refresh();
                }
                txt_title.setText(R.string.app_name);
                img_right.setImageResource(R.drawable.icon_add);
                break;
            case R.id.re_contact_list:
                index = 1;
                txt_title.setText("Trade");
                img_right.setVisibility(View.VISIBLE);
                img_right.setImageResource(R.drawable.icon_titleaddfriend);
                break;
            case R.id.re_find:
                index = 2;
                txt_title.setText("History");
                break;
            case R.id.re_profile:
                index = 3;
                txt_title.setText("User");
                break;
        }
        if (currentTabIndex != index) {
            android.app.FragmentTransaction trx = getFragmentManager()
                    .beginTransaction();
            trx.hide(fragmentArray[currentTabIndex]);
            if (!fragmentArray[index].isAdded()) {
                trx.add(R.id.fragment_container, fragmentArray[index]);
            }
            trx.show(fragmentArray[index]).commit();
        }
        bottom_imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        bottom_imagebuttons[index].setSelected(true);
        bottom_button_textviews[currentTabIndex].setTextColor(0xFF999999);
        bottom_button_textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }




    private TitlePopup.OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {
        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 扫一扫
                    IntentIntegrator integrator = new IntentIntegrator(act);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.initiateScan();
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_right:
                if (index == 0) {
                    titlePopup.show(findViewById(R.id.layout_bar));
                }
                /*else {
                    Utils.start_Activity(MainActivity.this, PublicActivity.class,
                            new BasicNameValuePair(Constants.NAME, "添加朋友"));
                }*/
                break;
            default:
                break;
        }
    }

    @Override
    public User getUser(){
        return conUser;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        JSONObject objSell=null;
        JSONObject objBuy=null;
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(act, "Scanning Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String sellerStrResult = result.getContents();
                try {
                    objSell = new JSONObject(sellerStrResult);
                    objBuy = new JSONObject(sellerStrResult);

                    objBuy.put("thuid",conUser.getUid());
                    objBuy.put("cidout",objSell.getString("cidin"));
                    objBuy.put("cidin",objSell.getString("cidout"));
                    Double rate = new Double(1.0/Double.valueOf(objSell.getString("rate")));
                    Double d_amount = new Double(objSell.getString("thamount"));
                    Double d_buy_amount = new Double(d_amount/rate);

                    objBuy.put("rate",rate.toString());
                    objBuy.put("thamount",d_buy_amount.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new LoginAsyncTradeBuyQR().execute(objSell,objBuy);
                //while(QRflag == 0);
                Toast.makeText(act, "Scanning is done.", Toast.LENGTH_LONG).show();
            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class LoginAsyncTradeBuyQR extends AsyncTask<JSONObject, Void, Boolean> {
        protected void onPreExecute() {

        }
        protected Boolean doInBackground(JSONObject... params) {
            String result;
            //params[0]就是我要传进来的Transaction_history对象
            try {
                QRflag = 0;
                String URL = "http://192.168.95.1:8080/FreeX_Server/AddQRTransaction.action";
                result = null;
                HttpPost request = new HttpPost(URL);
                try{
                    JSONObject json1 = params[0];
                    JSONObject json2 = params[1];

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(json1);
                    jsonArray.put(json2);

                    CookieApplication appCookie = (CookieApplication) appCtx;
                    List<Cookie> cookies = appCookie.getCookie();

                    StringEntity se = new StringEntity(jsonArray.toString(),"utf-8");
                    request.setEntity(se);

                    //set http header cookie information
                    request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());

                    HttpResponse response = new DefaultHttpClient().execute(request);;
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200){
                        result = EntityUtils.toString(response.getEntity());

                    }
                }
                catch(ClientProtocolException e){
                    e.printStackTrace();
                    result = "ClientProtocolException:network is not available";
                }
                catch(IOException e) {
                    e.printStackTrace();
                    result = "IOException:network is not available";
                }

                if (result.equals("TransactionFail")){
                    QRflag = 1;
                }
                else if (result.equals("MoneyNotEnough")) {
                    QRflag = 2;
                }
                else{
                    QRflag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean params) {
            if(QRflag == 3){
                Toast.makeText(act, "Face-to-face transaction is done.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
