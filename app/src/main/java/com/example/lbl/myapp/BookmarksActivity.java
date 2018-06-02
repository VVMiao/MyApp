package com.example.lbl.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private List<News> newsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Intent intent = getIntent();
        News[] news = NewsStream.readBookmarks(getApplicationContext());
        if(news != null) {
            for(News i : news){
                newsList.add(i);
            }
            setRecyclerView();
        }
        else {
            Toast.makeText(this, "收藏夹里面没有东西噢", Toast.LENGTH_LONG).show();
        }
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.bookmarks_view);
        layoutManager = new LinearLayoutManager(BookmarksActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(newsList);
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                News news = newsList.get(position);
                Intent intent = new Intent(BookmarksActivity.this, TextActivity.class);
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

    private void setLongClickView(final int position) {
        final String[] items = {"打开", "取消收藏"};
        AlertDialog longClickView = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        News news = newsList.get(position);
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(BookmarksActivity.this, TextActivity.class);
                                intent.putExtra("text_uri",news.getUri());
                                startActivity(intent);
                                break;

                            case 1:
                                newsList = NewsStream.delectBookmarks(newsList, position,getApplicationContext());
                                adapter.notifyItemRemoved(position);
                                break;

                            default:
                                break;
                        }
                    }
                }).create();
        longClickView.show();

    }
}
