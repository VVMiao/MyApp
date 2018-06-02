package com.example.lbl.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String a1 = "http://news.qq.com/";
    private String a2 = "div.Q-tpWrap";
    private String a3 = "div.Q-tpWrap";
    private String a4 = "em";
    private int maxNews = 50;
    private List<News> newsList = new ArrayList<>();
    private List<News> savaNewsList = new ArrayList<>();
    private Handler handler;
    private SearchView mSearchView;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewsAdapter adapter;
    private boolean closeThread = false;
    private boolean needFoodSafety = true;
    private String[][] foodsafety = {{"吃", "食", "舌尖", "餐", "农产品", "奶", "肉"},
            {"害", "毒", "死", "安", "安全", "违法", "检查", "监管", "问题", "整治", "残留", "销毁", "治理", "查处", "合格"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNightMode();
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        a1 = pref.getString("a1", a1);
        a2 = pref.getString("a2", a2);
        a3 = pref.getString("a3", a3);
        a4 = pref.getString("a4", a4);

        boolean isChangeNightMode = pref.getBoolean("isChangeNightMode", false);
            if(isChangeNightMode) {
                SharedPreferences.Editor editor;
                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("isChangeNightMode", false);
                editor.apply();
                initNews(false);
                setRecyclerView();
            }
            else {
                setRecyclerView();
                initNews(true);
            }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what >= 0) {
                    adapter.notifyItemChanged(msg.what);
                }
                if(msg.what == -1) {
                    Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_LONG).show();
                }
                if(msg.what == -2) {
                    adapter.notifyItemRangeChanged(0,newsList.size() - 1);
                }
            }
        };
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(newsList);
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News news = newsList.get(position);
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                intent.putExtra("text_uri",news.getUri());
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new NewsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                setLongClickView(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void seclectFoodSafetyNews(boolean need) {
        if(need) {
            int size = newsList.size();
            Log.w("text1", "" + newsList.size());
            ArrayList<Integer> numbers = new ArrayList<Integer>();
            for(int i = 0; i < size; i++) {
                String name = newsList.get(i).getName();
                for(int j = 0; j < foodsafety[0].length; j++) {
                    boolean find = false;
                    if(name.contains(foodsafety[0][j])) {
                        for(int k = 0; k < foodsafety[1].length; k++) {
                            if(name.contains(foodsafety[1][k])) {
                                numbers.add(i);
                                find = true;
                            }
                            if(find) break;
                        }
                    }
                    if(find) break;
                }
            }
            newsList.clear();
            adapter.notifyItemRangeRemoved(0, newsList.size());
            for(int i = 0; i < numbers.size(); i++) {
                newsList.add(savaNewsList.get(numbers.get(i)));
                adapter.notifyItemInserted(i);
            }
            adapter.notifyDataSetChanged();
            Log.w("text2", "" + newsList.size());
        }
        else {
            newsList.clear();
            adapter.notifyItemRangeRemoved(0,newsList.size() - 1);
            newsList.addAll(savaNewsList);
            adapter.notifyItemRangeChanged(0, newsList.size() - 1);
        }
    }

    private void seclectNewsList (String newText) {
        newsList.clear();
        adapter.notifyItemRangeRemoved(0,newsList.size() - 1);
        newsList.addAll(savaNewsList);
        adapter.notifyItemRangeChanged(0, newsList.size() - 1);
        if(!TextUtils.isEmpty(newText)) {
            int size = newsList.size();
            for(int i = 0; i < size; i++) {
                String name = newsList.get(i).getName();
                if(!name.contains(newText)) {
                    newsList.remove(i);
                    adapter.notifyItemRemoved(i);
                    i--;
                    size--;
                }
            }
        }
    }

    private void setSearchView() {
        mSearchView.setMaxWidth(900);
        mSearchView.setQueryHint("请输入搜索内容");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                seclectNewsList(newText);
                return true;
            }
        });
    }

    private void setLongClickView(final int position) {
        final String[] items = {"打开", "收藏"};
        AlertDialog longClickView = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        News news = newsList.get(position);
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                                intent.putExtra("text_uri",news.getUri());
                                startActivity(intent);
                                break;

                            case 1:
                                NewsStream.writeBookmarks(news, getApplicationContext());
                                break;

                            default:
                                break;
                        }
                    }
                }).create();
        longClickView.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            menu.findItem(R.id.menu_nightmode).setTitle("切换到夜间模式");
        }
        else {
            menu.findItem(R.id.menu_nightmode).setTitle("切换到日间模式");
        }
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        setSearchView();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(needFoodSafety) {
            menu.findItem(R.id.menu_foodsafety).setTitle("搜索食品安全相关");
        }
        else {
            menu.findItem(R.id.menu_foodsafety).setTitle("取消指定搜索");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor;
        switch (item.getItemId()) {
            case R.id.menu_bookmarks:
                Intent intent = new Intent(MainActivity.this, BookmarksActivity.class);
                startActivity(intent);
                break;

            case R.id.txnews:
                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("a1", "http://news.qq.com/");
                editor.putString("a2", "div.Q-tpWrap");
                editor.putString("a3", "div.Q-tpWrap");
                editor.putString("a4", "em");
                editor.apply();
                closeThread = true;
                recreate();
                break;

            case R.id.aiyuke:
                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("a1", "http://www.aiyuke.com/view/cate/index.htm");
                editor.putString("a2", "div.news_list_box");
                editor.putString("a3", "div.news_list_box");
                editor.putString("a4", "h1");
                editor.apply();
                closeThread = true;
                recreate();
                break;

            case R.id.customize:
                setDialogView();
                break;

            case R.id.menu_foodsafety:
                seclectFoodSafetyNews(needFoodSafety);
                needFoodSafety = !needFoodSafety;
                break;

            case R.id.menu_nightmode:
                setChangeNightMode();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setDialogView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        String c1 = pref.getString("c1", "");
        String c2 = pref.getString("c2", "");
        String c3 = pref.getString("c3", "");
        String c4 = pref.getString("c4", "");
        final EditText editurl = (EditText) view.findViewById(R.id.edit_url);
        final EditText editdiv1 = (EditText) view.findViewById(R.id.edit_div1);
        final EditText editdiv2 = (EditText) view.findViewById(R.id.edit_div2);
        final EditText editdiv3 = (EditText) view.findViewById(R.id.edit_div3);
        editurl.setText(c1);
        editdiv1.setText(c2);
        editdiv2.setText(c3);
        editdiv3.setText(c4);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("设置自定义来源")
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor;
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("a1", editurl.getText().toString());
                        editor.putString("a2", editdiv1.getText().toString());
                        editor.putString("a3", editdiv2.getText().toString());
                        editor.putString("a4", editdiv3.getText().toString());
                        editor.putString("c1", editurl.getText().toString());
                        editor.putString("c2", editdiv1.getText().toString());
                        editor.putString("c3", editdiv2.getText().toString());
                        editor.putString("c4", editdiv3.getText().toString());
                        editor.apply();
                        closeThread = true;
                        recreate();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void setChangeNightMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        SharedPreferences.Editor editor;
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        if(currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("NightMode", AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO);
        }
        editor.putBoolean("isChangeNightMode", true);
        editor.apply();
        News[] news = new News[newsList.size()];
        for(int i = 0; i < newsList.size(); i++){
            news[i] = newsList.get(i);
        }
        NewsStream.writeNew(news, getApplicationContext());
        closeThread = true;
        recreate();
    }

    private void setNightMode() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int nightMode = pref.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }


    //用Jsoup爬虫
    private void initNews(final boolean need) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if(need) {
                        //从一个URL加载一个Document对象
                        Document doc = Jsoup.connect(a1).get();
                        //选择节点
                        Elements elementsone = doc.select(a2);
                        Elements elements = elementsone.select(a3);
                        Elements picelements = elements.select("img");

//                        Log.w("666", elementsone.toString());
//                        for(int i = 0; i < elements.size(); i++){
//                            Log.w("1.", elements.get(i).select(a4).text());
//                        }

                        for(int i = 0; i < Math.min(elements.size(), maxNews); i++){
                            String uri = elements.get(i).select("a").attr("href");
                            String picuri = "";
                            if (!picelements.isEmpty()) {
                                picuri = picelements.get(i).attr("src");
                            }
                            if(!picuri.isEmpty()) {
                                if(!picuri.contains("http")) {
                                    if(picuri.substring(0, 2).equals("//")) {
                                        picuri = "http:" + picuri;
                                    }
                                    else {
                                        picuri = a1 + "/" + picuri;
                                    }
                                }
                            }
                            Bitmap bitmap;
                            MyBitmap myBitmap;
                            if(picuri.isEmpty()) {
                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
                                myBitmap = new MyBitmap(MyBitmap.getBytes(bitmap),"name");
                            }
                            else {
                                bitmap = GetPic.getImageBitmap(picuri);
                                myBitmap = new MyBitmap(MyBitmap.getBytes(bitmap),bitmap.getConfig().name());
                            }
//                            Log.w("ok", "ok");
                            News news = new News("\t" + elements.get(i).select(a4).text(), uri,
                                    myBitmap);
                            newsList.add(news);
                            savaNewsList.add(news);

                            if(closeThread) {
                                i = maxNews;
                            }

                            Message msg = new Message();
                            if(i == Math.min(elements.size(), maxNews) - 1) {
                                msg.what = -1;
                            }
                            else {
                                msg.what = i;
                            }
                            handler.sendMessage(msg);
                        }
                    }
                    else {
                        News[] news = NewsStream.readNews(getApplicationContext());
                        for(News i : news){
                            newsList.add(i);
                            savaNewsList.add(i);
                        }
                        Message msg = new Message();
                        msg.what = -2;
                        handler.sendMessage(msg);
                    }
//                    Log.w("ojbk", "ojbk");
                }
                catch(Exception e){
                }
            }
        }.start();

    }
}
