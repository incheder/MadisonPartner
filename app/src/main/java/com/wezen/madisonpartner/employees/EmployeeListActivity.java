package com.wezen.madisonpartner.employees;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wezen.madisonpartner.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    public static final String EMPLOYEE_ID = "employee_id";
    private List<Employee> list;
    private EmployeesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        Toolbar toolbar = (Toolbar)findViewById(R.id.employee_list_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView recyclerViewEmployees = (RecyclerView)findViewById(R.id.recyclerViewEmployees);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewEmployees.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        adapter = new EmployeesAdapter(list,this);
        recyclerViewEmployees.setAdapter(adapter);
        getEmployees(getIntent().getExtras());
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_list, menu);
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
    }*/

    private void getEmployees(Bundle extras){
        if(extras != null){
            ArrayList<Parcelable> parcelList = getIntent().getExtras().getParcelableArrayList("employeesList");
            if(parcelList!= null){
                for (Parcelable parcelable : parcelList) {
                    Employee employee = Parcels.unwrap(parcelable);
                    if(employee!= null){
                        list.add(employee);
                    }
                }
            }
        }

        /*Employee employee =  new Employee();
        employee.setName("Tú");
        employee.setAvatarUrl("http://files.parsetfss.com/fa3a72ef-1f46-4743-8933-ab4f6c8ff56a/tfss-10e62ad0-18d8-4b9c-891e-a60e06600dfd-mcfly.jpg");
        employee.setId("100");

        for (int i = 0; i < 5; i++) {
            employee = new Employee();
            employee.setName("Empleado " + i);
            employee.setAvatarUrl("http://files.parsetfss.com/fa3a72ef-1f46-4743-8933-ab4f6c8ff56a/tfss-10e62ad0-18d8-4b9c-891e-a60e06600dfd-mcfly.jpg");
            employee.setId("" + i);
            list.add(employee);
        }*/
        adapter.notifyDataSetChanged();
    }

    public void sendData(String id){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EMPLOYEE_ID,id);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
