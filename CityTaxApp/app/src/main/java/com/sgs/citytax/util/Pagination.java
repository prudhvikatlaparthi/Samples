package com.sgs.citytax.util;


/*
 * Created by ADIL on 20-12-2018.
 */


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Pagination {
    private static final String TAG = "Pagination";
    private int default_initialPageNumber = 1;
    private int default_defaultPageSize = 10;

    private int initialPageNumber = 1;
    //    private int lastPageNumber = -1;
    private int defaultPageSize = 10;
    private int totalRecords = 0;
    private boolean stop_pagination;
    private boolean isScrolled = false;

    private final RecyclerView recyclerView;
    private final PaginationListener paginationListener;

    public Pagination(int initialPageNumber, int defaultPageSize, RecyclerView recyclerView, PaginationListener paginationListener) {
        this.initialPageNumber = initialPageNumber;
        this.defaultPageSize = defaultPageSize;
        this.default_initialPageNumber = initialPageNumber;
        this.default_defaultPageSize = defaultPageSize;
        this.recyclerView = recyclerView;
        this.paginationListener = paginationListener;

        setListener();
    }

    public void resetInitialPageNumber() {
        this.initialPageNumber = 1;
    }
    public int getInitialPageNumber() {
        return initialPageNumber;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultValues() {
        this.initialPageNumber = default_initialPageNumber;
        this.defaultPageSize = default_defaultPageSize;
        stop_pagination = true;
        isScrolled = false;
        paginationListener.pageToCall(initialPageNumber, defaultPageSize);
    }


    private void setListener() {
        if (recyclerView == null) {
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = 0;
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3 && firstVisibleItemPosition >= 0) {
                    if (!stop_pagination && !isScrolled && totalItemCount != getTotalRecords()) {
                        initialPageNumber++;
                        isScrolled = true;
                        paginationListener.pageToCall(initialPageNumber, defaultPageSize);
                    }
                }
            }
        });
    }

    public void stopPagination(int size) {
        if (size == 0) {
            stop_pagination = true;
        } else {
            stop_pagination = size % defaultPageSize != 0 || defaultPageSize == getTotalRecords();
        }
    }

    public void setIsScrolled(boolean isScrolled) {
        this.isScrolled = isScrolled;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void doNextCall() {
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        if (totalItemCount < getTotalRecords()) {
            initialPageNumber++;
            paginationListener.pageToCall(initialPageNumber, defaultPageSize);
        } else {
            Log.d(TAG, "nextCall: finished");
        }
    }

    public void doForceNextCall() {
        initialPageNumber++;
        paginationListener.pageToCall(initialPageNumber, defaultPageSize);
    }

    public interface PaginationListener {
        void pageToCall(int pageNumber, int PageSize);
    }

}
