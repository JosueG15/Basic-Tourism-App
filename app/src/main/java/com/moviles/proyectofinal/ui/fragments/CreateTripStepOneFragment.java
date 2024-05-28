package com.moviles.proyectofinal.ui.fragments;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.moviles.proyectofinal.R;
import com.moviles.proyectofinal.data.entity.PlacePrediction;
import com.moviles.proyectofinal.data.entity.Trip;
import com.moviles.proyectofinal.ui.adapter.DestinationAdapter;
import com.moviles.proyectofinal.ui.viewmodels.LocationViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.moviles.proyectofinal.ui.viewmodels.TripViewModel;
import android.widget.AutoCompleteTextView;
import android.text.Editable;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class CreateTripStepOneFragment extends Fragment implements DestinationAdapter.OnDestinationRemovedListener {

    private static final String ARG_TRIP = "trip";
    private LocationViewModel locationViewModel;
    private TripViewModel tripViewModel;
    private RecyclerView recyclerView;
    private Button btnContinue, btnDiscard;
    private DestinationAdapter adapter;
    private List<PlacePrediction> selectedDestinations = new ArrayList<>();

    public static CreateTripStepOneFragment newInstance(Trip trip) {
        CreateTripStepOneFragment fragment = new CreateTripStepOneFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip_step_one, container, false);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        if (getArguments() != null && getArguments().containsKey(ARG_TRIP)) {
            Trip trip = (Trip) getArguments().getSerializable(ARG_TRIP);
            if (trip != null) {
                initializeTripViewModel(trip);
            }
        }


        setupAutoComplete(view);
        setupRecyclerView(view);
        setupButtons(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedDestinations = tripViewModel.getSelectedDestinations().getValue();
        if (selectedDestinations != null) {
            adapter.updateDestinations(selectedDestinations);
            updateButtonState();
        }
    }

    private void initializeTripViewModel(Trip trip) {
        tripViewModel.setSelectedDestinations(trip.getSelectedDestinations());
    }

    private void setupAutoComplete(View view) {
        TextInputLayout textInputLayout = view.findViewById(R.id.til_search);
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.actv_destination_search);

        locationViewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            ArrayAdapter<PlacePrediction> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, results);
            autoCompleteTextView.setAdapter(adapter);
        });

        autoCompleteTextView.setOnItemClickListener((parent, v, position, id) -> {
            PlacePrediction prediction = (PlacePrediction) parent.getItemAtPosition(position);

            boolean isAlreadyAdded = false;
            for (PlacePrediction destination : selectedDestinations) {
                if (destination.getId().equals(prediction.getId())) {
                    isAlreadyAdded = true;
                    break;
                }
            }

            if (isAlreadyAdded) {
                Toast.makeText(getContext(), "Este destino ya ha sido agregado.", Toast.LENGTH_LONG).show();
            } else {
                selectedDestinations.add(prediction);
                adapter.notifyDataSetChanged();
                updateButtonState();
                autoCompleteTextView.setText("");
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    locationViewModel.searchAutocompletePlaces(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_selected_destinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DestinationAdapter(selectedDestinations, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupButtons(View view) {
        btnContinue = view.findViewById(R.id.btn_continue);
        btnDiscard = view.findViewById(R.id.btn_discard);

        btnContinue.setEnabled(true);

        btnContinue.setOnClickListener(v -> {
            if (selectedDestinations.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, selecciona al menos un destino antes de continuar.", Toast.LENGTH_LONG).show();
            } else {
                tripViewModel.setSelectedDestinations(selectedDestinations);
                navigateToStepTwo();
            }
        });

        btnDiscard.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void navigateToStepTwo() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        CreateTripStepTwoFragment stepTwoFragment = CreateTripStepTwoFragment.newInstance(getArguments() != null ? (Trip) getArguments().getSerializable(ARG_TRIP) : null);
        transaction.replace(R.id.fragment_container, stepTwoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateButtonState() {
        if (selectedDestinations.isEmpty()) {
            btnContinue.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        } else {
            btnContinue.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onDestinationRemoved() {
        updateButtonState();
    }
}
