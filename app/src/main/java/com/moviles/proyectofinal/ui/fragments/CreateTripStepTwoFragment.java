package com.moviles.proyectofinal.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.ScheduledDestination;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.adapter.ScheduledDestinationAdapter;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateTripStepTwoFragment extends Fragment {

    private static final String ARG_TRIP = "trip";
    private TripViewModel tripViewModel;
    private RecyclerView recyclerView;
    private Button btnBack, btnContinue;
    private MaterialCalendarView calendarView;
    private ScheduledDestinationAdapter adapter;

    public static CreateTripStepTwoFragment newInstance(Trip trip) {
        CreateTripStepTwoFragment fragment = new CreateTripStepTwoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip_step_two, container, false);

        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

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
        if (trip.getScheduledDestinations() != null) {
            tripViewModel.setScheduledDestinations(trip.getScheduledDestinations());
        }
    }

    private void setupCalendarView(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangedListener((widget, date, selected) -> showTimePickerDialog(date));
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_destinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tripViewModel.getScheduledDestinations().observe(getViewLifecycleOwner(), scheduledDestinations -> {
            adapter = new ScheduledDestinationAdapter(scheduledDestinations, tripViewModel);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setupButtons(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnContinue = view.findViewById(R.id.btn_continue);

        btnBack.setOnClickListener(v -> navigateToStepOne());

        btnContinue.setOnClickListener(v -> {
            List<ScheduledDestination> scheduledDestinations = tripViewModel.getScheduledDestinations().getValue();
            if (scheduledDestinations == null || scheduledDestinations.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, agrega al menos un destino programado antes de continuar.", Toast.LENGTH_LONG).show();
            } else {
                navigateToStepThree();
            }
        });
    }

    private void navigateToStepThree() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        CreateTripStepThreeFragment stepThreeFragment = CreateTripStepThreeFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepThreeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showTimePickerDialog(CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> showDestinationPickerDialog(date, hourOfDay, minute),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void showDestinationPickerDialog(CalendarDay date, int hourOfDay, int minute) {
        List<PlacePrediction> selectedDestinations = tripViewModel.getSelectedDestinations().getValue();
        if (selectedDestinations == null || selectedDestinations.isEmpty()) {
            Toast.makeText(getContext(), "No hay destinos seleccionados.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] destinationNames = new String[selectedDestinations.size()];
        for (int i = 0; i < selectedDestinations.size(); i++) {
            destinationNames[i] = selectedDestinations.get(i).getDescription();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar Destino")
                .setItems(destinationNames, (dialog, which) -> {
                    PlacePrediction selectedPlace = selectedDestinations.get(which);
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(date.getYear(), date.getMonth() - 1, date.getDay(), hourOfDay, minute);

                    if (tripViewModel.isDateTimeSlotAvailable(selectedDateTime.getTime())) {
                        tripViewModel.addScheduledDestination(selectedDateTime.getTime(), selectedPlace);
                    } else {
                        Toast.makeText(getContext(), "Este espacio ya está reservado.", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void navigateToStepOne() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        CreateTripStepOneFragment stepOneFragment = CreateTripStepOneFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepOneFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
