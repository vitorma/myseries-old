package br.edu.ufcg.aweseries;

import java.util.Locale;

public class LocalizationProvider {

    public String language() {
        return Locale.getDefault().getLanguage();
    }
}
