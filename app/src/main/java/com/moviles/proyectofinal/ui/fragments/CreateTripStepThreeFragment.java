package com.moviles.proyectofinal.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.HotelReservation;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.adapter.HotelReservationAdapter;
import com.moviles.proyectofinal.ui.viewmodels.LocationViewModel;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateTripStepThreeFragment extends Fragment {

    private static final String ARG_TRIP = "trip";
    private TripViewModel tripViewModel;
    private LocationViewModel locationViewModel;
    private RecyclerView recyclerView;
    private Button btnBack, btnContinue;
    private MaterialCalendarView calendarView;
    private HotelReservationAdapter adapter;
    private Date startDate, endDate;

    public static CreateTripStepThreeFragment newInstance(Trip trip) {
        CreateTripStepThreeFragment fragment = new CreateTripStepThreeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip_step_three, container, false);

        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        if (getArguments() != null && getArguments().containsKey(ARG_TRIP)) {
            Trip trip = (Trip) getArguments().getSerializable(ARG_TRIP);
            if (trip != null) {
                initializeTripViewModel(trip);
            }
        }

        setupCalendarView(view);
        setupRecyclerView(view);
        setupButtons(view);

        return view;
    }

    private void initializeTripViewModel(Trip trip) {
        if (trip.getHotelReservations() != null) {
            tripViewModel.setHotelReservations(trip.getHotelReservations());
        }
    }

    private void setupCalendarView(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangedListener((widget, date, selected) -> showDatePickerDialog(date));
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_destinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tripViewModel.getHotelReservations().observe(getViewLifecycleOwner(), hotelReservations -> {
            adapter = new HotelReservationAdapter(hotelReservations, reservation -> {
                tripViewModel.removeHotelReservation(reservation);
            });
            recyclerView.setAdapter(adapter);
        });
    }

    private void setupButtons(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnContinue = view.findViewById(R.id.btn_continue);

        btnBack.setOnClickListener(v -> navigateToStepTwo());

        btnContinue.setOnClickListener(v -> {
            List<HotelReservation> hotelReservations = tripViewModel.getHotelReservations().getValue();
            if (hotelReservations == null || hotelReservations.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, agrega al menos una reserva de hotel antes de continuar.", Toast.LENGTH_LONG).show();
            } else {
                navigateToStepFour();
            }
        });
    }

    private void showDatePickerDialog(CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> showTimePickerDialog(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    startDate = getDateTime(year, month, dayOfMonth, hourOfDay, minute);
                    showEndDatePickerDialog();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> showEndTimePickerDialog(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showEndTimePickerDialog(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    endDate = getDateTime(year, month, dayOfMonth, hourOfDay, minute);
                    showHotelPickerDialog();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private Date getDateTime(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
    }

    private void showHotelPickerDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_hotel_search, null);
        AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.actv_hotel_search);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Buscar Hotel")
                .setView(dialogView)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        autoCompleteTextView.setOnItemClickListener((parent, v, position, id) -> {
            PlacePrediction selectedHotel = (PlacePrediction) parent.getItemAtPosition(position);
            if (tripViewModel.isHotelDateTimeSlotAvailable(startDate, endDate)) {
                tripViewModel.addHotelReservation(startDate, endDate, selectedHotel);
                Toast.makeText(getContext(), "Hotel reservado.", Toast.LENGTH_SHORT).show();
                autoCompleteTextView.setText("");
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Ya hay una reserva en ese horario.", Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    locationViewModel.searchAutocompletePlaces(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        locationViewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            ArrayAdapter<PlacePrediction> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, results);
            autoCompleteTextView.setAdapter(adapter);
        });

        dialog.show();
    }

    private void navigateToStepTwo() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        CreateTripStepTwoFragment stepTwoFragment = CreateTripStepTwoFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepTwoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToStepFour() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        CreateTripStepFourFragment stepFourFragment = CreateTripStepFourFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepFourFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
