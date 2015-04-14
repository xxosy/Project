package com.example.youngwon.electronictester;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.youngwon.electronictester.view.LineChart;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private LineChart mLineChart;
    private ExpandableListView mListView;
    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent = null;
    private BaseExpandableAdapter mBaseExpandableAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OptionList optionList = new OptionList(MainActivity.this);
        DrawerLayout drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
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

        mBaseExpandableAdapter = new BaseExpandableAdapter(MainActivity.this, mGroupList, mChildList);
        mListView.setAdapter(mBaseExpandableAdapter);

        mListView.setGroupIndicator(null);

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Toast.makeText(MainActivity.this, "g click = " + groupPosition,
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
                Toast.makeText(MainActivity.this, "c click = " + childPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(MainActivity.this, "g Collapse = " + groupPosition,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(MainActivity.this, "g Expand = " + groupPosition,
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
