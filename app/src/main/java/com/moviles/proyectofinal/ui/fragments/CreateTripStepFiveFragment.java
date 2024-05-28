package com.moviles.proyectofinal.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.services.FirebaseManager;
import com.moviles.proyectofinal.ui.activities.MyTripsActivity;
import com.moviles.proyectofinal.ui.adapter.TripSummaryAdapter;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;

public class CreateTripStepFiveFragment extends Fragment {

    private static final String ARG_TRIP = "trip";
    private TripViewModel tripViewModel;
    private RecyclerView recyclerView;
    private Button btnSaveTrip, btnBack;
    private FirebaseManager firebaseManager;
    private Trip currentTrip;

    public static CreateTripStepFiveFragment newInstance(Trip trip) {
        CreateTripStepFiveFragment fragment = new CreateTripStepFiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_summary, container, false);

        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        firebaseManager = new FirebaseManager();

        if (getArguments() != null && getArguments().containsKey(ARG_TRIP)) {
            currentTrip = (Trip) getArguments().getSerializable(ARG_TRIP);
            if (currentTrip != null) {
                initializeTripViewModel(currentTrip);
            }
        }

        setupRecyclerView(view);
        setupButtons(view);

        return view;
    }

    private void initializeTripViewModel(Trip trip) {
        tripViewModel.setSelectedDestinations(trip.getSelectedDestinations());
        tripViewModel.setScheduledDestinations(trip.getScheduledDestinations());
        tripViewModel.setHotelReservations(trip.getHotelReservations());
        tripViewModel.setActivityReservations(trip.getActivityReservations());
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_summary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TripSummaryAdapter tripSummaryAdapter = new TripSummaryAdapter(tripViewModel);
        recyclerView.setAdapter(tripSummaryAdapter);
    }

    private void setupButtons(View view) {
        btnSaveTrip = view.findViewById(R.id.btn_save_trip);
        btnBack = view.findViewById(R.id.btn_back);

        if (currentTrip != null) {
            btnSaveTrip.setText("Editar Viaje");
        }

        btnSaveTrip.setOnClickListener(v -> {
            if (currentTrip == null) {
                showSaveTripDialog();
            } else {
                showEditTripDialog();
            }
        });

        btnBack.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            CreateTripStepFourFragment stepFourFragment = CreateTripStepFourFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
            transaction.replace(R.id.fragment_container, stepFourFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void showSaveTripDialog() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_trip, null);
        builder.setView(dialogView);

        EditText etTripName = dialogView.findViewById(R.id.et_trip_name);
        EditText etNumberOfPeople = dialogView.findViewById(R.id.et_number_of_people);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String tripName = etTripName.getText().toString();
            String numberOfPeopleStr = etNumberOfPeople.getText().toString();

            if (tripName.isEmpty() || numberOfPeopleStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show();
                return;
            }

            int numberOfPeople = Integer.parseInt(numberOfPeopleStr);

            firebaseManager.saveTrip(tripName, numberOfPeople, tripViewModel, new FirebaseManager.FirebaseTripCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Viaje guardado exitosamente.", Toast.LENGTH_LONG).show();
                    navigateToMyTrips();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditTripDialog() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_trip, null);
        builder.setView(dialogView);

        EditText etTripName = dialogView.findViewById(R.id.et_trip_name);
        EditText etNumberOfPeople = dialogView.findViewById(R.id.et_number_of_people);

        etTripName.setText(currentTrip.getTripName());
        etNumberOfPeople.setText(String.valueOf(currentTrip.getNumberOfPeople()));

        builder.setPositiveButton("Editar", (dialog, which) -> {
            String tripName = etTripName.getText().toString();
            String numberOfPeopleStr = etNumberOfPeople.getText().toString();

            if (tripName.isEmpty() || numberOfPeopleStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show();
                return;
            }

            int numberOfPeople = Integer.parseInt(numberOfPeopleStr);

            currentTrip.setTripName(tripName);
            currentTrip.setNumberOfPeople(numberOfPeople);

            firebaseManager.editTrip(currentTrip, new FirebaseManager.FirebaseTripCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Viaje editado exitosamente.", Toast.LENGTH_LONG).show();
                    navigateToMyTrips();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void navigateToMyTrips() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MyTripsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
