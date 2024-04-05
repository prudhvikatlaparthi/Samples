package com.sgs.citytax.ui.viewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.example.treestructure.PropTreeData;
import com.sgs.citytax.R;
import com.sgs.citytax.databinding.LayoutIconNodeBinding;
import com.sgs.citytax.ui.atv.model.TreeNode;


/**
 * Created by Sriram Vikas on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private LayoutIconNodeBinding layoutIconNodeBinding;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        layoutIconNodeBinding = DataBindingUtil.inflate(inflater, R.layout.layout_icon_node, null, false);
        final View view = layoutIconNodeBinding.getRoot();

        layoutIconNodeBinding.setVmPropTreeData(value.propTreeData);

        layoutIconNodeBinding.arrowIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_arrow));

        view.findViewById(R.id.btn_addFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode newFolder = new TreeNode(new IconTreeItem(R.string.ic_folder, "New Folder", value.propTreeData));
                getTreeView().addNode(node, newFolder);
            }
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTreeView().removeNode(node);
            }
        });

        //if My computer
        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        layoutIconNodeBinding.arrowIcon.setImageDrawable(context.getResources().getDrawable(active ? R.drawable.ic_arrow_down : R.drawable.ic_arrow));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public PropTreeData propTreeData;

        public IconTreeItem(int icon, String text, PropTreeData propTreeData) {
            this.icon = icon;
            this.text = text;
            this.propTreeData = propTreeData;
        }
    }
}
