package br.edu.ufcg.aweseries;

import java.util.Locale;

public class AndroidLocalizationProvider implements LocalizationProvider {

    @Override
    public String language() {
        return Locale.getDefault().getLanguage();
    }
}
