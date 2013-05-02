package edu.psu.cmpsc483w.moviesearch2;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
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

	// Android requires radio buttons to be direct descendants of the
	// radiogroup, as the last item has a custom layout
	// it is necessary to implement a custom listener
	private RadioButton checkedRadio;

	// Allows a radio button to be unchecked if clicked after already being
	// checked (no selection is a valid selection)
	private boolean alreadyChecked = false;

	private Filter filter;

	private ActorSearchModel exclude;

	private FilterFragmentReceiver callback;

	public interface FilterFragmentReceiver {
		public void handleFilterData(Filter filter, ActorSearchModel exclude);

		public void fragmentFinished(Filter filter, ActorSearchModel exclude);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_filter, container, false);

		this.setUpVoteNumberPickers(view);
		this.setUpCustomRadio(view);
		this.setUpDatePickers(view);
		this.setUpMiscListeners(view);

		this.setUpInterface(view, this.filter, this.exclude);

		Button close = (Button) view.findViewById(R.id.button_filter_close);

		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FilterFragment.this.callback
						.fragmentFinished(FilterFragment.this.filter,
								FilterFragment.this.exclude);
			}
		});

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			this.filter = this.getArguments().getParcelable("filter");
			this.exclude = new ActorSearchModel();
		} else {
			this.filter = savedInstanceState.getParcelable("filter");
			this.exclude = savedInstanceState.getParcelable("exclude");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("filter", this.filter);
		savedInstanceState.putParcelable("exclude", this.exclude);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.callback = (FilterFragmentReceiver) activity;
	}

	public static final FilterFragment newInstance(Filter defaultSettings) {
		FilterFragment filterFragment = new FilterFragment();

		Bundle bundle = new Bundle(1);
		bundle.putParcelable("filter", defaultSettings);
		filterFragment.setArguments(bundle);

		return filterFragment;
	}

	public Filter requestFilter() {
		return this.filter;
	}
	
	public ActorSearchModel requestActorSearchModel() {
		return this.exclude;
	}

	private void setUpInterface(View view, Filter filter,
			ActorSearchModel exclude) {
		// Set the vote fields
		EditText ratingLower = (EditText) view
				.findViewById(R.id.edit_filter_vote_rating_lower);
		EditText ratingUpper = (EditText) view
				.findViewById(R.id.edit_filter_vote_rating_upper);
		EditText voteNumber = (EditText) view
				.findViewById(R.id.edit_filter_vote_number);

		ratingLower.setText(Integer.toString(filter.getMinRating()));
		ratingUpper.setText(Integer.toString(filter.getMaxRating()));
		voteNumber.setText(Integer.toString(filter.getVoteThreshold()));

		CheckBox checkVoteRange = (CheckBox) view
				.findViewById(R.id.checkbox_filter_vote_range);
		CheckBox checkVoteNumber = (CheckBox) view
				.findViewById(R.id.checkbox_filter_vote_number);

		checkVoteRange
				.setChecked(filter.getRatingFilter() == Filter.FILTER_ENABLED);
		checkVoteNumber
				.setChecked(filter.getVoteFilter() == Filter.FILTER_ENABLED);

		// Set the date fields

		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(this.getActivity().getApplicationContext());

		EditText customDateLower = (EditText) view
				.findViewById(R.id.edit_filter_date_custom_lower);
		EditText customDateUpper = (EditText) view
				.findViewById(R.id.edit_filter_date_custom_upper);

		Calendar calCustomLower = filter.getCustomTimeLower();
		Calendar calCustomUpper = filter.getCustomTimeUpper();

		if (calCustomLower != null) {
			customDateLower.setText(dateFormat.format(filter
					.getCustomTimeLower().getTime()));
		}
		if (calCustomUpper != null) {
			customDateUpper.setText(dateFormat.format(filter
					.getCustomTimeUpper().getTime()));
		}

		int timeFilter = filter.getTimeFilter();

		switch (timeFilter) {
		case Filter.ONE_MONTH:
			RadioButton pastMonth = (RadioButton) view
					.findViewById(R.id.radio_filter_date_past_month);
			pastMonth.setChecked(true);
			break;
		case Filter.THREE_MONTHS:
			RadioButton pastThreeMonth = (RadioButton) view
					.findViewById(R.id.radio_filter_date_past_three_month);
			pastThreeMonth.setChecked(true);
			break;
		case Filter.ONE_YEAR:
			RadioButton pastYear = (RadioButton) view
					.findViewById(R.id.radio_filter_date_past_year);
			pastYear.setChecked(true);
			break;
		case Filter.CUSTOM_TIME:
			RadioButton pastCustom = (RadioButton) view
					.findViewById(R.id.radio_filter_date_custom);
			pastCustom.setChecked(true);
			break;
		}
	}

	// Sets up the date picker dialogs for the two custom date pickers
	private void setUpDatePickers(View root) {
		final EditText fromDateEdit = (EditText) root
				.findViewById(R.id.edit_filter_date_custom_lower);
		final EditText toDateEdit = (EditText) root
				.findViewById(R.id.edit_filter_date_custom_upper);

		fromDateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					Calendar cal = Calendar.getInstance();

					int year;
					int month;
					int day;

					FilterFragment.this.setCalendar(fromDateEdit, cal);

					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);

					DatePickerDialog datePickDialog = new DatePickerDialog(
							FilterFragment.this.getActivity(),
							new FilterDatePickerListener(fromDateEdit, FilterDatePickerListener.LOWER_TIME),
							year, month, day);
					datePickDialog.show();

					//FilterFragment.this.setCalendar(toDateEdit, cal);
					//FilterFragment.this.filter.setLowerTimeLimit(cal);
				}
				return false;
			}

		});

		toDateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					Calendar cal = Calendar.getInstance();

					int year;
					int month;
					int day;

					FilterFragment.this.setCalendar(toDateEdit, cal);

					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH);
					day = cal.get(Calendar.DAY_OF_MONTH);

					DatePickerDialog datePickDialog = new DatePickerDialog(
							FilterFragment.this.getActivity(),
							new FilterDatePickerListener(toDateEdit, FilterDatePickerListener.UPPER_TIME),
							year, month, day);
					datePickDialog.show();

					//FilterFragment.this.setCalendar(toDateEdit, cal);
					//FilterFragment.this.filter.setUpperTimeLimit(cal);
				}
				return false;
			}

		});

	}

	private void setCalendar(EditText date, Calendar target) {
		String text = date.getText().toString();

		if (!text.equals("")) {
			java.text.DateFormat dateFormat = android.text.format.DateFormat
					.getDateFormat(this.getActivity().getApplicationContext());
			try {
				target.setTime(dateFormat.parse(text));
			} catch (ParseException e) {
				// Use today's date instead
			}
		}
	}

	private class FilterDatePickerListener implements OnDateSetListener {
		EditText source;
		int type;
		
		public final static int LOWER_TIME = 0;
		public final static int UPPER_TIME = 1;
		
		public FilterDatePickerListener(EditText source, int type) {
			this.source = source;
			this.type = type;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			java.text.DateFormat dateFormat = android.text.format.DateFormat
					.getDateFormat(FilterFragment.this.getActivity()
							.getApplicationContext());
			this.source.setText(dateFormat.format(cal.getTime()));
			
			if (this.type == LOWER_TIME) {
				FilterFragment.this.filter.setLowerTimeLimit(cal);
			} else {
				FilterFragment.this.filter.setUpperTimeLimit(cal);
			}
			
		}

	}

	// Sets up listeners for vote number and checkbox which don't have complex
	// dialogs
	private void setUpMiscListeners(View root) {
		CheckBox ratingFilter = (CheckBox) root
				.findViewById(R.id.checkbox_filter_vote_range);
		final CheckBox numberFilter = (CheckBox) root
				.findViewById(R.id.checkbox_filter_vote_number);

		final EditText ratingLower = (EditText) root
				.findViewById(R.id.edit_filter_vote_rating_lower);
		final EditText ratingUpper = (EditText) root
				.findViewById(R.id.edit_filter_vote_rating_upper);
		final EditText voteNumber = (EditText) root
				.findViewById(R.id.edit_filter_vote_number);

		ratingFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					FilterFragment.this.filter.enableRatingFilter(
							Integer.valueOf(ratingLower.getText().toString()),
							Integer.valueOf(ratingUpper.getText().toString()));
				} else {
					FilterFragment.this.filter.disableRatingFilter();
				}
			}
		});

		numberFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					FilterFragment.this.filter.enableVoteFilter(Integer
							.valueOf(voteNumber.getText().toString()));
				} else {
					FilterFragment.this.filter.disableVoteFilter();
				}
			}
		});

		voteNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				boolean isChecked = numberFilter.isChecked();
				if (isChecked) {
					FilterFragment.this.filter.enableVoteFilter(Integer
							.valueOf(voteNumber.getText().toString()));
				}

			}
		});
	}

	// Sets up the number pickers for the two editText views for vote filtering
	private void setUpVoteNumberPickers(View root) {
		final EditText ratingLower = (EditText) root
				.findViewById(R.id.edit_filter_vote_rating_lower);
		final EditText ratingUpper = (EditText) root
				.findViewById(R.id.edit_filter_vote_rating_upper);

		final Context context = this.getActivity().getApplicationContext();

		final LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ratingLower.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int min = MIN_RATING;
					int max = Integer
							.parseInt(ratingUpper.getText().toString());

					AlertDialog.Builder builder = new AlertDialog.Builder(
							FilterFragment.this.getActivity());

					View customView = inflater.inflate(
							R.layout.dialog_vote_rating, null);

					TextView prompt = (TextView) customView
							.findViewById(R.id.textview_dialog_vote_prompt);
					prompt.setText(context.getResources().getString(
							R.string.filter_vote_rating_lower_dialog_prompt));

					NumberPicker picker = (NumberPicker) customView
							.findViewById(R.id.numberpicker_dialog_vote);

					final NumberPicker replacement = FilterFragment.this
							.replaceNumberPicker(picker);

					replacement.setMinValue(min);
					replacement.setMaxValue(max);
					replacement.setValue(Integer.parseInt(ratingLower.getText()
							.toString()));
					replacement.setWrapSelectorWheel(false);

					builder.setTitle(context.getResources().getString(
							R.string.filter_vote_rating_lower_dialog_title));

					builder.setView(customView);

					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									ratingLower.setText(Integer
											.toString(replacement.getValue()));

									FilterFragment.this.filter
											.setMinRating(replacement
													.getValue());
								}

							});

					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

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
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int min = Integer
							.parseInt(ratingLower.getText().toString());
					int max = MAX_RATING;

					AlertDialog.Builder builder = new AlertDialog.Builder(
							FilterFragment.this.getActivity());

					View customView = inflater.inflate(
							R.layout.dialog_vote_rating, null);

					TextView prompt = (TextView) customView
							.findViewById(R.id.textview_dialog_vote_prompt);
					prompt.setText(context.getResources().getString(
							R.string.filter_vote_rating_upper_dialog_prompt));

					NumberPicker picker = (NumberPicker) customView
							.findViewById(R.id.numberpicker_dialog_vote);

					final NumberPicker replacement = FilterFragment.this
							.replaceNumberPicker(picker);

					replacement.setMinValue(min);
					replacement.setMaxValue(max);
					replacement.setValue(Integer.parseInt(ratingUpper.getText()
							.toString()));
					replacement.setWrapSelectorWheel(false);

					builder.setTitle(context.getResources().getString(
							R.string.filter_vote_rating_upper_dialog_title));
					builder.setView(customView);
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									ratingUpper.setText(Integer
											.toString(replacement.getValue()));

									FilterFragment.this.filter
											.setMaxRating(replacement
													.getValue());
								}

							});

					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					builder.create().show();
				}
				return false;
			}

		});
	}

	// Sets up the radio buttons to be connected (RadioGroup doesn't work due to
	// a lack of direct descendancy) also set up
	// radio buttons such that if a radio button is checked and clicked again,
	// it is unchecked.
	private void setUpCustomRadio(View root) {
		RadioButton past_month = (RadioButton) root
				.findViewById(R.id.radio_filter_date_past_month);
		RadioButton past_three_month = (RadioButton) root
				.findViewById(R.id.radio_filter_date_past_three_month);
		RadioButton past_year = (RadioButton) root
				.findViewById(R.id.radio_filter_date_past_year);
		RadioButton custom = (RadioButton) root
				.findViewById(R.id.radio_filter_date_custom);

		// Create two listeners so that radio buttons can be unchecked when
		// clicked twice and to allow indirect descendancy
		OnCheckedChangeListener radioCheckChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (FilterFragment.this.checkedRadio != null) {
					FilterFragment.this.checkedRadio.setChecked(false);
				}
				FilterFragment.this.alreadyChecked = false;
				FilterFragment.this.checkedRadio = (RadioButton) (isChecked ? buttonView
						: null);
			}
		};

		past_month.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((v == FilterFragment.this.checkedRadio)
						&& FilterFragment.this.alreadyChecked) {
					FilterFragment.this.checkedRadio.setChecked(false);

					FilterFragment.this.filter.setTimeFilter(Filter.NO_FILTER);
				} else {
					FilterFragment.this.alreadyChecked = true;

					FilterFragment.this.filter.setTimeFilter(Filter.ONE_MONTH);
				}
			}
		});
		past_month.setOnCheckedChangeListener(radioCheckChangeListener);

		past_three_month.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((v == FilterFragment.this.checkedRadio)
						&& FilterFragment.this.alreadyChecked) {
					FilterFragment.this.checkedRadio.setChecked(false);

					FilterFragment.this.filter.setTimeFilter(Filter.NO_FILTER);
				} else {
					FilterFragment.this.alreadyChecked = true;

					FilterFragment.this.filter
							.setTimeFilter(Filter.THREE_MONTHS);
				}
			}
		});
		past_three_month.setOnCheckedChangeListener(radioCheckChangeListener);

		past_year.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((v == FilterFragment.this.checkedRadio)
						&& FilterFragment.this.alreadyChecked) {
					FilterFragment.this.checkedRadio.setChecked(false);

					FilterFragment.this.filter.setTimeFilter(Filter.NO_FILTER);
				} else {
					FilterFragment.this.alreadyChecked = true;

					FilterFragment.this.filter.setTimeFilter(Filter.ONE_YEAR);
				}
			}
		});
		past_year.setOnCheckedChangeListener(radioCheckChangeListener);

		custom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((v == FilterFragment.this.checkedRadio)
						&& FilterFragment.this.alreadyChecked) {
					FilterFragment.this.checkedRadio.setChecked(false);

					FilterFragment.this.filter.setTimeFilter(Filter.NO_FILTER);
				} else {
					FilterFragment.this.alreadyChecked = true;

					// TODO: Add calendars to filter
					FilterFragment.this.filter.setTimeFilter(
							Filter.CUSTOM_TIME, null, null);
				}
			}
		});
		custom.setOnCheckedChangeListener(radioCheckChangeListener);

	}

	// There seems to be some kind of rendering bug with number pickers in alert
	// dialogs, this is a workaround that
	// fixes it by creating a new number picker and replacing it in the view
	// hierarchy
	private NumberPicker replaceNumberPicker(NumberPicker picker) {
		LayoutParams params = picker.getLayoutParams();

		NumberPicker replacement = new NumberPicker(this.getActivity());
		ViewGroup parent = (ViewGroup) picker.getParent();
		int index = parent.indexOfChild(picker);
		parent.removeView(picker);
		parent.addView(replacement, index);

		replacement.setLayoutParams(params);
		replacement.setId(picker.getId());

		return replacement;
	}

}
