package com.example.marcin.osmtest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.marcin.osmtest.R;
import com.example.marcin.osmtest.utils.CustomListAdapter;

import java.util.ArrayList;

import static com.example.marcin.osmtest.location.NavActivity.listOfRoadNodes;

public class ItemRoadStepActivity extends AppCompatActivity
{
    ListView listView;
    Context context;
    CustomListAdapter adapter;
    ArrayList<String> listOfInstruction;
    ArrayList<Integer> directionIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_road_step);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(android.R.id.list);
        toolbar.setTitle("Lista kolejnych manewrów");
        setSupportActionBar(toolbar);
        context = this;

        listOfInstruction = new ArrayList<>();
        directionIcon = new ArrayList<>();


        for(int i=1 ; i<listOfRoadNodes.size() ; i++)
        {
            listOfInstruction.add(listOfRoadNodes.get(i).mInstructions);
            int n = listOfRoadNodes.get(i).mManeuverType;
            directionIcon.add(chooseIconForManeuverID(n));
        }

        adapter = new CustomListAdapter(this, listOfInstruction, directionIcon);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu5, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                listOfInstruction.clear();
                directionIcon.clear();
                listOfRoadNodes.clear();
                adapter.notifyDataSetChanged();
                adapter.clear();
                listView.setAdapter(null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Integer chooseIconForManeuverID(int manewr)
    {
        Integer idDirectionIcon = null;
        switch (manewr)
        {
            case 0:
            {
                idDirectionIcon =  R.drawable.empty;
                break;
            }
            case 1:
            {
                idDirectionIcon = R.drawable.straight;
                break;
            }
            case 2:
            {
                idDirectionIcon = R.drawable.straight;
                break;
            }
            case 3:
            {
                idDirectionIcon = R.drawable.slightleft;
                break;
            }
            case 4:
            {
                idDirectionIcon = R.drawable.turnleft;
                break;
            }
            case 5:
            {
                idDirectionIcon = R.drawable.sharpleft;
                break;
            }
            case 6:
            {
                idDirectionIcon = R.drawable.slightright;
                break;
            }
            case 7:
            {
                idDirectionIcon = R.drawable.turnright;
                break;
            }
            case 8:
            {
                idDirectionIcon = R.drawable.sharpright;
                break;
            }
            case 9:
            {
                idDirectionIcon = R.drawable.slightleft;
                break;
            }
            case 10:
            {
                idDirectionIcon = R.drawable.slightright;
                break;
            }
            case 11:
            {
                idDirectionIcon = R.drawable.straight;
                break;
            }
            case 12:
            {
                idDirectionIcon = R.drawable.uturn;
                break;
            }
            case 13:
            {
                idDirectionIcon = R.drawable.uturn;
                break;
            }
            case 14:
            {
                idDirectionIcon = R.drawable.uturn;
                break;
            }
            case 15:
            {
                idDirectionIcon = R.drawable.slightleft;
                break;
            }
            case 16:
            {
                idDirectionIcon = R.drawable.slightright;
                break;
            }
            case 17:
            {
                idDirectionIcon = R.drawable.slightleft;
                break;
            }
            case 18:
            {
                idDirectionIcon = R.drawable.slightright;
                break;
            }
            case 19:
            {
                idDirectionIcon = R.drawable.slightright;
                break;
            }
            case 20:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 21:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 22:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 23:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 24:
            {
                idDirectionIcon = R.drawable.arrived;
                break;
            }
            case 25:
            {
                idDirectionIcon = R.drawable.arrived;
                break;
            }
            case 26:
            {
                idDirectionIcon = R.drawable.arrived;
                break;
            }
            case 27:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 28:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 29:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 30:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 31:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 32:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 33:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 34:
            {
                idDirectionIcon = R.drawable.roundabout;
                break;
            }
            case 35:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 36:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 37:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 38:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }
            case 39:
            {
                idDirectionIcon = R.drawable.empty;
                break;
            }

        }
        return idDirectionIcon;
    }

}
