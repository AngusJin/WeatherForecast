package com.sitan.weatherforecast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBManager dbMgr;
    private FavoDBManager favoDBMgr;
    private WeatherInfo info;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private CircleProgress progress;
    private CardView forecastView;
    private TextView parentCity, city, wendu, weather_shidu, quality, date, lastUpdateTime, aqi, pm25, pm10, fx_fl, sunrise, sunset, notice;
    private ProgressBar progressBar1, progressBar2;
    private ImageView todayImg;
    private List<TextView> weeks = new ArrayList<>();
    private List<TextView> ymds = new ArrayList<>();
    private List<ImageView> imgs = new ArrayList<>();
    private List<TextView> highs = new ArrayList<>();
    private List<TextView> lows = new ArrayList<>();
    private SharedPreferences sharedPrefs;
    private static final String FILENAME = "weatherInfoCache";
    private LinearLayout mainView;
    private Button like;
    private ListView favoritesList;
    private ArrayAdapter<String> adapter;
    private List<String> list;
    private Window mWindow;
    private ListView parentCityList;
    private ArrayAdapter<String> adapter2;
    private List<String> list2;
    private Window mWindow2;
    private ListView cityList;
    private ArrayAdapter<String> adapter3;
    private List<String> list3;
    private Window mWindow3;
    private PopupWindow window1, window2;
    private Button closeWindow2;
    private TextView netTip;
    private ScrollView scrollView;
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (searchView != null) {
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
            }
            if (window1 != null)
                window1.dismiss();
            if (window2 != null)
                window2.dismiss();
            if (secondTime - firstTime < 2000) {
                System.exit(0);
            } else {
                Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initSearchView(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.Search);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (window1 != null)
                    window1.dismiss();
                if (window2 != null)
                    window2.dismiss();
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "!!!!!!", Toast.LENGTH_LONG).show();
                if (!dbMgr.isDBNull()) {
                    //hideInputMethod(MainActivity.this);
//                    mWindow = getWindow();
//                    WindowManager.LayoutParams params = mWindow.getAttributes();
//                    params.alpha = 0.6f;
//                    mWindow.setAttributes(params);

                    final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.parent_city_list, null);
                    parentCityList = v.findViewById(R.id.parentCityList);
                    list2 = new ArrayList<>();
                    adapter2 = new ArrayAdapter<>(MainActivity.this, R.layout.parent_city_items, dbMgr.getParentCityList());
                    parentCityList.setAdapter(adapter2);
                    window1 = new PopupWindow(v, 900, 990, true);
                    //window1.setO
                    window1.setAnimationStyle(android.R.style.Animation_Dialog);
                    //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //window1.setOutsideTouchable(true);
                    window1.setFocusable(false);
                    searchView.setFocusable(true);
                    window1.setTouchable(true);
                    final View mainView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
                    window1.showAtLocation(mainView, Gravity.CENTER | Gravity.TOP, 0, 180);

//                    window1.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            if (mWindow != null) {
//                                WindowManager.LayoutParams params = mWindow.getAttributes();
//                                params.alpha = 1.0f;
//                                mWindow.setAttributes(params);
//                            }
//                        }
//                    });

                    parentCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //hideInputMethod(MainActivity.this);
                            final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.city_list, null);
                            cityList = v.findViewById(R.id.cityList);
                            //list3 = new ArrayList<>();
                            list3 = dbMgr.getCityList(i + 1);
                            adapter3 = new ArrayAdapter<>(MainActivity.this, R.layout.city_items, dbMgr.getCityList(i + 1));
                            cityList.setAdapter(adapter3);
                            window2 = new PopupWindow(v, 900, 990, true);
                            window2.setAnimationStyle(android.R.style.Animation_Dialog);
                            //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            //window2.setOutsideTouchable(true);
                            window2.setFocusable(false);
                            searchView.setFocusable(true);
                            window2.setTouchable(true);
                            final View mainView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
                            window2.showAtLocation(mainView, Gravity.CENTER | Gravity.TOP, 0, 180);
                            closeWindow2 = v.findViewById(R.id.closeWindow2);
                            closeWindow2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    window2.dismiss();
                                }
                            });
                            cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    //dbMgr.getCityList(i + 1);
                                    //北京市\t\t\t\t101010100
                                    int length = list3.get(i).length();
                                    String cityCode = list3.get(i).substring(length - 9, length);
                                    if (isNetOk()) {
                                        SendRequest request = new SendRequest(cityCode);
                                        Thread thread = new Thread(request);
                                        thread.start();
                                        try {
                                            thread.join();
                                            info = request.getInfo();
                                            cacheWeatherInfo(info);
                                            setMainView();
                                            if (window1 != null)
                                                window1.dismiss();
                                            if (window2 != null)
                                                window2.dismiss();
                                            //searchView.setQuery("", false);
                                            searchView.clearFocus();
                                            searchView.onActionViewCollapsed();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "暂无可用网络 请检查网络连接！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

//                            SendRequest request = new SendRequest(favoDBMgr.getCityCode(i + 1));
//                            Thread thread = new Thread(request);
//                            thread.start();
//                            try {
//                                thread.join();
//                                info = request.getInfo();
//                                cacheWeatherInfo(info);
//                                setMainView();
//                                window.dismiss();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "暂无城市列表！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint("请在这里输入城市ID");
        searchView.setSubmitButtonEnabled(true);
//        searchView.setOnQueryTextFocusChangeListener(new SearchView.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    searchView.onActionViewCollapsed();
//                }
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() == 9 && isValidCityId(query)) {
                    if (window1 != null)
                        window1.dismiss();
                    if (window2 != null)
                        window2.dismiss();
                    sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
                    WeatherInfo[] infoCache = new WeatherInfo[3];
                    if (sharedPrefs.getString("json1", null) != null) {
                        infoCache[0] = new Gson().fromJson(sharedPrefs.getString("json1", null), WeatherInfo.class);
                    }
                    if (sharedPrefs.getString("json2", null) != null) {
                        infoCache[1] = new Gson().fromJson(sharedPrefs.getString("json2", null), WeatherInfo.class);
                    }
                    if (sharedPrefs.getString("json3", null) != null) {
                        infoCache[2] = new Gson().fromJson(sharedPrefs.getString("json3", null), WeatherInfo.class);
                    }
                    for (WeatherInfo infoThis : infoCache) {
                        if (infoThis != null && infoThis.getCityInfo().getCitykey().equals(query)) {
                            Toast.makeText(MainActivity.this, "Using Cache!", Toast.LENGTH_LONG).show();
                            info = infoThis;//如果有缓存
                            setMainView();
                            searchView.setQuery("", false);
                            searchView.clearFocus();
                            searchView.onActionViewCollapsed();
                            return true;
                        }
                    }
                    //如果没有缓存，查完并缓存
                    if (isNetOk()) {
                        SendRequest request = new SendRequest(query);
                        Thread thread = new Thread(request);
                        thread.start();
                        try {
                            thread.join();
                            if (request.getInfo().getData() != null) {
                                info = request.getInfo();
                                cacheWeatherInfo(info);
                                setMainView();
                                Toast.makeText(MainActivity.this, "No Avaliable Cache! Query and Cached.", Toast.LENGTH_LONG).show();
                                searchView.clearFocus();
                                searchView.onActionViewCollapsed();
                                return true;
                            } else {
                                Toast.makeText(MainActivity.this, "请输入正确的城市ID！", Toast.LENGTH_LONG).show();
                                searchView.setQuery("", false);
                                return false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "暂无可用网络 请检查网络连接！", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "请输入正确的城市ID！", Toast.LENGTH_LONG).show();
                    searchView.setQuery("", false);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(MainActivity.this, "字符改变", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Favorites:
                if (!favoDBMgr.isDBNull()) {
                    if (window1 != null)
                        window1.dismiss();
                    if (window2 != null)
                        window2.dismiss();
                    hideInputMethod(this);
                    mWindow = getWindow();
                    WindowManager.LayoutParams params = mWindow.getAttributes();
                    params.alpha = 0.6f;
                    mWindow.setAttributes(params);

                    final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.favorites_list, null);
                    favoritesList = v.findViewById(R.id.favoritesList);
                    list = new ArrayList<>();
                    adapter = new ArrayAdapter<>(MainActivity.this, R.layout.favorites_items, favoDBMgr.getListTitle());
                    favoritesList.setAdapter(adapter);
                    final PopupWindow window = new PopupWindow(v, 700, 900, true);
                    window.setAnimationStyle(android.R.style.Animation_Dialog);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    window.setOutsideTouchable(false);
                    window.setTouchable(true);
                    final View mainView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
                    window.showAtLocation(mainView, Gravity.CENTER, 0, 0);

                    window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (mWindow != null) {
                                WindowManager.LayoutParams params = mWindow.getAttributes();
                                params.alpha = 1.0f;
                                mWindow.setAttributes(params);
                            }
                        }
                    });

                    favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (isNetOk()) {
                                SendRequest request = new SendRequest(favoDBMgr.getCityCode(i + 1));
                                Thread thread = new Thread(request);
                                thread.start();
                                try {
                                    thread.join();
                                    info = request.getInfo();
                                    cacheWeatherInfo(info);
                                    setMainView();
                                    window.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "暂无可用网络 请检查网络连接！", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    favoritesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("\n确认删除这条收藏？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    adapter.remove(adapter.getItem(i));
                                    favoDBMgr.delete(i + 1);
                                    list.clear();
                                    if (!favoDBMgr.isDBNull()) {
                                        list.addAll(favoDBMgr.getListTitle());
                                    }
                                    adapter.notifyDataSetChanged();
//                                    favoritesList.postInvalidate();
//                                    window.update();
                                    if (favoDBMgr.isDBNull())
                                        window.dismiss();
                                    Toast.makeText(getBaseContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                                    setMainView();
                                }
                            });
                            builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            builder.show();
                            return true;
                        }
                    });
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, "暂无收藏！", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbMgr != null)
            dbMgr.closeDB();
        if (favoDBMgr != null)
            favoDBMgr.closeDB();
//        sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
//        SharedPreferences.Editor edit = sharedPrefs.edit();
//        edit.putBoolean("firstRun", false);
//        edit.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        favoDBMgr = new FavoDBManager(this);

        todayImg = findViewById(R.id.todayImg);
        parentCity = findViewById(R.id.parentCity);
        city = findViewById(R.id.city);
        wendu = findViewById(R.id.wendu);
        weather_shidu = findViewById(R.id.weather_shidu);
        quality = findViewById(R.id.quality);
        date = findViewById(R.id.date);
        lastUpdateTime = findViewById(R.id.lastUpdateTime);
        aqi = findViewById(R.id.aqi);
        pm25 = findViewById(R.id.pm25);
        pm10 = findViewById(R.id.pm10);
        fx_fl = findViewById(R.id.fx_fl);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        notice = findViewById(R.id.notice);

        progress = findViewById(R.id.progress);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);

        forecastView = findViewById(R.id.forecast);

        mainView = findViewById(R.id.mainView);
        like = findViewById(R.id.like);

        netTip = findViewById(R.id.netTip);
        scrollView = findViewById(R.id.scrollView);

        sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPrefs.edit();


//        forecastView.setRadius(16);
//        forecastView.setCardElevation(10);
//        forecastView.setContentPadding(5, 5, 5, 5);


        Resources res = getResources();
        for (int i = 0; i < 16; i++) {
            TextView week = findViewById(res.getIdentifier("week" + i, "id", getPackageName()));
            TextView ymd = findViewById(res.getIdentifier("ymd" + i, "id", getPackageName()));
            ImageView img = findViewById(res.getIdentifier("img" + i, "id", getPackageName()));
            TextView high = findViewById(res.getIdentifier("high" + i, "id", getPackageName()));
            TextView low = findViewById(res.getIdentifier("low" + i, "id", getPackageName()));
            weeks.add(week);
            ymds.add(ymd);
            imgs.add(img);
            highs.add(high);
            lows.add(low);
        }

        if (isNetOk()) {
            SendRequest request = new SendRequest("101010100");
            Thread thread = new Thread(request);
            thread.start();
            try {
                thread.join();
                info = request.getInfo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setMainView();

            //设置like颜色
            //如果favoDB中存在该城市，则设置颜色
            if (favoDBMgr.isExist(info.getCityInfo().getCitykey()))
                like.getBackground().setColorFilter(Color.rgb(255, 242, 171), PorterDuff.Mode.MULTIPLY);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击时储存info中的city code等信息到favoDB
                    //点击时，如果已经有：不添加，且删除
                    //点击时，如果还没有：添加，且不删除
                    if (favoDBMgr.isExist(info.getCityInfo().getCitykey())) {
                        removeFavoriteCity(info);
                        like.getBackground().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY);
                        Toast.makeText(MainActivity.this, "取消收藏！", Toast.LENGTH_SHORT).show();
                    } else {
                        setNewFavoriteCity(info);
                        like.getBackground().setColorFilter(Color.rgb(255, 242, 171), PorterDuff.Mode.MULTIPLY);
                        Toast.makeText(MainActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            netTip.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
        }


        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccentCos, R.color.colorPrimaryDarkCos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetOk()) {
                    netTip.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    SendRequest request;
                    if (isNetOk() && info == null) {
                        request = new SendRequest("101010100");
                    } else
                        request = new SendRequest(info.getCityInfo().getCitykey());
                    Thread thread = new Thread(request);
                    thread.start();
                    try {
                        thread.join();
                        info = request.getInfo();
                        setMainView();
                        refreshCacheWeatherInfo(info);
                        if (favoDBMgr.isExist(info.getCityInfo().getCitykey()))
                            like.getBackground().setColorFilter(Color.rgb(255, 242, 171), PorterDuff.Mode.MULTIPLY);

                        like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //点击时储存info中的city code等信息到favoDB
                                //点击时，如果已经有：不添加，且删除
                                //点击时，如果还没有：添加，且不删除
                                if (favoDBMgr.isExist(info.getCityInfo().getCitykey())) {
                                    removeFavoriteCity(info);
                                    like.getBackground().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY);
                                    Toast.makeText(MainActivity.this, "取消收藏！", Toast.LENGTH_SHORT).show();
                                } else {
                                    setNewFavoriteCity(info);
                                    like.getBackground().setColorFilter(Color.rgb(255, 242, 171), PorterDuff.Mode.MULTIPLY);
                                    Toast.makeText(MainActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (info != null) {
                    Toast.makeText(MainActivity.this, "暂无可用网络 请检查网络连接！", Toast.LENGTH_SHORT).show();
                } else {
                    netTip.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        dbMgr = new DBManager(this);
        if (dbMgr.isDBNull())
            dbMgr.parseListIntoDB(getFromAsset("city_list.json", this));
    }

    public static String getFromAsset(String fileName, Context context) {
        StringBuilder str = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                str.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public static WeatherInfo getWeatherInfo(String json) {
        return new Gson().fromJson(json, WeatherInfo.class);
    }

    public boolean isFirstRun() {
        sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
        return sharedPrefs.getBoolean("firstRun", true);
    }

    //缓存天气信息
    public void cacheWeatherInfo(WeatherInfo info) {
        sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        if (sharedPrefs.getBoolean("writable1", true)) {
            Toast.makeText(MainActivity.this, "Cached json1.", Toast.LENGTH_LONG).show();
            edit.putString("json1", new Gson().toJson(info));
            edit.putBoolean("writable1", false);
        } else if (sharedPrefs.getBoolean("writable2", true)) {
            Toast.makeText(MainActivity.this, "Cached json2.", Toast.LENGTH_LONG).show();
            edit.putString("json2", new Gson().toJson(info));
            edit.putBoolean("writable2", false);
        } else if (sharedPrefs.getBoolean("writable3", true)) {
            Toast.makeText(MainActivity.this, "Cached json3.", Toast.LENGTH_LONG).show();
            edit.putString("json3", new Gson().toJson(info));
            edit.putBoolean("writable1", true);
            edit.putBoolean("writable2", true);
            edit.putBoolean("writable3", true);
        }
        edit.apply();
    }

    //刷新时的缓存处理
    public void refreshCacheWeatherInfo(WeatherInfo info) {
        sharedPrefs = getSharedPreferences(FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPrefs.edit();
        //判断传入info和哪一条json相同
        //如果存在相同的citycode和不同的updatetime则修改，否则cacheWeatherInfo(info)
        WeatherInfo[] infoCache = new WeatherInfo[3];
        for (int i = 0; i < 3; i++) {
            int x = i + 1;
            infoCache[i] = new Gson().fromJson(sharedPrefs.getString("json" + x, null), WeatherInfo.class);
            String cityCodeOld, updateTimeOld;
            if (infoCache[i] != null) {
                cityCodeOld = infoCache[i].getCityInfo().getCitykey();
                updateTimeOld = infoCache[i].getCityInfo().getUpdateTime();
                if (cityCodeOld.equals(info.getCityInfo().getCitykey())) {
                    //如果城市码相同
                    if (!updateTimeOld.equals(info.getCityInfo().getUpdateTime())) {
                        //如果时间不同
                        Toast.makeText(MainActivity.this, "ReCached json" + x, Toast.LENGTH_LONG).show();
                        edit.putString("json" + x, new Gson().toJson(info));
                        edit.apply();
                        break;
                    } else {
                        //如果时间相同
                        Toast.makeText(MainActivity.this, "Current data is up to date!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    //如果城市码不同
                    continue;
                }
            } else {
                cacheWeatherInfo(info);
                break;
            }
        }
    }

    public void setNewFavoriteCity(WeatherInfo info) {
//        favoDBMgr = new FavoDBManager(this);
        if (!favoDBMgr.isExist(info.getCityInfo().getCitykey()))
            favoDBMgr.insert(favoDBMgr.getNewId(), info.getCityInfo().getCitykey(), info.getCityInfo().getParent(), info.getCityInfo().getCity());
    }

    public void removeFavoriteCity(int id) {
//        favoDBMgr = new FavoDBManager(this);
        favoDBMgr.delete(id);
    }

    public void removeFavoriteCity(WeatherInfo info) {
//        favoDBMgr = new FavoDBManager(this);
        if (favoDBMgr.isExist(info.getCityInfo().getCitykey()))
            favoDBMgr.delete(info.getCityInfo().getCitykey());
    }

    public List<String> getFavoriteCityList() {
//        favoDBMgr = new FavoDBManager(this);
        return favoDBMgr.getListTitle();
    }

    public void hideInputMethod(Activity activity) {
        View a = activity.getCurrentFocus();
        if (a != null) {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //设置主界面内容
    public void setMainView() {
        todayImg.setImageResource(getImgRes(info.getData().getForecast().get(0).getType()));
        parentCity.setText(info.getCityInfo().getParent());
        city.setText(info.getCityInfo().getCity());
        wendu.setText(info.getData().getWendu() + "°");
        weather_shidu.setText(info.getData().getForecast().get(0).getType() + "\n湿度 " + info.getData().getShidu());
        quality.setText("空气 " + info.getData().getQuality());
        String ymd = info.getDate();
        date.setText(ymd.substring(0, 4) + "-" + ymd.substring(4, 6) + "-" + ymd.substring(6, 8));
        lastUpdateTime.setText("上次更新 " + info.getCityInfo().getUpdateTime());
        aqi.setText(info.getData().getForecast().get(0).getAqi() + "\n空气质量指数");
        pm25.setText("PM2.5              " + info.getData().getPm25() + " μg/m³");
        pm10.setText("PM10              " + info.getData().getPm10() + " μg/m³");
        fx_fl.setText("风向 " + info.getData().getForecast().get(0).getFx() + "\n风力 " + info.getData().getForecast().get(0).getFl());
        sunrise.setText("日出 " + info.getData().getForecast().get(0).getSunrise());
        sunset.setText("日落 " + info.getData().getForecast().get(0).getSunset());
        notice.setText(info.getData().getGanmao());
        progress.setProgress((int) Math.round(info.getData().getForecast().get(0).getAqi() / 500.0 * 100.0));
        progressBar1.setProgress((int) Math.round(info.getData().getPm25() / 250.0 * 100.0));
        progressBar2.setProgress((int) Math.round(info.getData().getPm10() / 380.0 * 100.0));

        //forecast>yesterday
        weeks.get(0).setText(info.getData().getYesterday().getWeek());
        ymds.get(0).setText(info.getData().getYesterday().getYmd().substring(5, 10));
        imgs.get(0).setImageResource(getImgRes(info.getData().getYesterday().getType()));
        String highStr = info.getData().getYesterday().getHigh();
        String lowStr = info.getData().getYesterday().getLow();
        highs.get(0).setText(highStr.substring(3, highStr.length()));
        lows.get(0).setText(lowStr.substring(3, lowStr.length()));

        //forecast>1-15
        for (int i = 1; i < 16; i++) {
            weeks.get(i).setText(info.getData().getForecast().get(i - 1).getWeek());
            ymds.get(i).setText(info.getData().getForecast().get(i - 1).getYmd().substring(5, 10));
            imgs.get(i).setImageResource(getImgRes(info.getData().getForecast().get(i - 1).getType()));
            highStr = info.getData().getForecast().get(i - 1).getHigh();
            lowStr = info.getData().getForecast().get(i - 1).getLow();
            highs.get(i).setText(highStr.substring(3, highStr.length()));
            lows.get(i).setText(lowStr.substring(3, lowStr.length()));
        }
        weeks.get(1).setText("今日");
        if (favoDBMgr.isExist(info.getCityInfo().getCitykey())) {
            //Toast.makeText(this, "存在", Toast.LENGTH_SHORT).show();
            like.getBackground().setColorFilter(Color.rgb(255, 242, 171), PorterDuff.Mode.MULTIPLY);
        } else {
            //Toast.makeText(this, "不存在", Toast.LENGTH_SHORT).show();
            like.getBackground().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.MULTIPLY);
        }
        if (parentCity.getText().equals("") && !city.getText().equals("")) {
            parentCity.setText(city.getText());
            city.setText("");
        }
    }

    //获得资源ID
    public int getImgRes(String type) {
        switch (type) {
            case "晴":
                return R.drawable.sun;
            case "多云":
                return R.drawable.partly_cloudy;
            case "小雨":
                return R.drawable.rain;
            case "阴":
                return R.drawable.clouds;
        }
        return R.drawable.sun;
    }

    //判断输入字符串是否为数字
    public boolean isValidCityId(String city_code) {
        for (int i = 0; i < city_code.length(); i++) {
            if (!Character.isDigit(city_code.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isNetOk() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }
}