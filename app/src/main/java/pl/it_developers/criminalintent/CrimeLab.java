package pl.it_developers.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab crimeLab;

    private List<Crime> crimes;

    public static CrimeLab get(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context);
        }
        return crimeLab;
    }

    private CrimeLab(Context context) {
        crimes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Sprawa #" + i);
            crime.setSolved(i % 2 == 0);
            crimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return crimes;
    }

    public Crime getCrime(UUID uuid) {
        for (Crime crime : crimes) {
            if (crime.getId().equals(uuid)) {
                return crime;
            }
        }
        return null;
    }

    public int getCrimeIndex(UUID uuid) {
        for (Crime crime : crimes) {
            if (crime.getId().equals(uuid)) {
                return crimes.indexOf(crime);
            }
        }
        return -1;
    }

    public boolean isFirstCrime(UUID uuid) {
        return getCrimeIndex(uuid) == 0;
    }

    public boolean isLastCrime(UUID uuid) {
        return getCrimeIndex(uuid) == (crimes.size() - 1);
    }

    public Crime getFirstCrime() {
        return crimes.get(0);
    }

    public Crime getLastCrime() {
        return crimes.get(crimes.size() - 1);
    }
}
