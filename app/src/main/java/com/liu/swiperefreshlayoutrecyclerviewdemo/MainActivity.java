package com.liu.swiperefreshlayoutrecyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.Toast;

import com.liu.swiperefreshlayoutrecyclerviewdemo.adapter.RefreshAdapter;
import com.liu.swiperefreshlayoutrecyclerviewdemo.view.RefreshItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView       mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    List<String> mDatas = new ArrayList<>();
    private RefreshAdapter mRefreshAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initView() {

        mSwipeRefreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);

    }

    private void initData() {

        for (int i = 0; i < 10; i++) {

            mDatas.add(" Item "+i);
        }

        initRecylerView();
    }

    private void initRecylerView() {

        mRefreshAdapter = new RefreshAdapter(this,mDatas);
        mLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);



        //添加动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //添加分割线
        mRecyclerView.addItemDecoration(new RefreshItemDecoration(this,RefreshItemDecoration.VERTICAL_LIST));

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRefreshAdapter);
    }

    private void initListener() {

        initPullRefresh();

        initLoadMoreListener();

    }



    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> headDatas = new ArrayList<String>();
                        for (int i = 20; i <30 ; i++) {

                            headDatas.add("Heard Item "+i);
                        }
                        mRefreshAdapter.AddHeaderItem(headDatas);

                        //刷新完成
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "更新了 "+headDatas.size()+" 条目数据", Toast.LENGTH_SHORT).show();
                    }

                }, 3000);

            }
        });
    }

    private void initLoadMoreListener() {

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==mRefreshAdapter.getItemCount()){

                    //设置正在加载更多
                    mRefreshAdapter.changeMoreStatus(mRefreshAdapter.LOADING_MORE);

                    //改为网络请求
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //
                            List<String> footerDatas = new ArrayList<String>();
                            for (int i = 0; i< 10; i++) {

                                footerDatas.add("footer  item" + i);
                            }
                            mRefreshAdapter.AddFooterItem(footerDatas);
                            //设置回到上拉加载更多
                            mRefreshAdapter.changeMoreStatus(mRefreshAdapter.PULLUP_LOAD_MORE);
                            //没有加载更多了
                            //mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NO_LOAD_MORE);
                            Toast.makeText(MainActivity.this, "更新了 "+footerDatas.size()+" 条目数据", Toast.LENGTH_SHORT).show();
                        }
                    }, 3000);


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });

    }
}
