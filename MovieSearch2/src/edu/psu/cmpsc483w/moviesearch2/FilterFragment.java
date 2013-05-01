package edu.psu.cmpsc483w.moviesearch2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

public class FilterFragment extends Fragment {

	private final static int MIN_RATING = 0;
	private final static int MAX_RATING = 10;
	
	// Android requires radio buttons to be direct descendants of the radiogroup, as the last item has a custom layout
	// it is necessary to implement a custom listener
	private RadioButton checkedRadio;
	
	// Allows a radio button to be unchecked if clicked after already being checked (no selection is a valid selection)
	private boolean alreadyChecked = false;
	
	Filter filter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_filter, container,
				false);
		
		setUpVoteNumberPickers(view);
		setUpCustomRadio(view);
		setUpDatePickers(view);
		
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		filter = new Filter();
	}
	
	// Sets up the date picker dialogs for the two custom date pickers
	private void setUpDatePickers(View root)
	{
		final EditText fromDateEdit = (EditText) root.findViewById(R.id.edit_filter_date_custom_lower);
		final EditText toDateEdit = (EditText) root.findViewById(R.id.edit_filter_date_custom_upper);
		
		fromDateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					
					Calendar cal = Calendar.getInstance();
					
					int year;
					int month;
					int day;
					
					setCalendar(fromDateEdit, cal);
					
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);
					
					DatePickerDialog datePickDialog = new DatePickerDialog(getActivity(), 
							new FilterDatePickerListener(fromDateEdit), year, month, day);
					datePickDialog.show();
					
					setCalendar (toDateEdit, cal);
					filter.setLowerTimeLimit(cal);
				}
				return false;
			}
			
		});
		
		toDateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					
					Calendar cal = Calendar.getInstance();
					
					int year;
					int month;
					int day;
					
					setCalendar(toDateEdit, cal);
					
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);
					
					DatePickerDialog datePickDialog = new DatePickerDialog(getActivity(), 
							new FilterDatePickerListener(toDateEdit), year, month, day);
					datePickDialog.show();
					
					setCalendar (toDateEdit, cal);
					filter.setUpperTimeLimit(cal);
				}
				return false;
			}
			
		});
		
	}
	
	private void setCalendar(EditText date, Calendar target)
	{
		String text = date.getText().toString();
		
		if (!text.equals(""))
		{
			java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
			try {
				target.setTime(dateFormat.parse(text));
			} catch (ParseException e) {
				// Use today's date instead
			}
		}
	}
	
	private class FilterDatePickerListener implements OnDateSetListener
	{
		EditText source;
		
		public FilterDatePickerListener(EditText source)
		{
			this.source = source;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
			source.setText(dateFormat.format(cal.getTime()));
		}
		
	}
	
	// Sets up the number pickers for the two editText views for vote filtering
	private void setUpVoteNumberPickers(View root)
	{
		final EditText ratingLower = (EditText) root.findViewById(R.id.edit_filter_vote_rating_lower);
		final EditText ratingUpper = (EditText) root.findViewById(R.id.edit_filter_vote_rating_upper);
		
		final Context context = getActivity().getApplicationContext();
		
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ratingLower.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					int min = MIN_RATING;
					int max = Integer.parseInt(ratingUpper.getText().toString());
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					View customView = inflater.inflate(R.layout.dialog_vote_rating, null);
					
					TextView prompt = (TextView) customView.findViewById(R.id.textview_dialog_vote_prompt);
					prompt.setText(context.getResources().getString(R.string.filter_vote_rating_lower_dialog_prompt));
					
					NumberPicker picker = (NumberPicker) customView.findViewById(R.id.numberpicker_dialog_vote);
					
					final NumberPicker replacement = replaceNumberPicker(picker);
					
					replacement.setMinValue(min);
					replacement.setMaxValue(max);
					replacement.setValue(Integer.parseInt(ratingLower.getText().toString()));
					replacement.setWrapSelectorWheel(false);
					
					builder.setTitle(context.getResources().getString(R.string.filter_vote_rating_lower_dialog_title));

					builder.setView(customView);
					
					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							ratingLower.setText(Integer.toString(replacement.getValue()));
							
							filter.setMinRating(replacement.getValue ());
						}
						
					});
					
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
					builder.create().show();
				}
				return false;
			}
			
		});
		
		
		ratingUpper.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					int min = Integer.parseInt(ratingLower.getText().toString());
					int max = MAX_RATING;
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					View customView = inflater.inflate(R.layout.dialog_vote_rating, null);
					
					TextView prompt = (TextView) customView.findViewById(R.id.textview_dialog_vote_prompt);
					prompt.setText(context.getResources().getString(R.string.filter_vote_rating_upper_dialog_prompt));
					
					NumberPicker picker = (NumberPicker) customView.findViewById(R.id.numberpicker_dialog_vote);
					
					final NumberPicker replacement = replaceNumberPicker(picker);
					
					replacement.setMinValue(min);
					replacement.setMaxValue(max);
					replacement.setValue(Integer.parseInt(ratingUpper.getText().toString()));
					replacement.setWrapSelectorWheel(false);
					
					builder.setTitle(context.getResources().getString(R.string.filter_vote_rating_upper_dialog_title));
					builder.setView(customView);
					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							ratingUpper.setText(Integer.toString(replacement.getValue()));
							
							filter.setMaxRating(replacement.getValue ());
						}
						
					});
					
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					
					builder.create().show();
				}
				return false;
			}
			
		});
	}
	
	// Sets up the radio buttons to be connected (RadioGroup doesn't work due to a lack of direct descendancy) also set up
	// radio buttons such that if a radio button is checked and clicked again, it is unchecked.
	private void setUpCustomRadio(View root)
	{
		RadioButton past_month = (RadioButton) root.findViewById(R.id.radio_filter_date_past_month);
		RadioButton past_three_month = (RadioButton) root.findViewById(R.id.radio_filter_date_past_three_month);
		RadioButton past_year = (RadioButton) root.findViewById(R.id.radio_filter_date_past_year);
		RadioButton custom = (RadioButton) root.findViewById(R.id.radio_filter_date_custom);
		
		// Create two listeners so that radio buttons can be unchecked when clicked twice and to allow indirect descendancy
		OnCheckedChangeListener radioCheckChangeListener = new OnCheckedChangeListener()
	    {
	        @Override
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	        	if (checkedRadio != null)
	        		checkedRadio.setChecked(false);
	            alreadyChecked = false;
	            checkedRadio = (RadioButton) (isChecked ? buttonView : null);
	        }
	    };
	    
	    past_month.setOnClickListener(new OnClickListener ()
	    {
	    	@Override
	    	public void onClick (View v) {
	    		if (v == checkedRadio && alreadyChecked)
	    		{
	    			checkedRadio.setChecked(false);
	    			
	    			filter.setTimeFilter(Filter.NO_FILTER);
	    		}
	    		else
	    		{
	    			alreadyChecked = true;
	    			
	    			filter.setTimeFilter(Filter.ONE_MONTH);
	    		}
	    	}
	    });
	    past_month.setOnCheckedChangeListener(radioCheckChangeListener);
	    
	    past_three_month.setOnClickListener(new OnClickListener ()
	    {
	    	@Override
	    	public void onClick (View v) {
	    		if (v == checkedRadio && alreadyChecked)
	    		{
	    			checkedRadio.setChecked(false);
	    			
	    			filter.setTimeFilter(Filter.NO_FILTER);
	    		}
	    		else
	    		{
	    			alreadyChecked = true;
	    			
	    			filter.setTimeFilter(Filter.THREE_MONTHS);
	    		}
	    	}
	    });
	    past_three_month.setOnCheckedChangeListener(radioCheckChangeListener);
	    
	    past_year.setOnClickListener(new OnClickListener ()
	    {
	    	@Override
	    	public void onClick (View v) {
	    		if (v == checkedRadio && alreadyChecked)
	    		{
	    			checkedRadio.setChecked(false);
	    			
	    			filter.setTimeFilter(Filter.NO_FILTER);
	    		}
	    		else
	    		{
	    			alreadyChecked = true;
	    			
	    			filter.setTimeFilter(Filter.ONE_YEAR);
	    		}
	    	}
	    });
	    past_year.setOnCheckedChangeListener(radioCheckChangeListener);
	    
	    custom.setOnClickListener(new OnClickListener ()
	    {
	    	@Override
	    	public void onClick (View v) {
	    		if (v == checkedRadio && alreadyChecked)
	    		{
	    			checkedRadio.setChecked(false);
	    			
	    			filter.setTimeFilter(Filter.NO_FILTER);
	    		}
	    		else
	    		{
	    			alreadyChecked = true;
	    			
	    			// TODO: Add calendars to filter
	    			filter.setTimeFilter(Filter.CUSTOM_TIME, null, null);
	    		}
	    	}
	    });
	    custom.setOnCheckedChangeListener(radioCheckChangeListener);
		
	}
	
	// There seems to be some kind of rendering bug with number pickers in alert dialogs, this is a workaround that
	// fixes it by creating a new number picker and replacing it in the view hierarchy
	private NumberPicker replaceNumberPicker(NumberPicker picker)
	{
		LayoutParams params = picker.getLayoutParams();
		
		NumberPicker replacement = new NumberPicker(getActivity());
		ViewGroup parent = (ViewGroup)picker.getParent();
		int index = parent.indexOfChild(picker);
		parent.removeView(picker);
		parent.addView(replacement,index);
		
		replacement.setLayoutParams(params);
		replacement.setId(picker.getId());
		
		return replacement;
	}

}
