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
    }

    public void addCrime(Crime crime) {
        crimes.add(crime);
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

    public void deleteCrime(UUID uuid) {
        crimes.remove(getCrimeIndex(uuid));
    }
}
