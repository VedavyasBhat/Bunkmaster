package com.example.bunkmasterfull.options;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.bunkmasterfull.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

  
public class YesNo extends DialogPreference  
{ 
	Context con;
    public YesNo(Context context, AttributeSet attrs)   
    {  
        super(context, attrs);
        con = context;
    }
    
    @Override
    protected void onClick()
    {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		dialog.setTitle("Reset application?");
		dialog.setMessage("This action will delete all your data. Are you sure you want to continue?");
		dialog.setCancelable(true);
		dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				//reset database
				DBHelper db=new DBHelper(getContext(), null, null, 1);
				db.resetDB();
				db.close();
				
				//reset semester dates
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
				final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
				Calendar today = Calendar.getInstance();
				sp.edit().putString("semstart", sdf.format(today.getTime())).apply();
				sp.edit().putString("semend", sdf.format(today.getTime())).apply();
				
				Toast.makeText(getContext(), "Application reset!", Toast.LENGTH_SHORT).show();
			}
		});
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dlg, int which) 
			{
				dlg.cancel();
			}
		});
		
		AlertDialog al = dialog.create();
		al.show();
    }
}  