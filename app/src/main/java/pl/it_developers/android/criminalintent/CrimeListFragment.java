package pl.it_developers.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {
    public static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView crimeRecyclerView;
    private CrimeAdapter crimeAdapter;
    private boolean isSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(pl.it_developers.android.criminalintent.R.layout.fragment_crime_list, container, false);
        crimeRecyclerView = (RecyclerView) view.findViewById(pl.it_developers.android.criminalintent.R.id.crime_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            isSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(pl.it_developers.android.criminalintent.R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(pl.it_developers.android.criminalintent.R.id.show_subtitle);
        if (isSubtitleVisible) {
            subtitleItem.setTitle(pl.it_developers.android.criminalintent.R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(pl.it_developers.android.criminalintent.R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case pl.it_developers.android.criminalintent.R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case pl.it_developers.android.criminalintent.R.id.show_subtitle:
                isSubtitleVisible = !isSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, isSubtitleVisible);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(crimeAdapter);
        } else {
            crimeAdapter.setCrimes(crimes);
            crimeAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimesCount = crimeLab.getCrimes().size();
        String subtitle = getString(pl.it_developers.android.criminalintent.R.string.subtitle_format, crimesCount);

        if (!isSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTextView;
        private TextView dateTextView;
        private ImageView solvedImageView;

        private Crime crime;

        public CrimeHolder(View view) {
            super(view);
            titleTextView = (TextView) itemView.findViewById(pl.it_developers.android.criminalintent.R.id.crime_title);
            dateTextView = (TextView) itemView.findViewById(pl.it_developers.android.criminalintent.R.id.crime_date);
            solvedImageView = (ImageView) itemView.findViewById(pl.it_developers.android.criminalintent.R.id.crime_solved);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            this.crime = crime;
            titleTextView.setText(this.crime.getTitle());
            dateTextView.setText(this.crime.getDate().toString());
            dateTextView.setVisibility(View.VISIBLE);
            solvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> crimes;

        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(pl.it_developers.android.criminalintent.R.layout.list_item_crime, parent, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = crimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return crimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }
    }
}
