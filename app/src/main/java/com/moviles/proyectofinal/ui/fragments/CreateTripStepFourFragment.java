package com.moviles.proyectofinal.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.ScheduledDestination;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.adapter.ActivityReservationAdapter;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.List;

public class CreateTripStepFourFragment extends Fragment {

    private static final String ARG_TRIP = "trip";
    private TripViewModel tripViewModel;
    private RecyclerView recyclerView;
    private Button btnBack, btnContinue;
    private MaterialCalendarView calendarView;
    private ActivityReservationAdapter adapter;

    public static CreateTripStepFourFragment newInstance(Trip trip) {
        CreateTripStepFourFragment fragment = new CreateTripStepFourFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip_step_four, container, false);

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
        if (trip.getActivityReservations() != null) {
            tripViewModel.setActivityReservations(trip.getActivityReservations());
        }
    }

    private void setupCalendarView(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangedListener((widget, date, selected) -> showActivityInputDialog(date));
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tripViewModel.getActivityReservations().observe(getViewLifecycleOwner(), activityReservations -> {
            adapter = new ActivityReservationAdapter(activityReservations, tripViewModel);
            recyclerView.setAdapter(adapter);
        });
    }

    private void setupButtons(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnContinue = view.findViewById(R.id.btn_continue);

        btnBack.setOnClickListener(v -> navigateToStepThree());

        btnContinue.setOnClickListener(v -> {
            List<ScheduledDestination> activityReservations = tripViewModel.getActivityReservations().getValue();
            if (activityReservations == null || activityReservations.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, agrega al menos una actividad antes de continuar.", Toast.LENGTH_LONG).show();
            } else {
                navigateToStepFive();
            }
        });
    }

    private void showActivityInputDialog(CalendarDay date) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_activity_input, null);
        EditText etActivityName = dialogView.findViewById(R.id.et_activity_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Agregar Actividad")
                .setView(dialogView)
                .setPositiveButton("Reservar Actividad", (dialog, which) -> {
                    String activityName = etActivityName.getText().toString().trim();
                    if (!activityName.isEmpty()) {
                        PlacePrediction activity = new PlacePrediction();
                        activity.setDescription(activityName);
                        Calendar selectedDateTime = Calendar.getInstance();
                        selectedDateTime.set(date.getYear(), date.getMonth() - 1, date.getDay());
                        tripViewModel.addActivityReservation(selectedDateTime.getTime(), activity);
                        Toast.makeText(getContext(), "Actividad reservada.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Por favor, ingrese un nombre de actividad.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToStepThree() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        CreateTripStepThreeFragment stepThreeFragment = CreateTripStepThreeFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepThreeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToStepFive() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        CreateTripStepFiveFragment stepFiveFragment = CreateTripStepFiveFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepFiveFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
