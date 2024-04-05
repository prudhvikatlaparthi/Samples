package com.sgs.citytax.util;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sgs.citytax.R;

import java.util.ArrayList;
import java.util.List;

public class SearchableDialog {
    public static <T> void showSpinnerSelectionDialog(Activity activity, List<T> data, SpinnerDialogInterface dialogInterface) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.searchable_dialog);
        dialog.setCanceledOnTouchOutside(true);
        SearchView searchView = dialog.findViewById(R.id.searchView);
        RecyclerView recyclerView = dialog.findViewById(R.id.listView);

        MySpinnerAdapter adapter = new MySpinnerAdapter(data, object -> {
            dialog.dismiss();
            dialogInterface.onItemSelected(object);
        });

        DividerItemDecoration itemDecor = new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setAdapter(adapter);

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.trim());
                return false;
            }
        });

        dialog.show();
    }

    public interface SpinnerDialogInterface {
        void onItemSelected(Object selObj);
    }

    static class MySpinnerAdapter<T> extends RecyclerView.Adapter<MySpinnerAdapter.ViewHolder> implements Filterable {
        List<T> list;
        List<T> filteredList;
        Listener listener;

        MySpinnerAdapter(List<T> list, Listener listener) {
            this.list = list;
            this.listener = listener;
            this.filteredList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchable_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(filteredList.get(position).toString());
            holder.textView.setOnClickListener(v -> listener.onItemClick(filteredList.get(position)));
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (TextUtils.isEmpty(constraint)) {
                        filteredList = list;
                    } else {
                        List<T> newFilterList = new ArrayList<T>();
                        for (T o : list) {
                            if (o.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                newFilterList.add(o);
                            }
                        }
                        filteredList = newFilterList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<T>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        interface Listener {
            void onItemClick(Object object);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}
