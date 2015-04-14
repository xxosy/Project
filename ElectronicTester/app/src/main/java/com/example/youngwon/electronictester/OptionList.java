package com.example.youngwon.electronictester;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by YoungWon on 2015-04-06.
 */
public class OptionList extends LinearLayout {
    private RelativeLayout relativeLayout;
    private LayoutInflater inflater;
    private ExpandableListView mListView;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent = null;
    private BaseExpandableAdapter mBaseExpandableAdapter = null;
    private Context context;
    private String[] mainItem = new String[]{"DCV", "DCV(NULL)", "DC10A(+)", "Ω"};
    private String[] subItem = new String[]{"2.5", "10", "50", "250", "1000"};
    FrameLayout mFrameLayout;
    FrameLayout mFrameLayout1;
    Animation anim;

        public OptionList(Context mContext) {
            super(mContext);
            this.context = mContext;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_test_expandable_list_view, null);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(relativeLayout);
            setLayout();

            mGroupList = new ArrayList<String>();
            mChildList = new ArrayList<ArrayList<String>>();
            mChildListContent = new ArrayList<String>();

            mGroupList.add("DCV");
            mGroupList.add("DCV(NULL)");
            mGroupList.add("DC10A(+)");

            mChildListContent.add("10");
            mChildListContent.add("50");
            mChildListContent.add("250");

            mChildList.add(mChildListContent);
            mChildList.add(mChildListContent);
            mChildList.add(mChildListContent);

            mBaseExpandableAdapter = new BaseExpandableAdapter(context, mGroupList, mChildList);
            mListView.setAdapter(mBaseExpandableAdapter);

            mListView.setGroupIndicator(null);

            mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    Toast.makeText(context, "g click = " + groupPosition,
                            Toast.LENGTH_SHORT).show();

                    int groupCount = (int) parent.getExpandableListAdapter().getGroupCount();
                    int childCount = (int) parent.getExpandableListAdapter().getChildrenCount(groupPosition);

                    return false;
                }
            });

            mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    Toast.makeText(context, "c click = " + childPosition,
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    Toast.makeText(context, "g Collapse = " + groupPosition,
                            Toast.LENGTH_SHORT).show();
                }
            });

            mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    Toast.makeText(context, "g Expand = " + groupPosition,
                            Toast.LENGTH_SHORT).show();

                    int groupCount = mBaseExpandableAdapter.getGroupCount();

                    for (int i = 0; i < groupCount; i++) {
                        if (!(i == groupPosition))
                            mListView.collapseGroup(i);
                    }
                }
            });
        }



    private void setLayout(){
        mListView = (ExpandableListView) findViewById(R.id.elv_list);
    }

    public OptionList(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

}
